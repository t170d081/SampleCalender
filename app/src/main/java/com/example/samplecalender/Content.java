package com.example.samplecalender;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Content extends AppCompatActivity {

    private String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        TextView text = (TextView)findViewById(R.id.textview_id);
        Intent intent = getIntent();
        currentDate = intent.getStringExtra("date");
        text.setText(currentDate);
    }
}