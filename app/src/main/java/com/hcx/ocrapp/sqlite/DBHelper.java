package com.hcx.ocrapp.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private static final String DATABASE_NAME = "word.db";
    private static final int DATABASE_VERSION = 3;

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG,"创建word数据库表！");
        db.execSQL("CREATE TABLE IF NOT EXISTS word(id INTEGER PRIMARY KEY AUTOINCREMENT," + " word TEXT,time varchar(10),userId varchar(255))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("CREATE TABLE IF NOT EXISTS word(id INTEGER PRIMARY KEY AUTOINCREMENT," + " word TEXT,time varchar(10),userId varchar(255))");
    }
}
