package com.example.samplecalender;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
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
    private String colorText = "";

    private String amtext;
    private String pmtext;
    private String nighttext;

    private boolean notSelecttimeflag = true;
    private boolean notSelectplanflag;
    MyOpenHelper helper = new MyOpenHelper(this);
    private ProgressDialog progressDialog;
    Handler mHandler;

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

        dayPlanSetText();
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
                    notSelecttimeflag = false;

                    // 選択されているラジオボタンの取得
                    RadioButton radioButton = (RadioButton) findViewById(checkedId);

                    // ラジオボタンのテキストを取得
                    timeDivision = radioButton.getText().toString();
                    Log.v("checked", timeDivision);

                } else {
                    // 何も選択されていない場合の処理
                    notSelecttimeflag = true;
                }
            }
        });
    }

    private void initSpinners(){
        //削除スピナーとDBとの連結関連
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
                colorText = cursor.getString(2);
                color = cursor.getString(3);
            }
            public void onNothingSelected(AdapterView parent) {

            }
        });
    }

    private void dayPlanSetText(){
        amtext="空き";
        pmtext="空き";
        nighttext="空き";

        //SQLのセットアップ
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
    }

    // 決定ボタンクリック処理
    public void onButtonKettei(View v){
        //何も選択されていないフラグ
        notSelectplanflag = false;

        System.out.println("aaaa"+plans);

        if(plans.equals("なし")) notSelectplanflag = true;
        System.out.println("kettei(1)selecttime" + notSelecttimeflag + notSelectplanflag);

        if((!notSelecttimeflag) && (!notSelectplanflag)) {

            //ハンドラを生成
            mHandler = new Handler();

            //ProgressDialogを生成します。
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(timeDivision+"の予定を登録中");

            //ProgressDialogを表示します。
            progressDialog.show();

            //スレッドを生成して起動します。
            Content.MyThread thread = new Content.MyThread();
            thread.start();
        }
        else if(notSelecttimeflag && notSelectplanflag){
            Toast myToast = Toast.makeText(
                    getApplicationContext(),
                    "登録する[時間][予定]を選択してください",
                    Toast.LENGTH_SHORT
            );
            myToast.show();
        }
        else if(notSelecttimeflag){
            Toast myToast = Toast.makeText(
                    getApplicationContext(),
                    "登録する[時間]を選択してください",
                    Toast.LENGTH_SHORT
            );
            myToast.show();
        }
        else if(notSelectplanflag){
            Toast myToast = Toast.makeText(
                    getApplicationContext(),
                    "登録する[予定]を選択してください",
                    Toast.LENGTH_SHORT
            );
            myToast.show();
        }

    }

    class MyThread extends Thread {
        public void run() {
            //時間のかかる処理
            //DB内に格納する配列的なものと、1つ1つのデータ
            ContentValues insertValues = new ContentValues();
            insertValues.put("Date", currentDate);
            insertValues.put("TimeDivision", timeDivision);
            insertValues.put("Plans", plans);
            insertValues.put("Colors", color);
            insertValues.put("ColorText", colorText);

            //DBに格納する
            final SQLiteDatabase db = helper.getWritableDatabase();
            db.insert("Schedule", currentDate, insertValues);
            db.close();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //メインスレッドのメッセージキューにメッセージを登録します。
            mHandler.post(new Runnable() {
                //run()の中の処理はメインスレッドで動作されます。
                public void run() {
                    //追加予定を確認できるよう表示
                    dayPlanSetText();

                    //ProgressDialogを消去します。
                    progressDialog.dismiss();

                    Toast myToast = Toast.makeText(
                            getApplicationContext(),
                            timeDivision+"に予定["+plans+" : "+colorText+"]の登録が完了しました",
                            Toast.LENGTH_SHORT
                    );
                    myToast.show();
                }
            });
        }
    }

    //戻るボタン処理
    public void onButtonBack(View v){
        Intent intent = new Intent(Content.this, MainActivity.class);
        startActivity(intent);
    }

    //削除ボタンクリック処理
    public void onButtonDelete(View v){
        boolean delete = true;
        //SQLのセットアップ
        final SQLiteDatabase db = helper.getWritableDatabase();

        String setText="";


        if(!notSelecttimeflag) {
            switch (timeDivision) {
                case "午前":
                    if (!amtext.equals("空き")) {
                        setText = timeDivision + "の削除が完了しました";

                        //DB内のデータ削除
                        db.delete("Schedule", "Date=? and TimeDivision=?",
                                new String[]{currentDate, timeDivision});
                        db.close();
                    }else{
                        setText = timeDivision + "には予定が入っていません";
                    }
                    break;
                case "昼間":
                    if (!pmtext.equals("空き")) {
                        setText = timeDivision + "の削除が完了しました";

                        //DB内のデータ削除
                        db.delete("Schedule", "Date=? and TimeDivision=?",
                                new String[]{currentDate, timeDivision});
                        db.close();
                    }
                    else{
                        setText = timeDivision + "には予定が入っていません";
                    }
                    break;
                case "夜中":
                    if (!nighttext.equals("空き")) {
                        setText = timeDivision + "の削除が完了しました";

                        //DB内のデータ削除
                        db.delete("Schedule", "Date=? and TimeDivision=?",
                                new String[]{currentDate, timeDivision});
                        db.close();
                    }
                    else{
                        setText = timeDivision + "には予定が入っていません";
                    }
                    break;
                default:
                    break;
            }
        }else{
            setText = "削除を行う[時間]を選択してください";
        }

        Toast myToast = Toast.makeText(
                getApplicationContext(),
                setText,
                Toast.LENGTH_SHORT
        );
        myToast.show();
        dayPlanSetText();
    }

    //今日の削除ボタンクリック処理
    public void onButtontodayDelete(View v){
        String setText = "指定日の予定削除が完了しました";
        if(amtext.equals("空き") && pmtext.equals("空き") && nighttext.equals("空き"))
            setText = "指定日には予定が入っていません";

        //SQLのセットアップ
        final SQLiteDatabase db = helper.getWritableDatabase();

        //DB内のデータ全削除
        db.delete("Schedule", "Date=?", new String[] {currentDate});
        db.close();

        Toast myToast = Toast.makeText(
                getApplicationContext(),
                setText,
                Toast.LENGTH_SHORT
        );
        myToast.show();
        dayPlanSetText();
    }

    //予定編集ボタンクリック画面遷移処理
    public void onButtonHensyu(View v){
        //画面遷移
        Intent planIntent = new Intent(Content.this, YoteiHensyuu.class);
        startActivity(planIntent);
    }

    public void onButtonRestart(View v){
        initSpinners();
        dayPlanSetText();
        Toast myToast = Toast.makeText(
                getApplicationContext(),
                "更新が完了しました",
                Toast.LENGTH_SHORT
        );
        myToast.show();
        dayPlanSetText();
    }

    //オール削除ボタン処理
    public void onButtonAllDelete(View v){
        //SQLのセットアップ
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