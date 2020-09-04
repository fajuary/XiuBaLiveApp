package com.seek.contacts.module;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.cy.cache.XAppCache;
import com.cy.cache.XFileCache;
import com.google.gson.Gson;
import com.http.RxHttp;
import com.http.model.HttpParams;
import com.seek.contacts.module.contacts.bean.RecommendContact;
import com.seek.db.DBDao;
import com.seek.db.column.PhoneBookColumn;
import com.seek.library.bean.Empty;
import com.seek.library.http.url.UrlConstants;
import com.seek.library.user.UserHelp;
import com.xiu8.base.auth.AuthType;
import com.xiu8.base.mvp.model.BaseModel;
import com.xiu8.logger.library.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by chunyang on 2018/6/8.
 */

public class ContactModel extends BaseModel {

    private final static int MAX_COUNT = 100;
    private final static String CACHE_NAME = "SeekContact";

    private final static String KEY_IS_LOCAL_SAVE = "key_is_local_save";// 是否本地保存
    private final static String KEY_IS_FIRST_SYNC = "key_is_first_sync";// 是否第一次同步数据
    //    private final static String KEY_IS_REQUEST_PERMISSION = "key_is_request_permission";
    private final static String KEY_IS_SYNC_OVER = "key_is_sync_over";// 是否同步完成

    private ContentResolver mContentResolver;
    private XAppCache mXAppCache;
    private XFileCache mXFileCache;
    private long mUserId;

