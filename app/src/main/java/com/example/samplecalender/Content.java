package com.example.samplecalender;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Content extends AppCompatActivity {
    private String currentDate;
    private String timeDivision = "なし";
    private String plans = "なし";
    private String color = "#FFFFFF";

    private String amtext="空き";
    private String pmtext="空き";
    private String nighttext="空き";

    private boolean notSelectflag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        //日付データ識別
        TextView text = (TextView)findViewById(R.id.textview_id);
        Intent intent = getIntent();
        currentDate = intent.getStringExtra("date");

        //日付データ表示関連
        text.setText(currentDate);

        //SQLのセットアップ
        MyOpenHelper helper = new MyOpenHelper(this);
        final SQLiteDatabase db = helper.getWritableDatabase();

        Cursor c = db.query("Schedule", new String[] {"Date","TimeDivision","Plans"},
                null, null, null, null, null);

        boolean mov = c.moveToFirst();
        while (mov) {
            if(currentDate.equals(c.getString(0))){
                switch(c.getString(1)){
                    case "午前":
                        if(!c.getString(2).equals("null"))
                        amtext = c.getString(2);
                        break;
                    case "昼間":
                        if(!c.getString(2).equals("null"))
                        pmtext = c.getString(2);
                        break;
                    case "夜中":
                        if(!c.getString(2).equals("null"))
                        nighttext = c.getString(2);
                        break;
                    default:
                        break;
                }
            }
            mov = c.moveToNext();
        }
        TextView textView = (TextView)findViewById(R.id.todayPlan);
        textView.setText(String.format("午前:%s  昼間:%s  夜中:%s", amtext,
                pmtext,nighttext));
        c.close();
        db.close();


        judgeRadio();
        initSpinners();
    }
    private void judgeRadio(){
        //1つめのラジオボタン識別処理
        RadioGroup rg1 = (RadioGroup) findViewById(R.id.radioGroup1);
        rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1) {
                    notSelectflag = true;

                    // 選択されているラジオボタンの取得
                    RadioButton radioButton = (RadioButton) findViewById(checkedId);

                    // ラジオボタンのテキストを取得
                    timeDivision = radioButton.getText().toString();
                    Log.v("checked", timeDivision);

                } else {
                    // 何も選択されていない場合の処理
                    notSelectflag = false;
                }
            }
        });
    }

    private void initSpinners(){
        //削除スピナーとDBとの連結関連
        MyOpenHelper helper = new MyOpenHelper(this);
        final SQLiteDatabase db = helper.getWritableDatabase();

        Cursor c = db.query("Plans", new String[] {"_id","Plans","Color","ColorCode"},
                null, null, null, null, null);

        String[] from = {"Plans", "Color"};
        int[] to = {R.id.Plans, R.id.Color};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.spinner,c,from,to);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);

        Spinner spinner  = (Spinner)this.findViewById(R.id.SpinnerPlanSelect);
        spinner.setAdapter(adapter);

        //スピナー選択時
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView parent, View view, int position, long id) {
                TextView text = (TextView)findViewById(R.id.textdel);
                Spinner spinner = (Spinner) parent;
                Cursor cursor = (Cursor)spinner.getSelectedItem();
                int PlanId = cursor.getInt(cursor.getColumnIndex("_id"));

                //スピナードロップダウン時に表示するもの
                Toast.makeText(Content.this,
                        Integer.valueOf(PlanId).toString(),
                        Toast.LENGTH_LONG).show();

                //スピナー内で選択した項目を表示
                String select =  String.format("%s : %s",
                        cursor.getString(1),cursor.getString(2));
                text.setText(select);

                plans = cursor.getString(1);
                color = cursor.getString(3);
            }
            public void onNothingSelected(AdapterView parent) {

            }
        });
    }

    // 決定ボタンクリック処理
    public void onButtonKettei(View v){
        //SQLのセットアップ
        MyOpenHelper helper = new MyOpenHelper(this);
        final SQLiteDatabase db = helper.getWritableDatabase();

        if(notSelectflag) {
            //DB内に格納する配列的なものと、1つ1つのデータ
            ContentValues insertValues = new ContentValues();
            insertValues.put("Date", currentDate);
            insertValues.put("TimeDivision", timeDivision);
            insertValues.put("Plans", plans);
            insertValues.put("Colors", color);

            //DBに格納する
            db.insert("Schedule", currentDate, insertValues);

        }

        //画面遷移
        Intent dbIntent = new Intent(Content.this, MainActivity.class);
        startActivity(dbIntent);
        db.close();
    }

    //削除ボタンクリック処理
    public void onButtonDelete(View v){
        //SQLのセットアップ
        MyOpenHelper helper = new MyOpenHelper(this);
        final SQLiteDatabase db = helper.getWritableDatabase();

        //DB内のデータ全削除
        db.delete("Schedule", "Date=? and TimeDivision=?",
                new String[] {currentDate,timeDivision });
        db.close();
    }

    //削除ボタンクリック処理
    public void onButtontodayDelete(View v){
        //SQLのセットアップ
        MyOpenHelper helper = new MyOpenHelper(this);
        final SQLiteDatabase db = helper.getWritableDatabase();

        //DB内のデータ全削除
        db.delete("Schedule", "Date=?", new String[] {currentDate});
        db.close();
    }

    //予定編集ボタンクリック画面遷移処理
    public void onButtonHensyu(View v){
        //画面遷移
        Intent planIntent = new Intent(Content.this, YoteiHensyuu.class);
        startActivity(planIntent);
    }

    public void onButtonRestart(View v){
        Intent intent = new Intent(Content.this, Content.class);
        intent.putExtra("date", currentDate);
        startActivity(intent);
    }

    //オール削除ボタン処理
    public void onButtonAllDelete(View v){
        //SQLのセットアップ
        MyOpenHelper helper = new MyOpenHelper(this);
        final SQLiteDatabase db = helper.getWritableDatabase();

        //DB内のデータ全削除
        db.delete("Schedule", null,null);
        db.close();
    }

    //DB内のデータ確認ボタンクリック処理
    public void onButtonDB(View v){
        //画面遷移
        Intent dbIntent = new Intent(Content.this, ShowDataBase.class);
        startActivity(dbIntent);
    }

}