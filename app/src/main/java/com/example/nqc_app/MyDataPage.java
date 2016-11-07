package com.example.nqc_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by USER on 2016/7/21.
 */
public class MyDataPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceStace){
        super.onCreate(savedInstanceStace);
        super.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_mydata_page);

        Button btnToShowAttendPage = (Button)findViewById(R.id.btnToShowAttendPage);
        Button btnToEditMyDataPage = (Button)findViewById(R.id.btnToEditMyDataPage);

        btnToShowAttendPage.setOnClickListener(btnListener);
        btnToEditMyDataPage.setOnClickListener(btnListener);
    }

    private Button.OnClickListener btnListener = new Button.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()){
                case R.id.btnToShowAttendPage:{
                    Intent intent = new Intent();
                    intent.setClass(MyDataPage.this,ShowMyAttendPage.class);
                    startActivity(intent);
                    break;
                }
                case R.id.btnToEditMyDataPage:{
                    Intent intent = new Intent();
                    intent.setClass(MyDataPage.this,EditMyDataPage.class);
                    startActivity(intent);
                    break;
                }
            }

        }
    };
}
