package com.seek.db.column;

import android.provider.BaseColumns;
import android.util.Log;

import java.util.Map;

public abstract class DatabaseColumn implements BaseColumns {


    public String getTableCreate() {
        String tableCreate = getTableCreator(getTableName(), getTableMap());
        Log.d("DBHelper", "tableCreate:" + tableCreate);
        return tableCreate;
    }

    /**
     * Create a sentence to create a table by using a hash-map.
     *
     * @param tableName The table's name to create.
     * @param map       A map to store table columns info.
     * @return
     */
    private static final String getTableCreator(String tableName,
                                                Map<String, String> map) {
        String[] keys = map.keySet().toArray(new String[0]);
        String value = null;
        StringBuilder creator = new StringBuilder();
        creator.append("CREATE TABLE ").append(tableName).append("( ");
        int length = keys.length;
        for (int i = 0; i < length; i++) {
            value = map.get(keys[i]);
            creator.append(keys[i]).append(" ");
            creator.append(value);
            if (i < length - 1) {
                creator.append(",");
            }
        }
        creator.append(")");
        return creator.toString();
    }

    abstract public String getTableName();


    abstract protected Map<String, String> getTableMap();

}
