package com.seek.db.column;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chunyang on 2018/6/9.
 */

public class LoginHistoryColumn extends DatabaseColumn {

    public static final String TABLE_NAME = "login_history";

    public static final String USER_ID = "userId";
    public static final String NICK = "nick";
    public static final String AVATAR = "avatar";
    public static final String MOBILE = "mobile";
    public static final String LAST_UPDATE_TIME = "lastUpdateTime";


    public void setContentValues(ContentValues cv) {
        cv.put(USER_ID, userId);
        cv.put(AVATAR, avatar);
        cv.put(NICK, nick);
        cv.put(MOBILE, mobile);
        cv.put(LAST_UPDATE_TIME, lastUpdateTime);
    }

    public void setDataFromCursor(Cursor cursor) {
        setUserId(cursor.getLong(cursor.getColumnIndex(USER_ID)));
        setAvatar(cursor.getString(cursor.getColumnIndex(AVATAR)));
        setNick(cursor.getString(cursor.getColumnIndex(NICK)));
        setMobile(cursor.getString(cursor.getColumnIndex(MOBILE)));
        setLastUpdateTime(cursor.getLong(cursor.getColumnIndex(LAST_UPDATE_TIME)));
    }


    private long userId;
    private String nick;
    private String avatar;
    private String mobile;
    private long lastUpdateTime;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected Map<String, String> getTableMap() {
        Map<String, String> column = new HashMap<String, String>();
        column.put(USER_ID, "long primary key");
        column.put(AVATAR, "text");
        column.put(NICK, "text");
        column.put(MOBILE, "text");
        column.put(LAST_UPDATE_TIME, "long");
        return column;
    }
}
