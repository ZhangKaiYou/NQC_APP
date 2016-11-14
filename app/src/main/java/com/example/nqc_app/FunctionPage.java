package com.example.nqc_app;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.nqc_app.util.ActionBarTitle;

public class FunctionPage extends AppCompatActivity {
    //建立標題Bar類別
    private ActionBarTitle actionBarTitle;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        super.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_function_page);
        initUI(); //建立UI元件
        preferences = getSharedPreferences("UserData",MODE_PRIVATE);
        ActionBar actionBar = getSupportActionBar(); //取得ActionBar
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        ActionBarTitle.applyFont(actionBar,this,this.getTitle().toString()); //執行ActionBarTilte
    }

    //按鈕監聽方法
    private Button.OnClickListener btnListener = new Button.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()){
                //點擊NFC頁按鈕
                case R.id.btnToNFCPage:{
                    //跳轉至點名功能頁
                    Intent intent = new Intent();
                    intent.setClass(FunctionPage.this,NFC_ClockFunction_Page.class);
                    startActivity(intent);
                    break;
                }
                //點擊關於頁面
                case R.id.btnToAboutPage:{
                    //跳轉至關於頁面
                    Intent intent = new Intent();
                    intent.setClass(FunctionPage.this,AboutPage.class);
                    startActivity(intent);
                    break;
                }
                //點擊我的資料
                case R.id.btnToMyDataPage:{
                    //跳轉我的資料
                    Intent intent = new Intent();
                    intent.setClass(FunctionPage.this,MyDataPage.class);
                    startActivity(intent);
                    break;
                }
            }
        }
    };

    //執行UI類別
    public void initUI(){
        //將Button指向變數
        Button btnToNFCPage = (Button)findViewById(R.id.btnToNFCPage);
        Button btnToQRCodePage = (Button)findViewById(R.id.btnToQRCodePage);
        Button btnToAboutPage = (Button)findViewById(R.id.btnToAboutPage);
        Button btnToMyDataPage = (Button)findViewById(R.id.btnToMyDataPage);

        //建立按鈕監聽方法
        btnToNFCPage.setOnClickListener(btnListener);
        btnToQRCodePage.setOnClickListener(btnListener);
        btnToAboutPage.setOnClickListener(btnListener);
        btnToMyDataPage.setOnClickListener(btnListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //設定Menu內容
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //點擊AboutUsMenuItem
            case R.id.action_AboutUs:
                Intent intent = new Intent();
                intent.setClass(FunctionPage.this,AboutPage.class);
                startActivity(intent);
                break;
            //點擊LogOutMenuItem
            case R.id.action_LogOut:
                new AlertDialog.Builder(FunctionPage.this)
                        .setTitle("登出")
                        .setMessage("確認是否要登出!?")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                preferences.edit().clear().commit();
                                Intent intentHome = new Intent();
                                intentHome.setClass(FunctionPage.this,HomePage.class);
                                startActivity(intentHome);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .show();
                break;
            //點擊ContactUsMenuItem
            case R.id.action_ContactUs:
                Intent intentContactUs = new Intent();
                intentContactUs.setClass(FunctionPage.this,ContactUsPage.class);
                startActivity(intentContactUs);
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            default:
                break;
        }
        return true;
    }

}
