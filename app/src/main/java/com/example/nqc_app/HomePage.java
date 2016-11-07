package com.example.nqc_app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.nqc_app.util.UserInfoDB;


public class HomePage extends Activity {
    private SharedPreferences preferences;
    private ImageView imgHomePage;
    private String UserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home_page);
        imgHomePage = (ImageView)findViewById(R.id.imgHomePage);
        imgHomePage.setOnClickListener(imgListener);
        preferences = getSharedPreferences("UserData",MODE_PRIVATE);
        UserID = preferences.getString("userid","");
    }

    private ImageView.OnClickListener imgListener = new ImageView.OnClickListener(){
        @Override
        public void onClick(View v){
            if(UserID.equals("")){
                    Intent intent = new Intent();
                    intent.setClass(HomePage.this,MainPage.class);
                    startActivity(intent);
            }else{
                    Intent intent = new Intent();
                    intent.setClass(HomePage.this,FunctionPage.class);
                    startActivity(intent);
                }
        }
    };

}
