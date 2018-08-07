package com.andy.spo2.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {

    private static final String database = "spo2.db";
    private static final int version = 1;

    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                      int version) {
        super(context, name, factory, version);
    }

    public MyDBHelper(Context context) {
        this(context, database, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE spo2Table(_id integer primary key autoincrement," +
                "dateTime VARCHAR(100)," +
                "spo2 VARCHAR(20)," +
                "pulse VARCHAR(20),"+
                "user VARCHAR(100),"+
                "deviceName VARCHAR(100),"+
                "deviceMAC VARCHAR(100))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS spo2Table");
        onCreate(db);
    }
}
