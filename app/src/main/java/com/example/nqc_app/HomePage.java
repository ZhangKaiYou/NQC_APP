package com.example.nqc_app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class HomePage extends Activity {
    //建立資料分享類別變數
    private SharedPreferences preferences;
    //建立Layout變數
    LinearLayout HomePage;
    //建立儲存UserID變數字串
    private String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home_page);
        HomePage = (LinearLayout)findViewById(R.id.HomePage); //設定Layout指向變數
        HomePage.setOnClickListener(layoutListener); //設定Layout點擊監聽
        preferences = getSharedPreferences("UserData",MODE_PRIVATE); //建立本地儲存資料
        UserID = preferences.getString("userid",""); //取得UserID資料
    }

    //建立Layout點擊偵測方法
    private LinearLayout.OnClickListener layoutListener= new ImageView.OnClickListener(){
        @Override
        public void onClick(View v){
            //判斷使否已有登入資料
            if(UserID.equals("")){
                    //若無跳準至主頁面
                    Intent intent = new Intent();
                    intent.setClass(HomePage.this,MainPage.class);
                    startActivity(intent);
            }else{
                    //若有直接跳轉至功能頁
                    Intent intent = new Intent();
                    intent.setClass(HomePage.this,FunctionPage.class);
                    startActivity(intent);
                }
        }
    };
}
