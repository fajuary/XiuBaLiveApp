package com.seek.db.column;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chunyang on 2018/6/9.
 */

public class FriendColumn extends DatabaseColumn {


    public static final String TABLE_NAME = "friend";

    public static final String USER_ID = "userId";
    public static final String NICK = "NICK";
    public static final String REMARKS = "remarks";
    public static final String AVATAR = "avatar";
    public static final String SEX = "sex";
    public static final String AGE = "age";

    public void setContentValues(ContentValues cv) {
        cv.put(USER_ID, userId);
        cv.put(AVATAR, avatar);
        cv.put(NICK, nick);
        cv.put(REMARKS, remarks);
        cv.put(SEX, sex);
        cv.put(AGE, age);
    }

    public void setDataFromCursor(Cursor cursor) {
        setUserId(cursor.getLong(cursor.getColumnIndex(USER_ID)));
        setAvatar(cursor.getString(cursor.getColumnIndex(AVATAR)));
        setNick(cursor.getString(cursor.getColumnIndex(NICK)));
        setRemarks(cursor.getString(cursor.getColumnIndex(REMARKS)));
        setSex(cursor.getInt(cursor.getColumnIndex(SEX)));
        setAge(cursor.getInt(cursor.getColumnIndex(AGE)));
    }


    private long userId;
    private String nick;
    private String remarks;
    private String avatar;
    private int sex;
    private int age;

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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
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
        column.put(REMARKS, "text");
        column.put(AGE, "integer");
        column.put(SEX, "integer");
        return column;
    }

    private static final Gson gson = new Gson();

    @Override
    public String toString() {
        return gson.toJson(this);
    }

    public static FriendColumn toUserCache(String json) {
        if (TextUtils.isEmpty(json)) return null;
        return gson.fromJson(json, FriendColumn.class);
    }

    public String getName(){
        if(TextUtils.isEmpty(remarks)){
            return nick;
        }
        return remarks;
    }

}
