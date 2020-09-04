package com.seek.contacts.module;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;

import com.seek.library.bean.Empty;
import com.seek.library.http.SeekPObserver;
import com.xiu8.base.mvp.model.CreateModel;
import com.xiu8.base.mvp.presenter.BasePresenter;

/**
 * Created by chunyang on 2018/6/11.
 */
@CreateModel(ContactModel.class)
public class ContactPresenter<V extends ContactControl.View> extends BasePresenter<V, ContactModel> implements ContactControl.Presenter {


    private ContactModel mModel;
    private ContactsObserver mContactsObserver;
    private ContentResolver mContentResolver;
    private Handler mHandler;


    public ContactPresenter(Context context) {
        mModel = new ContactModel(context);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void registerContacts() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int what = msg.what;
                if (what == ContactsObserver.WHAT_CONTACTS_CHANGE) {
                    syncChangeList();
                }
            }
        };
        mContactsObserver = new ContactsObserver(mHandler);
        mContentResolver.registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, mContactsObserver);
    }

    @Override
    public void unregisterContentObserver() {
        if (mContactsObserver != null)
            mContentResolver.unregisterContentObserver(mContactsObserver);
    }

    @Override
    public void syncContact(boolean havePermission) {
        mModel.syncContact(havePermission).subscribe(new SeekPObserver<Empty>(this) {
            @Override
            public void onSuccess(Empty empty) {

            }
        });
    }

    @Override
    public void syncChangeList() {
        mModel.syncChangeContact().subscribe(new SeekPObserver<Empty>(this) {
            @Override
            public void onSuccess(Empty empty) {

            }
        });
    }

}

