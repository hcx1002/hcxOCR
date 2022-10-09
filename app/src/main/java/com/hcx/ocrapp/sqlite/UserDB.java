package com.hcx.ocrapp.sqlite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hcx.ocrapp.modle.User;

public class UserDB {
    private RegisHelper helper;
    private SQLiteDatabase db;
    public UserDB(Context context){
        //初始化
        helper = new RegisHelper(context);
    }

    //添加数据到user表
    public void addUser(User user){
        db = helper.getWritableDatabase();
        // SQL语句执行添加操作
        db.execSQL("insert into user(username,password) values(?,?)",
                new Object[] { user.getUsername(),user.getPassword()});
    }
    //查询单个数据是否存在或检验账号和密码是否一致
    @SuppressLint("Range")
    public boolean login(String name, String pass){
        db = helper.getReadableDatabase();
        Cursor cursor = db.query("user",new String[]{"username","password"},null,null,null,null,null);
        if (pass!=null){
            while (cursor.moveToNext()){
                if (name.equals(cursor.getString(cursor.getColumnIndex("username")))&&pass.equals(cursor.getString(cursor.getColumnIndex("password")))){
                    return true;
                }
            }
        }else{
            //用来判断账号是否存在
            while (cursor.moveToNext()){
                if (name.equals(cursor.getString(cursor.getColumnIndex("username")))){
                    return true;
                }
            }
        }
        return false;
    }

}
