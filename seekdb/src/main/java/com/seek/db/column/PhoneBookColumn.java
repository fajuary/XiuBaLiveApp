package com.seek.db.column;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chunyang on 2018/6/8.
 */

public class PhoneBookColumn extends DatabaseColumn {


    public static final String TABLE_NAME = "phoneBook";


    public static final String NAME = "name";
    public static final String NUMBER = "number";
    public static final String TYPE = "type";//特殊标记 0删除1新增2上传完成-1默认

    public void setContentValues(ContentValues cv) {
//        cv.put(_ID, this.id);
        cv.put(NAME, this.nick);
        cv.put(NUMBER, this.mobile);
        cv.put(TYPE, this.type);
    }

    public void setDataFromCursor(Cursor cursor) {
//        setId(cursor.getInt(cursor.getColumnIndex(_ID)));
        setName(cursor.getString(cursor.getColumnIndex(NAME)));
        setNumber(cursor.getString(cursor.getColumnIndex(NUMBER)));
        setType(cursor.getInt(cursor.getColumnIndex(TYPE)));
    }

    //    private int id;
    private String nick;
    private String mobile;
    private int type = -1;//特殊标记 0删除1新增 -1默认

    public String getName() {
        return nick;
    }

    public void setName(String name) {
        this.nick = name;
    }

    public String getNumber() {
        return mobile;
    }

    public void setNumber(String number) {
        this.mobile = number;
    }

//    public int getId() {
//        return id;
//    }

    //    public void setId(int id) {
//        this.id = id;
//    }
//
    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }


    @Override
    protected Map<String, String> getTableMap() {
        Map<String, String> columns = new HashMap<String, String>();
        columns.put(_ID, "integer primary key autoincrement");
        columns.put(NAME, "text");
        columns.put(NUMBER, "text");
        columns.put(TYPE, "integer");

        return columns;
    }


    @Override
    public int hashCode() {
        return mobile.hashCode();
    }

    public boolean equals(PhoneBookColumn column) {
        return column.getName().equals(getName()) && column.getNumber().equals(getNumber());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PhoneBookColumn)
            return equals((PhoneBookColumn) o);
        return super.equals(o);
    }
}
