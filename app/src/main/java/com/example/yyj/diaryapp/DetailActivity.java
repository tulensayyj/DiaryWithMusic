package com.example.yyj.diaryapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    MySqlite mySqlite=null;
    SQLiteDatabase mdbWriter=null;
    SQLiteDatabase mdbReader=null;

    TextView detail_title;
    TextView detail_details;

    int Position;
    String strTitle;
    String strDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        detail_title=(TextView)findViewById(R.id.details_title);
        detail_details=(TextView)findViewById(R.id.details_detail);

        mySqlite=new MySqlite(this);
        mdbWriter=mySqlite.getWritableDatabase();
        mdbReader=mySqlite.getReadableDatabase();
        Intent intent=getIntent();

        strTitle=intent.getStringExtra("Title");
        strDetails=intent.getStringExtra("Details");

        detail_title.setText(strTitle);
        detail_details.setText(strDetails);
    }
}
