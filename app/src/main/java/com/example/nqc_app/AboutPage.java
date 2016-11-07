package com.example.nqc_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by USER on 2016/7/21.
 */
public class AboutPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        super.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_about_page);

        Button btnToContactPage= (Button)findViewById(R.id.btnToContactUsPage);

        btnToContactPage.setOnClickListener(btnListener);
    }
    private Button.OnClickListener btnListener = new Button.OnClickListener(){
        public void onClick(View v){

            switch (v.getId()){
                case R.id.btnToContactUsPage:{
                    Intent intent = new Intent();
                    intent.setClass(AboutPage.this,ContactUsPage.class);
                    startActivity(intent);
                    break;
                }

            }

        }
    };

}
