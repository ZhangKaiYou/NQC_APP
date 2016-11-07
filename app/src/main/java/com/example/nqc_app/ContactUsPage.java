package com.example.nqc_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nqc_app.util.UserInfoDB;

/**
 * Created by USER on 2016/7/21.
 */
public class ContactUsPage extends AppCompatActivity {
    SharedPreferences preferences;

    private String UserInfo;
    private EditText edtContactUsDetail,edtContactUsTitle;
    private Button btnContactUsClear,btnContactUsSend;
    private TextView txtContactUsUserID,txtContactUsUserName;
    private String UserID,UserPW,UserName,UserGender,UserStatue,UserDepartment,UserClass,UserEmail;

    private Spinner spnContactUsKind;
    String[] Kind = new String[]{"軟體Bug","點名缺漏","行政疏失","軟體改善"};
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        super.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_contactus_page);
        initUI();

        preferences = getSharedPreferences("UserData",MODE_PRIVATE);
        UserPW = preferences.getString("userpw","");
        UserID = preferences.getString("userid","");
        UserName = preferences.getString("username","");
        UserGender = preferences.getString("usergender","");
        UserStatue = preferences.getString("userstatue","");
        UserDepartment = preferences.getString("userdepartment","");
        UserClass = preferences.getString("userclass","");
        UserEmail = preferences.getString("useremail","");

        txtContactUsUserID.setText("學號：" + UserID);
        txtContactUsUserName.setText("姓名：" + UserName);

        ArrayAdapter<String> ContactUsKind = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,Kind);
        ContactUsKind.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnContactUsKind.setAdapter(ContactUsKind);
    }

    public void initUI(){
        spnContactUsKind = (Spinner)findViewById(R.id.spnContactusKind);
        edtContactUsDetail = (EditText)findViewById(R.id.edtContactUsDetail);
        edtContactUsTitle = (EditText)findViewById(R.id.edtContactUsTitle);
        btnContactUsClear = (Button)findViewById(R.id.btnContactUsClear);
        btnContactUsSend = (Button)findViewById(R.id.btnContactUsSend);
        txtContactUsUserID = (TextView)findViewById(R.id.txtContactUsUserID);
        txtContactUsUserName = (TextView)findViewById(R.id.txtContactUsUserName);

        btnContactUsSend.setOnClickListener(btnListener);
        btnContactUsClear.setOnClickListener(btnListener);
    }

    private Button.OnClickListener btnListener  =new Button.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()){
                case R.id.btnContactUsClear:
                    edtContactUsDetail.setText("");
                    edtContactUsTitle.setText("");
                    spnContactUsKind.setSelection(0);
                    break;
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
}
