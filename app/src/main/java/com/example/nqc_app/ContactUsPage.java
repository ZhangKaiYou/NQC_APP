package com.example.nqc_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


/**
 * Created by USER on 2016/7/21.
 */
public class ContactUsPage extends AppCompatActivity {
    //建立本地資料分享類別
    SharedPreferences preferences;
    //使用者資訊儲存字串
    private String UserInfo;
    private String UserID,UserName,UserGender,UserStatue,UserDepartment,UserClass,UserEmail;
    //EditText變數建立
    private EditText edtContactUsDetail,edtContactUsTitle;
    //Button變數建立
    private Button btnContactUsClear,btnContactUsSend;
    //Spinner變數建立
    private Spinner spnContactUsKind;
    //Spinner內容字串
    String[] Kind = new String[]{"軟體Bug","點名缺漏","行政疏失","軟體改善"};

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactus_page);
        //執行元件建立
        initUI();
        //取得使用者資訊
        preferences = getSharedPreferences("UserData",MODE_PRIVATE);
        getUserInfo();
        //建立Spinner
        ArrayAdapter<String> ContactUsKind = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,Kind);
        ContactUsKind.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnContactUsKind.setAdapter(ContactUsKind);
    }

    public void initUI(){
        //Spinner元件指向變數
        spnContactUsKind = (Spinner)findViewById(R.id.spnContactusKind);
        //EditText元件指向變數
        edtContactUsDetail = (EditText)findViewById(R.id.edtContactUsDetail);
        edtContactUsTitle = (EditText)findViewById(R.id.edtContactUsTitle);
        //Button元件指向變數
        btnContactUsClear = (Button)findViewById(R.id.btnContactUsClear);
        btnContactUsSend = (Button)findViewById(R.id.btnContactUsSend);
        //Button點擊監聽偵測
        btnContactUsSend.setOnClickListener(btnListener);
        btnContactUsClear.setOnClickListener(btnListener);
    }
    //按鈕監聽
    private Button.OnClickListener btnListener  =new Button.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()){
                //點擊清除輸入框內容
                case R.id.btnContactUsClear:
                    edtContactUsDetail.setText("");
                    edtContactUsTitle.setText("");
                    spnContactUsKind.setSelection(0);
                    break;
                //點擊寄出信
                case R.id.btnContactUsSend:
                    if(edtContactUsTitle.getText().toString().equals("")||edtContactUsDetail.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(),"標題和內容請勿空白!",Toast.LENGTH_SHORT).show();
                    }else {

                        UserInfo =( "\n" + "\n" + "\n" + "\n" + "\n" + "\n" +
                                "使用者學號：" + UserID + "\n" +
                                "使用者姓名：" + UserName + "\n" +
                                "使用者性別：" + UserGender + "\n" +
                                "使用者身分："+ UserStatue + "\n" +
                                "使用者系別：" + UserDepartment + "\n" +
                                "使用者班級：" + UserClass + "\n" +
                                "使用者Email：" + UserEmail + "\n");
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("plain/text");
                        intent.putExtra(Intent.EXTRA_EMAIL,new String[]{"zxcv71427@gmail.com"});
                        intent.putExtra(Intent.EXTRA_CC,UserEmail);
                        intent.putExtra(Intent.EXTRA_SUBJECT,"[" + spnContactUsKind.getSelectedItem().toString() + "]" + edtContactUsTitle.getText().toString() );
                        intent.putExtra(Intent.EXTRA_TEXT,edtContactUsDetail.getText().toString() + UserInfo);
                        startActivity(Intent.createChooser(intent,"Send Email"));
                        break;
                    }
            }
        }
    };
    //取得使用者類別
    public void getUserInfo(){
        preferences = getSharedPreferences("UserData",MODE_PRIVATE);
        UserID = preferences.getString("userid","");
        UserName = preferences.getString("username","");
        UserGender = preferences.getString("usergender","");
        UserStatue = preferences.getString("userstatue","");
        UserDepartment = preferences.getString("userdepartment","");
        UserClass = preferences.getString("userclass","");
        UserEmail = preferences.getString("useremail","");
    }
}
