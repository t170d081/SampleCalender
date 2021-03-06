package com.example.samplecalender;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarAdapter extends BaseAdapter {
    private List<Date> dateArray;
    private Context mContext;
    private DateManager mDateManager;
    private LayoutInflater mLayoutInflater;

    private String dateText;
    private String timedivision;
    private String amText;
    private String pmText;
    private String nightText;

    private String sdfDate;


    //カスタムセルを拡張したらここでWigetを定義
    private static class ViewHolder {
        public TextView dateText;
        public TextView am;
        public TextView pm;
        public TextView night;
    }

    public CalendarAdapter(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mDateManager = new DateManager();
        dateArray = mDateManager.getDays();
    }

    @Override
    public int getCount() {
        return dateArray.size();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.calendar_cell, null);
            holder = new ViewHolder();
            holder.dateText = convertView.findViewById(R.id.dateText);
            holder.am=convertView.findViewById(R.id.am);
            holder.pm=convertView.findViewById(R.id.pm);
            holder.night=convertView.findViewById(R.id.night);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        //セルのサイズを指定
        float dp = mContext.getResources().getDisplayMetrics().density;
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(parent.getWidth()/7 - (int)dp, (parent.getHeight() - (int)dp * mDateManager.getWeeks() ) / mDateManager.getWeeks());
        convertView.setLayoutParams(params);

        //日付のみ表示させる
        SimpleDateFormat dateFormat = new SimpleDateFormat("d", Locale.US);
        holder.dateText.setText(dateFormat.format(dateArray.get(position)));


        //当月以外のセルをグレーアウト
        if (mDateManager.isCurrentMonth(dateArray.get(position))){
            convertView.setBackgroundColor(Color.WHITE);
            if(mDateManager.isToday(dateArray.get(position))){
                convertView.setBackgroundColor(Color.YELLOW);
            }
        }else {
            convertView.setBackgroundColor(Color.LTGRAY);
        }

        //SQLセットアップ
        MyOpenHelper helper = new MyOpenHelper(mContext);
        SQLiteDatabase db = helper.getReadableDatabase();

        //queryメソッドの実行例(おそらくDBのテーブルについて)
        Cursor c = db.query("Schedule", new String[] {"Date","TimeDivision","Colors"},
                null, null, null, null, null);

        //色初期化と色付け初期化
        amText = "#FFFFFF";
        pmText = "#FFFFFF";
        nightText = "#FFFFFF";
        holder.am.setBackgroundColor(Color.parseColor(amText));
        holder.pm.setBackgroundColor(Color.parseColor(pmText));
        holder.night.setBackgroundColor(Color.parseColor(nightText));

        boolean mov = c.moveToFirst();
        while (mov) {
            dateText = c.getString(0);
            timedivision = c.getString(1);

            //DB内の時間ごとの予定を見て色を確保
            switch(timedivision){
                case "午前":
                    if(isPlanday(dateArray.get(position),dateText)){
                        amText = c.getString(2);
                        holder.am.setBackgroundColor(Color.parseColor(amText));
                    }
                    break;
                case "昼間":
                    if(isPlanday(dateArray.get(position),dateText)){
                        pmText = c.getString(2);
                        holder.pm.setBackgroundColor(Color.parseColor(pmText));
                    }
                    break;
                case "夜中":
                    if(isPlanday(dateArray.get(position),dateText)){
                        nightText = c.getString(2);
                        holder.night.setBackgroundColor(Color.parseColor(nightText));
                    }
                    break;
                default:
                    break;
            }

            mov = c.moveToNext();
        }

        c.close();
        db.close();

        //日曜日を赤、土曜日を青に
        int colorId;
        switch (mDateManager.getDayOfWeek(dateArray.get(position))){
            case 1:
                colorId = Color.RED;
                break;
            case 7:
                colorId = Color.BLUE;
                break;

            default:
                colorId = Color.BLACK;
                break;
        }
        holder.dateText.setTextColor(colorId);

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        sdf.setTimeZone(TimeZone.getTimeZone("JST"));
        return sdf.format(dateArray.get(position));
    }

    //表示月を取得
    public String getTitle(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM", Locale.US);
        return format.format(mDateManager.mCalendar.getTime());
    }

    //翌月表示
    public void nextMonth(){
        mDateManager.nextMonth();
        dateArray = mDateManager.getDays();
        this.notifyDataSetChanged();
    }

    //前月表示
    public void prevMonth(){
        mDateManager.prevMonth();
        dateArray = mDateManager.getDays();
        this.notifyDataSetChanged();
    }

    //予定追加日の色付け判定

    public boolean isPlanday(Date date, String lu){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        sdf.setTimeZone(TimeZone.getTimeZone("JST"));
        sdfDate = sdf.format(date);

        return lu.equals(sdfDate);
    }

}
