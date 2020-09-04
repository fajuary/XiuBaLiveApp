package com.seek.ann.db;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by chunyang on 2018/8/1.
 */

public class AnnColumn {

    public static final String TABLE_NAME = "Ann";


    public static final String SQL = "CREATE TABLE " + AnnColumn.TABLE_NAME
            + " ("
//            + AnnColumn.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + AnnColumn.MSG + " TEXT, "
            + AnnColumn.RETRY_COUNT + " INTEGER, "
            + AnnColumn.NEXT_RETRY_TIME + " LONG "
            + " )";

    public static final String ID = "rowid";
    public static final String MSG = "msg";
    public static final String RETRY_COUNT = "retry_count";
    public static final String NEXT_RETRY_TIME = "next_retry_time";


    private long id;
    private String msg;
    private int retryCount;
    private long nextRetryTime;


    private void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public long getNextRetryTime() {
        return nextRetryTime;
    }

    public void setNextRetryTime(long nextRetryTime) {
        this.nextRetryTime = nextRetryTime;
    }

    public void setContentValues(ContentValues cv) {
        cv.put(MSG, msg);
        cv.put(RETRY_COUNT, retryCount);
        cv.put(NEXT_RETRY_TIME, nextRetryTime);
    }

    public void setDataFromCursor(Cursor cursor) {
        setId(cursor.getLong(cursor.getColumnIndex(ID)));
        setMsg(cursor.getString(cursor.getColumnIndex(MSG)));
        setRetryCount(cursor.getInt(cursor.getColumnIndex(RETRY_COUNT)));
        setNextRetryTime(cursor.getLong(cursor.getColumnIndex(NEXT_RETRY_TIME)));
    }
}
