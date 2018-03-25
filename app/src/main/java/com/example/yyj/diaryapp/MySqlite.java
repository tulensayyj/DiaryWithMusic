package com.example.yyj.diaryapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yyj on 2017/12/6.
 */

public class MySqlite extends SQLiteOpenHelper {

    /*public MySqlite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }*/
    public MySqlite(Context context){
        super(context,"Diary.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table Diary(_id integer primary key autoincrement, Title varchar,Details varchar)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}