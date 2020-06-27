package com.example.samplecalender;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyOpenHelper extends SQLiteOpenHelper {

    //コンストラクタ
    public MyOpenHelper(Context context) {
        super(context, "NameAgeDB", null, 1);
    }


    //SQLのコマンドしてくれている？
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table Schedule(" + " Date text not null," + "TimeDivision text," +
                "Plans text," + "Colors text" + " );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}