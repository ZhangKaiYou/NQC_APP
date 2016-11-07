package com.example.nqc_app;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class FunctionPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        super.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_function_page);

        Button btnToNFCPage = (Button)findViewById(R.id.btnToNFCPage);
        Button btnToQRCodePage = (Button)findViewById(R.id.btnToQRCodePage);
        Button btnToAboutPage = (Button)findViewById(R.id.btnToAboutPage);
        Button btnToMyDataPage = (Button)findViewById(R.id.btnToMyDataPage);

        btnToNFCPage.setOnClickListener(btnListener);
        btnToQRCodePage.setOnClickListener(btnListener);
        btnToAboutPage.setOnClickListener(btnListener);
        btnToMyDataPage.setOnClickListener(btnListener);

    }
    private Button.OnClickListener btnListener = new Button.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()){
                case R.id.btnToNFCPage:{
                    Intent intent = new Intent();
                    intent.setClass(FunctionPage.this,NFC_ClockFunction_Page.class);
                    startActivity(intent);
                    break;
                }
                case R.id.btnToAboutPage:{
                    Intent intent = new Intent();
                    intent.setClass(FunctionPage.this,AboutPage.class);
                    startActivity(intent);
                    break;
                }
                case R.id.btnToMyDataPage:{
                    Intent intent = new Intent();
                    intent.setClass(FunctionPage.this,MyDataPage.class);
                    startActivity(intent);
                    break;
                }
             }
            }

    };
}
