package com.example.samplecalender;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Content extends AppCompatActivity {

    private String currentDate;
    private String timedivision;
    private String plans;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //日付データ識別
        setContentView(R.layout.activity_content);
        TextView text = (TextView)findViewById(R.id.textview_id);
        Intent intent = getIntent();
        currentDate = intent.getStringExtra("date");

        //日付データ表示関連
        text.setText(currentDate);

        //1つめのラジオボタン識別処理
        RadioGroup rg1 = (RadioGroup) findViewById(R.id.radioGroup1);
        rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1) {
                    // 選択されているラジオボタンの取得
                    RadioButton radioButton = (RadioButton) findViewById(checkedId);

                    // ラジオボタンのテキストを取得
                    timedivision = radioButton.getText().toString();
                    Log.v("checked", timedivision);


                } else {
                    // 何も選択されていない場合の処理
                }
            }
        });

        //2つめのラジオボタン識別処理
        RadioGroup rg2 = (RadioGroup) findViewById(R.id.radioGroup2);
        rg2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1) {
                    // 選択されているラジオボタンの取得
                    RadioButton radioButton = (RadioButton) findViewById(checkedId);

                    // ラジオボタンのテキストを取得
                    plans = radioButton.getText().toString();
                    Log.v("checked", plans);


                } else {
                    // 何も選択されていない場合の処理
                }
            }
        });

    }

    // 決定ボタンクリック処理
    public void onButtonKettei(View v){

        //SQLのセットアップ
        MyOpenHelper helper = new MyOpenHelper(this);
        final SQLiteDatabase db = helper.getWritableDatabase();

        //DB内に格納する配列的なものと、1つ1つのデータ
        ContentValues insertValues = new ContentValues();
        insertValues.put("Date",currentDate);
        insertValues.put("TimeDivision", timedivision);
        insertValues.put("Plans",plans);

        //DBに格納する
        long id = db.insert("Schedule", currentDate, insertValues);

        //画面遷移
        Intent dbIntent = new Intent(Content.this, ShowDataBase.class);
        startActivity(dbIntent);

    }
    //削除ボタンクリック処理
    public void onButtonDelete(View v){
        //SQLのセットアップ
        MyOpenHelper helper = new MyOpenHelper(this);
        final SQLiteDatabase db = helper.getWritableDatabase();

        //DB内のデータ全削除
        db.delete("Schedule", null, null);
    }
}