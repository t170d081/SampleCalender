package com.example.samplecalender;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShowDataBase extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data_base);

        //縦に表示する配列的な何か
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);

        //SQLセットアップ
        MyOpenHelper helper = new MyOpenHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        // queryメソッドの実行例(おそらくDBのテーブルについて)
        Cursor c = db.query("Schedule", new String[] {"Date", "TimeDivision","Plans","Colors"},
                null, null, null, null, null);

        //不明ｗ
        boolean mov = c.moveToFirst();
        while (mov) {
            TextView textView = new TextView(this);
            textView.setText(String.format("%s : %s : %s : %s", c.getString(0),
                    c.getString(1),c.getString(2),c.getString(3)));
            mov = c.moveToNext();
            layout.addView(textView);
        }
        c.close();
        db.close();
    }
}