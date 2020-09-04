package com.seek.db.column;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chunyang on 2018/6/9.
 */

public class FriendVersionColumn extends DatabaseColumn {

    public static final String TABLE_NAME = "friend_version";

    public static final String VERSION = "version";
    public static final String UPDATE_TIME = "updateTime";

    public void setContentValues(ContentValues cv) {
        cv.put(VERSION, version);
        cv.put(UPDATE_TIME, updateTime);
    }

    public void setDataFromCursor(Cursor cursor) {
        setVersion(cursor.getString(cursor.getColumnIndex(VERSION)));
        setUpdateTime(cursor.getString(cursor.getColumnIndex(UPDATE_TIME)));
    }

    private String version;
    private String updateTime;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }



    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected Map<String, String> getTableMap() {
        Map<String, String> column = new HashMap<String, String>();
        column.put(_ID, "integer primary key");
        column.put(VERSION, "text");
        column.put(UPDATE_TIME, "text");
        return column;
    }
}