    public ContactModel(Context context) {
        super();
        mContentResolver = context.getContentResolver();
        mUserId = UserHelp.getInstance().getUserBean().getUserId();
        mXAppCache = XAppCache.create(context, CACHE_NAME + mUserId);
        File file;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            file = context.getExternalCacheDir();
        } else {
            file = context.getCacheDir();
        }
        mXFileCache = XFileCache.get(file);
    }


    public boolean isContactSyncOver() {
        return !TextUtils.isEmpty(mXFileCache.getString(KEY_IS_SYNC_OVER + mUserId));
    }


    public Observable<List<PhoneBookColumn>> getChangeList() {
        return Observable.just(DBDao.getInstance().getTypePhoneBook()).flatMap(new Function<List<PhoneBookColumn>, ObservableSource<List<PhoneBookColumn>>>() {
            @Override
            public ObservableSource<List<PhoneBookColumn>> apply(List<PhoneBookColumn> old) throws Exception {
                List<PhoneBookColumn> newContacts = readAllContact();
                List<PhoneBookColumn> diff = new ArrayList<>(CollectionUtil.getDiffent(newContacts, old));

                Log.d("ContactModel", "newContacts:" + newContacts.size() + ",oldContacts:" + old.size() + "diffContacts:" + diff.size());
                for (PhoneBookColumn column : diff) {
                    if (newContacts.contains(column)) {
                        column.setType(1);
                    }
                    if (old.contains(column)) {
                        column.setType(0);
                    }
                }
                return Observable.just(diff);
            }
        });

    }

    /**
     * 通讯录保存本地数据库
     */
    public Observable<List<PhoneBookColumn>> saveContact() {
        return Observable.create(new ObservableOnSubscribe<List<PhoneBookColumn>>() {
            @Override
            public void subscribe(ObservableEmitter<List<PhoneBookColumn>> emitter) throws Exception {
                Log.d("ContactModel", "saveContact");
                DBDao.getInstance().delPhoneBook();
                Uri uri = Uri.parse("content://com.android.contacts/contacts");
                Cursor cursor = mContentResolver.query(uri, null,
                        null, null, null);
                try {
                    List<PhoneBookColumn> list = new ArrayList<>();
                    while (cursor.moveToNext()) {
                        //名称
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        //取得电话号码
                        String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                        String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId;

                        Cursor phone = mContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, selection, null, null);

                        if (phone == null) continue;
                        while (phone.moveToNext()) {
                            //手机号
                            String number = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            if (!TextUtils.isEmpty(number)) {

                                PhoneBookColumn seekContact = new PhoneBookColumn();
//                                seekContact.setId(contactId);
                                seekContact.setName(name);
                                seekContact.setType(1);
                                //格式化手机号
                                number = number.replace("-", "");
                                number = number.replace(" ", "");
                                seekContact.setNumber(number);

                                list.add(seekContact);

                                //达到最大值发射一次
                                if (list.size() == MAX_COUNT) {
                                    emitter.onNext(new ArrayList<>(list));
                                    list.clear();
                                }
                            }
                        }
                        phone.close();
                    }
                    //结束后如果有数据在此发射
                    if (list.size() > 0) {
                        emitter.onNext(list);
                    }
                    emitter.onComplete();
                } catch (Exception e) {
                    Logger.t("ContactModel").e(Log.getStackTraceString(e));
                    emitter.onError(e);
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
            }
        }).doOnNext(new Consumer<List<PhoneBookColumn>>() {
            @Override
            public void accept(List<PhoneBookColumn> phoneBookColumns) throws Exception {
                Log.d("ContactModel", "saveContact OnNext");
                DBDao.getInstance().setPhoneBook(phoneBookColumns);
            }
        });
    }


    public List<PhoneBookColumn> readAllContact() {
        Uri uri = Uri.parse("content://com.android.contacts/contacts");
        Cursor cursor = mContentResolver.query(uri, null,
                null, null, null);
        try {
            List<PhoneBookColumn> list = new ArrayList<>();
            while (cursor.moveToNext()) {
                //名称
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                //取得电话号码
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId;

                Cursor phone = mContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, selection, null, null);

                if (phone == null) continue;
                while (phone.moveToNext()) {
                    //手机号
                    String number = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    if (!TextUtils.isEmpty(number)) {

                        PhoneBookColumn seekContact = new PhoneBookColumn();
//                                seekContact.setId(contactId);
                        seekContact.setName(name);
                        seekContact.setType(1);
                        //格式化手机号
                        number = number.replace("-", "");
                        number = number.replace(" ", "");
                        seekContact.setNumber(number);

                        list.add(seekContact);
                    }
                }
                phone.close();
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.t("ContactModel").e(Log.getStackTraceString(e));
            return null;
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public Observable<Empty> syncChangeContact() {
        return getChangeList().flatMap(new Function<List<PhoneBookColumn>, ObservableSource<Empty>>() {
            @Override
            public ObservableSource<Empty> apply(List<PhoneBookColumn> phoneBookColumns) throws Exception {
                if (phoneBookColumns.size() > MAX_COUNT) {
                    final List<PhoneBookColumn> newColumns = phoneBookColumns;
                    return Observable.create(new ObservableOnSubscribe<List<PhoneBookColumn>>() {
                        @Override
                        public void subscribe(ObservableEmitter<List<PhoneBookColumn>> emitter) throws Exception {
                            List<PhoneBookColumn> list = new ArrayList<>();

                            for (int i = 0; i < newColumns.size(); i++) {
                                list.add(newColumns.get(i));
                                if (list.size() == MAX_COUNT) {
                                    emitter.onNext(new ArrayList<>(list));
                                    list.clear();
                                }
                            }
                            if (list.size() > 0) {
                                emitter.onNext(new ArrayList<>(list));
                            }
                            emitter.onComplete();
                        }
                    }).flatMap(new Function<List<PhoneBookColumn>, ObservableSource<Empty>>() {
                        @Override
                        public ObservableSource<Empty> apply(List<PhoneBookColumn> phoneBookColumns) throws Exception {
                            return syncServiceContact(false, phoneBookColumns);
                        }
                    });
                } else {
                    return syncServiceContact(false, phoneBookColumns);
                }
            }
        });
    }


    public Observable<Empty> syncContact(boolean havePermission) {
        if (havePermission) {
            String isSyncOver = mXFileCache.getString(KEY_IS_SYNC_OVER + mUserId);
            if (TextUtils.isEmpty(isSyncOver)) {//本地缓存过去后同步数据
                return syncLocalContact();
            } else {
                return Observable.empty();
            }
        } else {
            return Observable.empty();
        }
    }

    private Observable<Empty> syncLocalContact() {
        boolean isLocalSave = mXAppCache.getBooleanValue(KEY_IS_LOCAL_SAVE + mUserId, false);
        if (isLocalSave) {
            return Observable.create(new ObservableOnSubscribe<List<PhoneBookColumn>>() {
                @Override
                public void subscribe(ObservableEmitter<List<PhoneBookColumn>> emitter) throws Exception {
                    List<PhoneBookColumn> phoneBookColumns = DBDao.getInstance().getTypePhoneBook();
                    List<PhoneBookColumn> list = new ArrayList<>();
                    if (phoneBookColumns == null || phoneBookColumns.isEmpty()) {
                        emitter.onNext(list);
                        emitter.onComplete();
                    }

                    for (int i = 0; i < phoneBookColumns.size(); i++) {
                        list.add(phoneBookColumns.get(i));
                        if (i == MAX_COUNT) {
                            emitter.onNext(new ArrayList<>(list));
                            list.clear();
                        }
                    }

                    if (list.size() > 0) {
                        emitter.onNext(list);
                    }
                    emitter.onComplete();
                }
            }).flatMap(new Function<List<PhoneBookColumn>, ObservableSource<Empty>>() {
                @Override
                public ObservableSource<Empty> apply(List<PhoneBookColumn> phoneBookColumns) throws Exception {
                    final boolean isFirstSync = mXAppCache.getBooleanValue(KEY_IS_FIRST_SYNC, false);
                    return syncServiceContact(isFirstSync, phoneBookColumns);
                }
            }).doOnComplete(new Action() {
                @Override
                public void run() throws Exception {
                    Log.d("ContactModel", "数据同步完成");
                    mXFileCache.put(KEY_IS_SYNC_OVER + mUserId, KEY_IS_SYNC_OVER, 3 * 24 * 60 * 60);
                }
            }).doOnError(new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    throwable.printStackTrace();
                    Logger.t("ContactModel").e("数据同步异常" + Log.getStackTraceString(throwable));
                }
            }).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        } else {
            return saveContact().doOnComplete(new Action() {
                @Override
                public void run() throws Exception {
                    Log.d("ContactModel", "本地保存完成");
                    mXAppCache.putBoolean(KEY_IS_LOCAL_SAVE + mUserId, true).apply();
                }
            }).flatMap(new Function<List<PhoneBookColumn>, ObservableSource<Empty>>() {
                @Override
                public ObservableSource<Empty> apply(List<PhoneBookColumn> phoneBookColumns) throws Exception {
                    final boolean isFirstSync = mXAppCache.getBooleanValue(KEY_IS_FIRST_SYNC, false);
                    return syncServiceContact(isFirstSync, phoneBookColumns);
                }
            }).doOnComplete(new Action() {
                @Override
                public void run() throws Exception {
                    Log.d("ContactModel", "数据同步完成");
                    mXFileCache.put(KEY_IS_SYNC_OVER + mUserId, KEY_IS_SYNC_OVER, 3 * 24 * 60 * 60);
                }
            }).doOnError(new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Logger.t("ContactModel").e("数据同步异常" + Log.getStackTraceString(throwable));
                }
            }).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        }
    }


    public Observable<Empty> syncServiceContact(final boolean isFirst,
                                                final List<PhoneBookColumn> mail) {
        if (!isFirst && (mail == null || mail.isEmpty())) {//临时添加
            return Observable.empty();
        }
        HttpParams params = new HttpParams();
        params.authType(AuthType.AUTH_LOGIN);
        params.put("type", isFirst ? "1" : "0");
        String postMail;
        if (mail == null || mail.isEmpty()) {
            postMail = "[]";
        } else {
//            List<PhoneBookColumn> data = new ArrayList<>();
//            data.addAll(mail);
//            data.addAll(mail);
//            data.addAll(mail);
//            data.addAll(mail);
//            data.addAll(mail);
//
//            List<PhoneBookColumn> newList = new ArrayList<>(data);
//            newList.addAll(data);
//            newList.addAll(data);
//            newList.addAll(data);
//            newList.addAll(data);
//            Log.d("ContactModel", "syncServiceContact " + newList.size());
            postMail = new Gson().toJson(mail);
        }
        params.put("mail", postMail);
        return RxHttp.post(UrlConstants.SYNC_CONTACT).params(params).execute(Empty.class).doOnNext(new Consumer<Empty>() {
            @Override
            public void accept(Empty empty) throws Exception {
                DBDao.getInstance().uploadBookFinish(mail);
                if (!isFirst)
                    mXAppCache.putBoolean(KEY_IS_FIRST_SYNC, true).apply();
            }
        });
    }


    /**
     * 通讯录推荐好友
     */
    public Observable<RecommendContact> getRecommendContact(int pageIndex) {
        HttpParams params = new HttpParams();
        params.authType(AuthType.AUTH_LOGIN);
        params.put("pageIndex", String.valueOf(pageIndex));
        return RxHttp.post(UrlConstants.RECOMMEND_CONTACT).params(params).execute(RecommendContact.class);
    }

    /**
     * 关闭通讯录推荐好友推荐
     *
     * @param friendId
     * @return
     */
    public Observable<Empty> closeContacts(long friendId) {
        HttpParams params = new HttpParams();
        params.authType(AuthType.AUTH_LOGIN);
        params.put("friendId", String.valueOf(friendId));
        return RxHttp.post(UrlConstants.RECOMMEND_CONTACT_CLOSE).params(params).execute(Empty.class);
    }

}
