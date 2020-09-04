package com.seek.db;

import android.content.ContentValues;
import android.content.Context;
import android.provider.BaseColumns;

import com.seek.db.column.DatabaseColumn;
import com.seek.db.column.FriendColumn;
import com.seek.db.column.FriendVersionColumn;
import com.seek.db.column.GreetColumn;
import com.seek.db.column.LoginHistoryColumn;
import com.seek.db.column.PhoneBookColumn;
import com.tencent.wcdb.Cursor;
import com.tencent.wcdb.DatabaseErrorHandler;
import com.tencent.wcdb.database.SQLiteCipherSpec;
import com.tencent.wcdb.database.SQLiteDatabase;
import com.tencent.wcdb.database.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by chunyang on 2018/6/8.
 */

class DBHelper extends SQLiteOpenHelper {


//    private static final String DB_NAME = "seekBase.db";
//    private static final int DB_VERSION = 1;
//    private static final int DB_USER_VERSION = 1;
//    private static DBHelper IN;
    //    public static DBHelper getInstance(Context context, String dbNames) {
//        if (IN == null) {                         //Single Checked
//            synchronized (DBHelper.class) {
//                if (IN == null) {                 //Double Checked
//                    IN = new DBHelper(context.getApplicationContext());
//                }
//            }
//        }
//        return IN;
//    }


    private SQLiteDatabase db;
    static final SQLiteCipherSpec CIPHER_SPEC = new SQLiteCipherSpec()
            .setPageSize(1024);
    static final byte[] PASSPHRASE = "seek".getBytes();
    private final String[] SUBCLASSES;


    static final DatabaseErrorHandler ERROR_HANDLER = new DatabaseErrorHandler() {
        @Override
        public void onCorruption(SQLiteDatabase dbObj) {
            // Do nothing
        }
    };

    public DBHelper(Context context, String dbName, int version) {
        super(context, dbName, PASSPHRASE, CIPHER_SPEC, null, version, ERROR_HANDLER);
        SUBCLASSES = "seekBase.db".equals(dbName) ?

                new String[]{PhoneBookColumn.class.getName(), LoginHistoryColumn.class.getName()} :

                new String[]{GreetColumn.class.getName(), FriendVersionColumn.class.getName(), FriendColumn.class.getName()};
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        operateTable(db, "");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == newVersion) {
            return;
        }
        operateTable(db, "DROP TABLE IF EXISTS ");
        onCreate(db);
    }

    /**
     * Get sub-classes of this class.
     *
     * @return Array of sub-classes.
     */
    private final Class<DatabaseColumn>[] getSubClasses() {
        ArrayList<Class<DatabaseColumn>> classes = new ArrayList<Class<DatabaseColumn>>();
        Class<DatabaseColumn> subClass;
        for (int i = 0; i < SUBCLASSES.length; i++) {
            try {
                subClass = (Class<DatabaseColumn>) Class.forName(SUBCLASSES[i]);
                classes.add(subClass);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                continue;
            }
        }
        return classes.toArray(new Class[0]);
    }

    private void operateTable(SQLiteDatabase db, String actionString) {
        Class<DatabaseColumn>[] columnsClasses = getSubClasses();
        DatabaseColumn columns = null;

        for (int i = 0; i < columnsClasses.length; i++) {
            try {
                columns = columnsClasses[i].newInstance();
                if ("".equals(actionString) || actionString == null) {
                    db.execSQL(columns.getTableCreate());
                } else {
                    db.execSQL(actionString + columns.getTableName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public long insert(String Table_Name, ContentValues values) {
        if (db == null)
            db = getWritableDatabase();
        return db.insert(Table_Name, null, values);
    }

    /**
     * @param Table_Name
     * @param id
     * @return 影响行数
     */
    public int delete(String Table_Name, int id) {
        if (db == null)
            db = getWritableDatabase();
        return db.delete(Table_Name, BaseColumns._ID + "=?",
                new String[]{String.valueOf(id)});
    }

    /**
     * @param Table_Name
     * @param values
     * @param WhereClause
     * @param whereArgs
     * @return 影响行数
     */
    public int update(String Table_Name, ContentValues values,
                      String WhereClause, String[] whereArgs) {
        if (db == null) {
            db = getWritableDatabase();
        }
        return db.update(Table_Name, values, WhereClause, whereArgs);
    }

    public Cursor query(String Table_Name, String[] columns, String whereStr,
                        String[] whereArgs) {
        if (db == null) {
            db = getReadableDatabase();
        }
        return db.query(Table_Name, columns, whereStr, whereArgs, null, null,
                null);
    }

    public Cursor rawQuery(String sql, String[] args) {
        if (db == null) {
            db = getReadableDatabase();
        }
        return db.rawQuery(sql, args);
    }

    public void execSQL(String sql) {
        if (db == null) {
            db = getWritableDatabase();
        }
        db.execSQL(sql);
    }

    public void closeDb() {
        if (db != null) {
            db.close();
            db = null;
        }
    }
}
