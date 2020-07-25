package com.example.samplecalender;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyOpenHelper extends SQLiteOpenHelper {

    static final String CREATE_TABLE_SCHEDULE ="create table Schedule(" + "Date text not null," +
            "TimeDivision text," + "Plans text," + "Colors text,"+"ColorText text"+ " );";

    static final String CREATE_TABLE_PLANS ="create table Plans("+ "_id integer primary key autoincrement,"+
            "Plans text not null,"+"Color text,"+"ColorCode text"+");";

    //コンストラクタ
    public MyOpenHelper(Context context) {
        super(context, "CalendarDB", null, 1);
    }


    //SQLのコマンドしてくれている？
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SCHEDULE);
        db.execSQL(CREATE_TABLE_PLANS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        onCreate(db);
    }

}