package com.hcx.ocrapp.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class RegisHelper extends SQLiteOpenHelper {
    private static final String TAG = "RegisHelper";
    private static final String DATABASE_NAME = "user.db";
    private static final int DATABASE_VERSION = 1;
    public RegisHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate: 成功创建user表");
        db.execSQL("CREATE TABLE IF NOT EXISTS user(username VARCHAR(10) PRIMARY KEY ," + " password VARCHAR(10))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
