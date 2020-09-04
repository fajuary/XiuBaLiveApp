package com.seek.db;

import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;

import com.cy.cache.XFileCache;
import com.seek.db.column.FriendColumn;
import com.seek.db.column.FriendColumnBean;
import com.seek.db.column.FriendVersionColumn;
import com.seek.db.column.GreetColumn;
import com.seek.db.column.LoginHistoryColumn;
import com.seek.db.column.PhoneBookColumn;
import com.tencent.wcdb.Cursor;
import com.tencent.wcdb.database.SQLiteDatabase;
import com.xiu8.logger.library.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chunyang on 2018/6/9.
 */

public class DBDao {

    private Map<Long, DBHelper> mHelperMap = new HashMap<>();

    private final static String USER_CACHE = "UserCache";
    private final static int USER_VERSION = 1;
    private final static int BASE_VERSION = 1;
    private final static int FILE_VERSION = 1;
    private static Context sContext;


    private static final class Holder {
        private static final DBDao IN = new DBDao();
    }

    public static DBDao getInstance() {
        return Holder.IN;
    }

    private DBHelper mUserDB;
    private DBHelper mBaseDB;
    private XFileCache mXFileCache;

    private DBDao() {
        //基础， 命名以 seekBase
        mBaseDB = new DBHelper(sContext, "seekBase.db", BASE_VERSION);

        File cacheFile;
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            cacheFile = new File(sContext.getExternalCacheDir(), USER_CACHE);
        } else {
            cacheFile = new File(sContext.getCacheDir(), USER_CACHE);
        }
        mXFileCache = XFileCache.get(cacheFile, FILE_VERSION);
    }

    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }

    public synchronized void initUser(long userId) {
        //用户库， 命名以 userId

        mUserDB = mHelperMap.get(userId);
        Logger.t("DBDao").d("DB UserId:" + userId + " userDB:" + mUserDB == null);
        if (mUserDB == null) {
            mUserDB = new DBHelper(sContext, userId + ".db", USER_VERSION);
            mHelperMap.put(userId, mUserDB);
        }
    }

    /**
     *      =============================  打招呼功能 ===============================================
     */

    /**
     * 打招呼 数据库插入
     */
    public synchronized void greeting(GreetColumn column) {
        SQLiteDatabase db = mUserDB.getWritableDatabase();
        try {
            String userId = String.valueOf(column.getUserId());
            String sql = "select * from " + GreetColumn.TABLE_NAME + " where " + GreetColumn.USER_ID + " =?";
            Cursor cursor = db.rawQuery(sql, new String[]{userId});
            ContentValues cv = new ContentValues();
            column.setContentValues(cv);

            if (cursor.moveToNext()) {
                db.update(GreetColumn.TABLE_NAME, cv, GreetColumn.USER_ID + " =?", new String[]{userId});
            } else {
                db.insert(GreetColumn.TABLE_NAME, null, cv);
            }
        } finally {
            db.close();
        }
    }

    /**
     * 获取打招呼列表
     */
    public synchronized List<GreetColumn> getAllGreet() {
        SQLiteDatabase db = mUserDB.getReadableDatabase();
        try {
            String sql = "select * from " + GreetColumn.TABLE_NAME + " order by " + GreetColumn.IS_READ + " desc , " + GreetColumn.LAST_TIME + " desc";
            Cursor cursor = db.rawQuery(sql, null);
            List<GreetColumn> list = new ArrayList<>();
            while (cursor.moveToNext()) {
                GreetColumn greetColumn = new GreetColumn();
                greetColumn.setDataFromCursor(cursor);
                list.add(greetColumn);
            }
            return list;
        } finally {
            db.close();
        }
    }

    /**
     * 更改为已读
     */
    public synchronized void setGreetRead(long userId) {
        SQLiteDatabase db = mUserDB.getWritableDatabase();
        try {
            String key = String.valueOf(userId);
            String sql = "select * from " + GreetColumn.TABLE_NAME + " where " + GreetColumn.USER_ID + " =?";
            Cursor cursor = db.rawQuery(sql, new String[]{key});
            if (cursor.moveToNext()) {
                ContentValues cv = new ContentValues();
                cv.put(GreetColumn.IS_READ, 1);
                cv.put(GreetColumn.LAST_TIME, System.currentTimeMillis());
                db.update(GreetColumn.TABLE_NAME, cv, GreetColumn.USER_ID + " =?", new String[]{key});
            }
        } finally {
            db.close();
        }
    }


    public synchronized void setAllGreetRead() {
        SQLiteDatabase db = mUserDB.getWritableDatabase();
        try {
            String sql = "select * from " + GreetColumn.TABLE_NAME;
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                ContentValues cv = new ContentValues();
                cv.put(GreetColumn.IS_READ, 1);
                db.update(GreetColumn.TABLE_NAME, cv, null, null);
            }
        } finally {
            db.close();
        }
    }

    public synchronized void delAllGreet() {
        SQLiteDatabase db = mUserDB.getWritableDatabase();
        try {
            db.delete(GreetColumn.TABLE_NAME, null, null);
        } finally {
            db.close();
        }
    }

    public synchronized void delGreet(long userId) {
        SQLiteDatabase db = mUserDB.getWritableDatabase();
        try {
            db.delete(GreetColumn.TABLE_NAME, GreetColumn.USER_ID + "=?", new String[]{String.valueOf(userId)});
        } finally {
            db.close();
        }
    }

    /**
     * ============================== 好友列表更新版本 =========================================
     */

    public synchronized void setFriendVersion(FriendVersionColumn column) {
        SQLiteDatabase db = mUserDB.getWritableDatabase();
        try {
            Logger.t("--db").i(column.getVersion());
            String key = String.valueOf(column.getVersion());
            String sql = "select * from " + FriendVersionColumn.TABLE_NAME + " where " + FriendVersionColumn.VERSION + " =?";
            Cursor cursor = db.rawQuery(sql, new String[]{key});
            ContentValues cv = new ContentValues();
            column.setContentValues(cv);

            if (cursor.moveToNext()) {
                db.update(FriendVersionColumn.TABLE_NAME, cv, FriendVersionColumn.VERSION + " =?", new String[]{key});
            } else {
                db.insert(FriendVersionColumn.TABLE_NAME, null, cv);
            }

        } finally {
            db.close();
        }
    }


    public synchronized FriendVersionColumn getFriendsLastVersion() {
        SQLiteDatabase db = mUserDB.getReadableDatabase();
        try {
            String sql = "select * from " + FriendVersionColumn.TABLE_NAME + " order by " + FriendVersionColumn.VERSION + " desc limit 1";
            Cursor cursor = db.rawQuery(sql, null);
            Logger.t("--db").i(cursor.toString());
            if (cursor.moveToNext()) {
                FriendVersionColumn column = new FriendVersionColumn();
                column.setDataFromCursor(cursor);
                Logger.t("--db").i(column.getVersion());
                return column;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    /**
     * ============================== 用户表 =========================================
     */

    /**
     * 插入或者更新好友信息
     */
    public synchronized void setFriendInfo(FriendColumn column) {
        SQLiteDatabase db = mUserDB.getWritableDatabase();
        try {
            Logger.t("--db").i(column.toString());
            String userId = String.valueOf(column.getUserId());
            String sql = "select * from " + FriendColumn.TABLE_NAME + " where " + FriendColumn.USER_ID + " =?";
            Cursor cursor = db.rawQuery(sql, new String[]{userId});
            ContentValues cv = new ContentValues();
            column.setContentValues(cv);

            if (cursor.moveToNext()) {
                FriendColumn friendColumn = new FriendColumn();
                friendColumn.setDataFromCursor(cursor);
                Logger.t("--db").i("update" + column.toString());
                db.update(FriendColumn.TABLE_NAME, cv, FriendColumn.USER_ID + " =?", new String[]{userId});
            } else {
                Logger.t("--db").i("insert" + column.toString());
                db.insert(FriendColumn.TABLE_NAME, null, cv);
            }
        } finally {
            db.close();
        }
    }


    /**
     * 插入或者更新好友信息
     */
    public synchronized void setFriendsRemark(long id, String remark) {
        SQLiteDatabase db = mUserDB.getWritableDatabase();
        try {
            String userId = String.valueOf(id);
            String sql = "select * from " + FriendColumn.TABLE_NAME + " where " + FriendColumn.USER_ID + " =?";
            Cursor cursor = db.rawQuery(sql, new String[]{userId});
            ContentValues cv = new ContentValues();
            Logger.t("--db").i(remark);
            if (cursor.moveToNext()) {
                Logger.t("--db").i(remark);
                FriendColumn friendColumn = new FriendColumn();
                friendColumn.setDataFromCursor(cursor);
                friendColumn.setRemarks(remark);
                friendColumn.setContentValues(cv);
                db.update(FriendColumn.TABLE_NAME, cv, FriendColumn.USER_ID + " =?", new String[]{userId});
            }
        } finally {
            db.close();
        }
    }


    //获取好友列表．
    public synchronized void setFriendList(List<FriendColumn> columns) {
        if (columns == null && columns.size() == 0) {
            return;
        }
        SQLiteDatabase db = mUserDB.getWritableDatabase();
        try {

            Logger.t("--db").i("setFriendList");
            db.beginTransaction();
            FriendColumn column;
            for (int i = 0; i < columns.size(); i++) {
                column = columns.get(i);
                String userId = String.valueOf(column.getUserId());
                String sql = "select * from " + FriendColumn.TABLE_NAME + " where " + FriendColumn.USER_ID + " =?";
                Cursor cursor = db.rawQuery(sql, new String[]{userId});
                ContentValues cv = new ContentValues();
                column.setContentValues(cv);
                if (cursor.moveToNext()) {

                    db.update(FriendColumn.TABLE_NAME, cv, FriendColumn.USER_ID + " =?", new String[]{userId});
                } else {
                    db.insert(FriendColumn.TABLE_NAME, null, cv);
                }
                cursor.close();
            }
            db.setTransactionSuccessful();
            Logger.t("--db").i("setFriendList,setTransactionSuccessful()");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    //更新好友列表．
    public synchronized void updateFriendsList(List<FriendColumnBean> columns) {
        if (columns != null && columns.size() > 0) {
            SQLiteDatabase db = mUserDB.getWritableDatabase();
            try {
                db.beginTransaction();
                FriendColumnBean column;
                for (int i = 0; i < columns.size(); i++) {
                    column = columns.get(i);
                    if (column.getType() == 0) {
                        db.delete(FriendColumn.TABLE_NAME, FriendColumn.USER_ID + "=?", new String[]{String.valueOf(column.getUserId())});
                    } else {
                        String sql = "select * from " + FriendColumn.TABLE_NAME + " where " + FriendColumn.USER_ID + " =?";
                        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(column.getUserId())});
                        ContentValues cv = new ContentValues();
                        column.setContentValues(cv);
                        if (cursor.moveToNext()) {
                            db.update(FriendColumn.TABLE_NAME, cv, FriendColumn.USER_ID + "=?", new String[]{String.valueOf(column.getUserId())});
                        } else {
                            db.insert(FriendColumn.TABLE_NAME, null, cv);
                        }
                        cursor.close();
                    }
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
                db.close();
            }
        }
    }

    public synchronized void delUserFriends(long id) {
        if (id > 0) {
            SQLiteDatabase db = mUserDB.getWritableDatabase();
            try {
                db.delete(FriendColumn.TABLE_NAME, FriendColumn.USER_ID + " =? ", new String[]{String.valueOf(id)});
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.close();
            }
        }
    }

    // 获取指定用户信息 @param userId 用户 id  @return
    public synchronized FriendColumn getFriend(long userId) {
        SQLiteDatabase db = mUserDB.getReadableDatabase();
        try {
            String key = String.valueOf(userId);
            String sql = "select * from " + FriendColumn.TABLE_NAME + " where " + FriendColumn.USER_ID + " =?";

            Cursor cursor = db.rawQuery(sql, new String[]{key});
            if (cursor.moveToNext()) {
                FriendColumn column = new FriendColumn();
                column.setDataFromCursor(cursor);
                return column;
            } else {
                String userCache = mXFileCache.getString(key);
                return FriendColumn.toUserCache(userCache);
            }
        } finally {
            db.close();
        }
    }

    //获取表中所有的好友信息.
    public synchronized List<FriendColumn> getFriends() {
        SQLiteDatabase db = mUserDB.getReadableDatabase();
        try {
            String sql = "select * from " + FriendColumn.TABLE_NAME;
            Cursor cursor = db.rawQuery(sql, null);
            List<FriendColumn> list = new ArrayList<>();
            while (cursor.moveToNext()) {
                FriendColumn column = new FriendColumn();
                column.setDataFromCursor(cursor);
                list.add(column);
            }
            return list;
        } finally {
            db.close();
        }
    }

    //统计好友个数.
    public synchronized long countFriends() {
        SQLiteDatabase db = mUserDB.getReadableDatabase();
        try {
            String sql = "select count(*) from " + FriendColumn.TABLE_NAME;
            Cursor cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            long count = cursor.getLong(0);
            return count;
        } finally {
            db.close();
        }
    }

    //-------------------------------------------------------------------------------------------

    /**
     * 写入或更新登录信息
     */
    public synchronized void setLoginHistory(LoginHistoryColumn column) {
        SQLiteDatabase db = mBaseDB.getWritableDatabase();
        try {
            String userId = String.valueOf(column.getUserId());
            String sql = "select * from " + LoginHistoryColumn.TABLE_NAME + " where " + LoginHistoryColumn.USER_ID + " =?";

            Cursor cursor = db.rawQuery(sql, new String[]{userId});

            ContentValues cv = new ContentValues();
            column.setContentValues(cv);

            if (cursor.moveToNext()) {
                db.update(LoginHistoryColumn.TABLE_NAME, cv, LoginHistoryColumn.USER_ID + " =?", new String[]{userId});
            } else {
                db.insert(LoginHistoryColumn.TABLE_NAME, null, cv);
            }
        } finally {
            db.close();
        }
    }


    public synchronized LoginHistoryColumn getLastLoginHistory() {
        SQLiteDatabase db = mBaseDB.getReadableDatabase();
        try {
            String sql = "select * from " + LoginHistoryColumn.TABLE_NAME + " ORDER BY " + LoginHistoryColumn.LAST_UPDATE_TIME + " DESC ";
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                LoginHistoryColumn column = new LoginHistoryColumn();
                column.setDataFromCursor(cursor);
                return column;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    /**
     * 获取登录信息
     */
    public synchronized LoginHistoryColumn getLoginHistory(long userId) {
        SQLiteDatabase db = mBaseDB.getReadableDatabase();
        try {
            String key = String.valueOf(userId);
            String sql = "select * from " + LoginHistoryColumn.TABLE_NAME + " where " + LoginHistoryColumn.USER_ID + " =?";
            Cursor cursor = db.rawQuery(sql, new String[]{key});
            if (cursor.moveToNext()) {
                LoginHistoryColumn column = new LoginHistoryColumn();
                column.setDataFromCursor(cursor);
                return column;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }


    /**
     * ============================== 通讯录功能 =========================================
     */


    public synchronized void delPhoneBook() {
        SQLiteDatabase db = mBaseDB.getWritableDatabase();
        try {
            db.delete(PhoneBookColumn.TABLE_NAME, null, null);
        } finally {
            db.close();
        }
    }

    /**
     * 同号码，同名问题无解
     */
    public synchronized void setPhoneBook(List<PhoneBookColumn> list) {
        if (list == null || list.isEmpty()) return;
        SQLiteDatabase db = mBaseDB.getWritableDatabase();
        try {
            db.beginTransaction();
            for (PhoneBookColumn column : list) {
                String sql = "select * from " + PhoneBookColumn.TABLE_NAME + " where " + PhoneBookColumn.NUMBER + " =?";
                Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(column.getNumber())});
                ContentValues cv = new ContentValues();
                column.setContentValues(cv);

                if (cursor.moveToNext()) {
                    db.update(PhoneBookColumn.TABLE_NAME, cv, null, null);
                } else {
                    db.insert(PhoneBookColumn.TABLE_NAME, null, cv);
                }
                cursor.close();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public synchronized void uploadBookFinish(List<PhoneBookColumn> list) {
        if (list == null || list.isEmpty()) return;
        SQLiteDatabase db = mBaseDB.getWritableDatabase();
        try {
            db.beginTransaction();
            for (PhoneBookColumn column : list) {
                String sql = "select * from " + PhoneBookColumn.TABLE_NAME + " where " + PhoneBookColumn.NUMBER + " =?";
                String key = column.getNumber();
                Cursor cursor = db.rawQuery(sql, new String[]{key});
                int type = column.getType();
                if (type == 1) {
                    ContentValues cv = new ContentValues();
                    column.setContentValues(cv);
                    if (cursor.moveToNext()) {
                        db.update(PhoneBookColumn.TABLE_NAME, cv, PhoneBookColumn.NUMBER + " =?", new String[]{key});
                    } else {
                        db.insert(PhoneBookColumn.TABLE_NAME, null, cv);
                    }
                } else {
                    if (cursor.moveToNext()) {
                        db.delete(PhoneBookColumn.TABLE_NAME, PhoneBookColumn.NUMBER + " =?", new String[]{key});
                    }
                }
                cursor.close();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }


    public synchronized List<PhoneBookColumn> getTypePhoneBook() {
        SQLiteDatabase db = mBaseDB.getReadableDatabase();
        try {
            String sql = "select * from " + PhoneBookColumn.TABLE_NAME + " where " + PhoneBookColumn.TYPE + "=? or " + PhoneBookColumn.TYPE + "  =?";
            Cursor cursor = db.rawQuery(sql, new String[]{"1", "0"});
            List<PhoneBookColumn> list = new ArrayList<>();
            while (cursor.moveToNext()) {
                PhoneBookColumn column = new PhoneBookColumn();
                column.setDataFromCursor(cursor);
                list.add(column);
            }
            return list;
        } finally {
            db.close();
        }
    }


}
