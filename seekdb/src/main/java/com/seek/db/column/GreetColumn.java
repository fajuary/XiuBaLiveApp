package com.seek.db.column;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chunyang on 2018/6/9.
 */

public class GreetColumn extends DatabaseColumn {

    public static final String TABLE_NAME = "greeting";

    public static final String USER_ID = "userId";
    public static final String AVATAR = "avatar";
    public static final String NICK = "nick";
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDES = "latitudes";
    public static final String LABEL = "label";
    public static final String IS_READ = "isRead";
    public static final String LAST_TIME = "lastTime";
    public static final String AGE = "age";
    public static final String SEX = "sex";


    public void setContentValues(ContentValues cv) {
        cv.put(USER_ID, userId);
        cv.put(AVATAR, avatar);
        cv.put(NICK, nick);
        cv.put(LONGITUDE, longitude);
        cv.put(LATITUDES, latitudes);
        cv.put(LABEL, label);
        cv.put(IS_READ, isRead ? 1 : 2);
        cv.put(LAST_TIME, lastTime);
        cv.put(AGE, age);
        cv.put(SEX, sex);
    }

    public void setDataFromCursor(Cursor cursor) {
        setUserId(cursor.getLong(cursor.getColumnIndex(USER_ID)));
        setAvatar(cursor.getString(cursor.getColumnIndex(AVATAR)));
        setNick(cursor.getString(cursor.getColumnIndex(NICK)));
        setLongitude(cursor.getDouble(cursor.getColumnIndex(LONGITUDE)));
        setLatitudes(cursor.getDouble(cursor.getColumnIndex(LATITUDES)));
        setLabel(cursor.getString(cursor.getColumnIndex(LABEL)));
        setRead(cursor.getInt(cursor.getColumnIndex(IS_READ)));
        setLastTime(cursor.getLong(cursor.getColumnIndex(LAST_TIME)));
        setSex(cursor.getInt(cursor.getColumnIndex(SEX)));
        setAge(cursor.getInt(cursor.getColumnIndex(AGE)));
    }

    private long userId;
    private String avatar;
    private String nick;
    private double longitude;
    private double latitudes;
    private String label;
    private boolean isRead;
    private long lastTime;
    private int sex;
    private int age;

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitudes() {
        return latitudes;
    }

    public void setLatitudes(double latitudes) {
        this.latitudes = latitudes;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(int read) {
        isRead = read == 1;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }


    public void setAge(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
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
        column.put(LONGITUDE, "double");
        column.put(LATITUDES, "double");
        column.put(LABEL, "text");
        column.put(IS_READ, "integer");
        column.put(LAST_TIME, "long");
        column.put(SEX, "int");
        column.put(AGE, "int");
        return column;
    }
}
