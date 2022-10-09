package com.hcx.ocrapp.sqlite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hcx.ocrapp.modle.User;
import com.hcx.ocrapp.modle.WordData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

//对word表上的所有数据操作
public class WordDB {
    private DBHelper helper;
    private SQLiteDatabase db;
    private Calendar calendars;//系统时间

    public WordDB(Context context){
        //初始化
        helper = new DBHelper(context);
    }
    //添加
    public void addWord(WordData wordData){
        // 初始化db对象
        db = helper.getWritableDatabase();
        // SQL语句执行添加操作
        db.execSQL("insert into word(id,word,time,userId) values(?,?,?,?)",
        new Object[] { wordData.getId(),wordData.getWord(),wordData.getTime(),wordData.getUserId() });


    }

    //查询
    @SuppressLint("Range")
    public List<WordData> findWord(String username){
        // 创建集合对象
        List<WordData> wordDataList = new ArrayList<WordData>();
        // 初始化SQLiteDatabase对象
        db = helper.getWritableDatabase();
        // 获取所有信息
        Cursor cursor = db.rawQuery("select * from word where userId = ?",new String[]{username});
        while (cursor.moveToNext()){// 遍历所有的信息
            // 将遍历到的信息添加到集合中
            wordDataList.add(new WordData(cursor.getInt(cursor
                    .getColumnIndex("id")), cursor.getString(cursor
                    .getColumnIndex("word")), cursor.getString(cursor
                    .getColumnIndex("time")),cursor.getString(cursor
                    .getColumnIndex("userId"))));
        }
        // 返回收入信息表集合
        return wordDataList;
    }
    /*** 获取信息表最大编号 */
    public int getMaxId() {
        // 初始化SQLiteDatabase对象
        db = helper.getWritableDatabase();
        // 获取信息表中的最大编号
        Cursor cursor = db.rawQuery("select max(id) from word", null);
        // 访问Cursor中的最后一条数据
        while (cursor.moveToLast()) {
        // 获取访问到的数据，即最大编号
            return cursor.getInt(0);
        }
        // 如果没有数据，则返回0
        return 0;
    }
    /** 删除信息 */
    public void deleteWord(int id) {
            db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
            // 执行删除信息操作
            db.execSQL("delete from word where id in (?)",new Object[]{id} );

    }
    /** 自定义方法显示系统时间 */
    public String getTime() {
//        calendars = Calendar.getInstance();
//        calendars.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
//        String year = String.valueOf(calendars.get(Calendar.YEAR));
//        String month = String.valueOf(calendars.get(Calendar.MONTH));
//        String day = String.valueOf(calendars.get(Calendar.DATE));
//        String hour = String.valueOf(calendars.get(Calendar.HOUR));
//        String min = String.valueOf(calendars.get(Calendar.MINUTE));
        //return year+"-"+month+"-"+day+" "+hour+":"+min;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.getDefault());
        String date = sdf.format(new java.util.Date());

        return date;
    }

}
