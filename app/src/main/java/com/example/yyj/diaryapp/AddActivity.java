package com.example.yyj.diaryapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;

public class AddActivity extends AppCompatActivity {
    SimpleCursorAdapter lvAdapter=null;
    EditText add_title=null;
    EditText add_details=null;
    MySqlite mySqlite=null;
    SQLiteDatabase mdbWriter=null;
    SQLiteDatabase mdbReader=null;
    Button add_btnAdd=null;
    Button add_btnCancel=null;

    Context context=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);



        add_title=(EditText)findViewById(R.id.add_title);
        add_details=(EditText)findViewById(R.id.add_details);
        add_btnAdd=(Button)findViewById(R.id.add_btnAdd);
        add_btnCancel=(Button)findViewById(R.id.add_btnCancel);


        mySqlite=new MySqlite(this);
        mdbWriter=mySqlite.getWritableDatabase();
        mdbReader=mySqlite.getReadableDatabase();

        add_btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertData();
                Intent intent=new Intent(AddActivity.this,MainActivity.class);
                startActivity(intent);
                AddActivity.this.finish();
            }
        });

        add_btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddActivity.this,MainActivity.class);
                startActivity(intent);
                AddActivity.this.finish();
            }
        });

    }

    public void insertData(){
        ContentValues mContentValues=new ContentValues();
        mContentValues.put("Title", add_title.getText().toString().trim());
        mContentValues.put("Details",add_details.getText().toString().trim());
        mdbWriter.insert("Diary", null, mContentValues);
    }
}
