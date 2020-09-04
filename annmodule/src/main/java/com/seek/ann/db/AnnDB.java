package com.seek.ann.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.seek.ann.AnnRetryFunc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chunyang on 2018/8/1.
 */

public class AnnDB extends SQLiteOpenHelper {

    private final static String DB_NAME = "AnnDB";
    private final static int VERSION = 1;


    public AnnDB(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AnnColumn.SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public synchronized long insert(String msg) {
        AnnColumn column = new AnnColumn();
        column.setMsg(msg);
        column.setRetryCount(1);
        column.setNextRetryTime(System.currentTimeMillis() + AnnRetryFunc.FIRST_RETRY_TIME);
        ContentValues cv = new ContentValues();
        column.setContentValues(cv);
        SQLiteDatabase db = getWritableDatabase();
        try {
            return db.insert(AnnColumn.TABLE_NAME, null, cv);
        } finally {
            db.close();
        }
    }


    public synchronized void update(long id, String msg, int count) {

        SQLiteDatabase db = getWritableDatabase();
        String sql = "select * from " + AnnColumn.TABLE_NAME + " where " + AnnColumn.ID + " =?";
        String key = String.valueOf(id);
        Cursor cursor = db.rawQuery(sql, new String[]{key});
        try {

            if (cursor != null && cursor.moveToNext()) {
                ContentValues cv = new ContentValues();
                AnnColumn column = new AnnColumn();
                column.setMsg(msg);
                column.setRetryCount(count);
                if (count > 0 && count <= 10) {
                    column.setNextRetryTime(System.currentTimeMillis() + AnnRetryFunc.FIRST_RETRY_TIME);
                } else if (count > 10 && count <= 15) {
                    column.setNextRetryTime(System.currentTimeMillis() + AnnRetryFunc.LAST_RETRY_TIME);
                } else {
                    column.setNextRetryTime(System.currentTimeMillis());
                }
                column.setContentValues(cv);
                db.update(AnnColumn.TABLE_NAME, cv, AnnColumn.ID + " =?", new String[]{key});
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }


    public synchronized void del(long id) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "select * from " + AnnColumn.TABLE_NAME + " where " + AnnColumn.ID + " =?";
        String key = String.valueOf(id);
        Cursor cursor = db.rawQuery(sql, new String[]{key});
        try {
            if (cursor != null && cursor.moveToNext()) {
                db.delete(AnnColumn.TABLE_NAME, AnnColumn.ID + " =?", new String[]{key});
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }


    public synchronized List<AnnColumn> getLostAnnMsg() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select " + AnnColumn.ID + ", * from " + AnnColumn.TABLE_NAME;//+ " where " + AnnColumn.RETRY_COUNT + " <=?" + AnnColumn.NEXT_RETRY_TIME + " <=?";
        Cursor cursor = db.rawQuery(sql, null/* new String[]{"15", String.valueOf(System.currentTimeMillis())}*/);
        try {
            List<AnnColumn> list = new ArrayList<>();
            while (cursor != null && cursor.moveToNext()) {
                AnnColumn column = new AnnColumn();
                column.setDataFromCursor(cursor);
                list.add(column);
            }
            return list;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

//    public synchronized void save(String id, String msg, int count) {
//
//        AnnColumn column = new AnnColumn();
////        column.setId(id);
//        column.setMsg(msg);
//        column.setRetryCount(count);
//        if (count > 0 && count <= 10) {
//            column.setNextRetryTime(System.currentTimeMillis() + AnnRetryFunc.FIRST_RETRY_TIME);
//        } else if (count > 10 && count <= 15) {
//            column.setNextRetryTime(System.currentTimeMillis() + AnnRetryFunc.LAST_RETRY_TIME);
//        } else {
//            column.setNextRetryTime(System.currentTimeMillis());
//        }
//
//        if (count == 1) {
//
//        } else {
//
//        }
//
//        SQLiteDatabase db = getWritableDatabase();
//        String sql = "select * from " + AnnColumn.TABLE_NAME + " where " + AnnColumn.ID + " =?";
//        Cursor cursor = db.rawQuery(sql, new String[]{id});
//        try {
//            ContentValues cv = new ContentValues();
//            column.setContentValues(cv);
//            if (cursor != null && cursor.moveToNext()) {
//                db.update(AnnColumn.TABLE_NAME, cv, AnnColumn.ID + " =?", new String[]{id});
//            } else {
//                db.insert(AnnColumn.TABLE_NAME, null, cv);
//            }
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//            db.close();
//        }
//    }

//    public synchronized void insert(String id, String msg) {
//        AnnColumn column = new AnnColumn();
//        column.setId(id);
//        column.setMsg(msg);
//        SQLiteDatabase db = getWritableDatabase();
//        String annId = String.valueOf(id);
//        String sql = "select * from " + AnnColumn.TABLE_NAME + " where " + AnnColumn.ID + " =?";
//        Cursor cursor = db.rawQuery(sql, new String[]{annId});
//        try {
//            ContentValues cv = new ContentValues();
//            column.setContentValues(cv);
//            if (cursor != null && cursor.moveToNext()) {
//                db.update(AnnColumn.TABLE_NAME, cv, AnnColumn.ID + " =?", new String[]{annId});
//            } else {
//                db.insert(AnnColumn.TABLE_NAME, null, cv);
//            }
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//            db.close();
//        }
//    }

//    public synchronized List<String> getAnnMsg() {
//        SQLiteDatabase db = getReadableDatabase();
//        String sql = "select * from " + AnnColumn.TABLE_NAME;
//        Cursor cursor = db.rawQuery(sql, null);
//        try {
//            List<String> list = new ArrayList<>();
//            while (cursor.moveToNext()) {
//                AnnColumn column = new AnnColumn();
//                column.setDataFromCursor(cursor);
//                list.add(column.getMsg());
//            }
//            return list;
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//            db.close();
//        }
//    }
}
