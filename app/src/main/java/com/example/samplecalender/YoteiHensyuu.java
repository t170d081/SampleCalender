package com.example.samplecalender;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class YoteiHensyuu extends AppCompatActivity {

    private String plans;
    private String color = "#FFFFFF";
    private String item;
    private String deleteplan;

    private EditText plansText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yotei_hensyuu);

        plansText = (EditText)findViewById(R.id.editPlan);

        initColorSpinners(); //カラー選択スピナー処理
        initSpinners();//削除選択スピナー処理
    }

    private void initColorSpinners(){
        Spinner spinnerCol = (Spinner)findViewById(R.id.colorSpinner);
        String[] labels = getResources().getStringArray(R.array.Color_list);
        ArrayAdapter<String> adapter1
                = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labels);
        spinnerCol.setAdapter(adapter1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void initSpinners(){

        //削除スピナーとDBとの連結関連
        MyOpenHelper helper = new MyOpenHelper(this);
        final SQLiteDatabase db = helper.getWritableDatabase();

        Cursor c = db.query("Plans", new String[] {"_id","Plans","Color"},
                null, null, null, null, null);

        String[] from = {"Plans", "Color"};
        int[] to = {R.id.Plans, R.id.Color};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.spinner,c,from,to);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);

        Spinner spinner  = (Spinner)this.findViewById(R.id.SpinnerDelete);
        spinner.setAdapter(adapter);

        //スピナー選択時
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView parent, View view, int position, long id) {
                TextView text = (TextView)findViewById(R.id.textdel);
                Spinner spinner = (Spinner) parent;
                Cursor cursor = (Cursor)spinner.getSelectedItem();
                int PlanId = cursor.getInt(cursor.getColumnIndex("_id"));

                //スピナードロップダウン時に表示するもの
                Toast.makeText(YoteiHensyuu.this,
                        Integer.valueOf(PlanId).toString(),
                        Toast.LENGTH_LONG).show();

                //スピナー内で選択した項目を表示
                String select =  String.format("%s : %s",
                        cursor.getString(1),cursor.getString(2));

                text.setText(select);
                deleteplan = cursor.getString(1);
            }
            public void onNothingSelected(AdapterView parent) {

            }
        });
    }

    private void selectColorCode(String item){
        switch(item) {
            case "青":
                color = "#2C7CFF";
                break;
            case "水色":
                color = "#60EEFF";
                break;
            case "紺":
                color = "#0000BB";
                break;
            case "濃紺":
                color = "#000066";
                break;
            case "紫":
                color = "#A16EFF";
                break;
            case "うす紫":
                color = "#DCC2FF";
                break;
            case "赤":
                color = "#FF1A6F";
                break;
            case "茶色":
                color = "#800000";
                break;
            case "エンジ":
                color = "#550000";
                break;
            case "オレンジ":
                color = "#FF6928";
                break;
            case "ピンク":
                color = "#FFABCE";
                break;
            case "ショッキングピンク":
                color = "#FF66FF";
                break;
            case "緑":
                color = "#00FF7F";
                break;
            case "深緑":
                color = "#008800";
                break;
            case "黄緑":
                color = "#ADFF2F";
                break;
            case "青緑":
                color = "#8FBC8F";
                break;
            case "黄色":
                color = "#FFFF55";
                break;
            case "赤黄":
                color = "#FFD700";
                break;
            case "黒":
                color = "#000000";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + plans);
        }
    }

    // 追加ボタンクリック処理
    public void onButtonTuika(View v) {
        plans = plansText.getText().toString();
        Spinner spinnerCol = (Spinner)findViewById(R.id.colorSpinner);
        item = (String)spinnerCol.getSelectedItem();

        selectColorCode(item);

        //SQLのセットアップ
        MyOpenHelper helper = new MyOpenHelper(this);
        final SQLiteDatabase db = helper.getWritableDatabase();

        //DB内に格納する配列的なものと、1つ1つのデータ
        ContentValues insertValues = new ContentValues();
        insertValues.put("Plans", plans);
        insertValues.put("Color", item);
        insertValues.put("ColorCode",color);

        //DBに格納する
        db.insert("Plans", plans, insertValues);
        db.close();

        //画面遷移
        Intent intent = new Intent(YoteiHensyuu.this, YoteiHensyuu.class);
        startActivity(intent);
    }

    //削除ボタンクリック処理
    public void onButtonDelete(View v){

        //SQLのセットアップ
        MyOpenHelper helper = new MyOpenHelper(this);
        final SQLiteDatabase db = helper.getWritableDatabase();

        //DB内のデータ削除
        db.delete("Plans", "Plans=?", new String[] { deleteplan });
        db.delete("Schedule", "Plans=?", new String[]{deleteplan});
        db.close();

        //画面遷移
        Intent intent = new Intent(YoteiHensyuu.this, YoteiHensyuu.class);
        startActivity(intent);
    }

    //オール削除ボタン処理
    public void onButtonAllDelete(View v){
        //SQLのセットアップ
        MyOpenHelper helper = new MyOpenHelper(this);
        final SQLiteDatabase db = helper.getWritableDatabase();

        //DB内のデータ全削除
        db.delete("Plans", null,null);

        //autoincrementsのリセット
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'Plans'");
        db.close();

        //画面遷移
        Intent intent = new Intent(YoteiHensyuu.this, YoteiHensyuu.class);
        startActivity(intent);
    }

    //予定編集ボタンクリック画面遷移処理
    public void onButtonHensyuDB(View v){
        boolean flug = false;

        //画面遷移
        Intent intent = new Intent(YoteiHensyuu.this, ShowDataBase.class);
        intent.putExtra("PlanDB",flug);
        startActivity(intent);
    }

    public void onButtonBack(View v){
        Intent intent = new Intent(YoteiHensyuu.this, MainActivity.class);
        startActivity(intent);
    }

    public void onButtonRestart(View v){
        Intent intent = new Intent(YoteiHensyuu.this, YoteiHensyuu.class);
        startActivity(intent);
    }

    public void onSpinnerDelete(View v){

    }


}