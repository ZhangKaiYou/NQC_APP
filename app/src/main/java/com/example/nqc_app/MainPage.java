package com.example.nqc_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/**
 * Created by USER on 2016/7/26.
 */
public class MainPage extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_page);
        //將Button元件指向變數
        Button btnToLoginPage = (Button)findViewById(R.id.btnToLoginPage);
        Button btnSingUpPage = (Button)findViewById(R.id.btnToSignUpPage);
        //建立Button監聽
        btnToLoginPage.setOnClickListener(btnListener);
        btnSingUpPage.setOnClickListener(btnListener);
    }

    private Button.OnClickListener btnListener = new Button.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()){
                //點擊登入按鈕將跳轉至登入頁面
                case R.id.btnToLoginPage:{
                    Intent intent = new Intent();
                    intent.setClass(MainPage.this,LoginPage.class);
                    startActivity(intent);
                    break;
                }
                //點擊註冊按鈕將跳轉至註冊頁面
                case R.id.btnToSignUpPage:{
                    Intent intent = new Intent();
                    intent.setClass(MainPage.this,SignUpPage.class);
                    startActivity(intent);
                    break;
                }
            }
        }
    };
}
