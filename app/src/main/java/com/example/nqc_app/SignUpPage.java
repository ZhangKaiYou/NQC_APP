package com.example.nqc_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.nqc_app.util.ConnectionClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by USER on 2016/7/25.
 */
public class SignUpPage extends AppCompatActivity {
    //建立資料庫連接類別變數
    ConnectionClass connectionClass;
    //建立分享本地資料類別變數
    SharedPreferences preferences;
    //Spinner設定
    private Spinner spnSignUpStatue,spnSignUpDepartment;
    String [] statue = new String[]{"學生","老師","教授","TA"};
    String [] Department = new String[] {"資訊管理系","會計資訊系","財務金融系","企業管理系"};
    //UI字串設定
    private EditText edtSignUpName, edtSignUpClass, edtSignUpStudentID, edtSignUpPW, edtSignUpCKPW,edtSignUpStudentEmail;
    private Button btnSignUpOK, btnSignUpClear;
    private RadioGroup Gender = null;
    private String GenderText = null;
    private String UserID,UserPW;
    //建立檢查EmailL格式
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^\\w+\\.*\\w+@(\\w+\\.){1,5}[a-zA-Z]{2,3}$");

    @Override
    protected void onCreate(Bundle savedInstabceState){
        super.onCreate(savedInstabceState);
        setContentView(R.layout.activtiy_signup_page);
        connectionClass = new ConnectionClass();
        initUI();
        //設定學生身分Spinner 相關資訊
        ArrayAdapter<String> StudentStatue = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,statue);
        StudentStatue.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnSignUpStatue.setAdapter(StudentStatue);
        //設定學生系別Spinner 相關資訊
        ArrayAdapter<String> StudentDepartment = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,Department);
        StudentDepartment.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnSignUpDepartment.setAdapter(StudentDepartment);
        //取得本地儲存資料
        preferences = getSharedPreferences("UserData",MODE_PRIVATE);
        UserID = preferences.getString("userid","");
        UserPW = preferences.getString("userpw","");
    }

    public void initUI(){
        //將Spinner 指向變數
        spnSignUpStatue = (Spinner)findViewById(R.id.spnSignUpStatue);
        spnSignUpDepartment = (Spinner)findViewById(R.id.spnSignUpDepartment);
        //將輸入框指向變數
        edtSignUpName = (EditText)findViewById(R.id.edtSignUpName);
        edtSignUpClass = (EditText)findViewById(R.id.edtSignUpClass);
        edtSignUpStudentID = (EditText)findViewById(R.id.edtSignUpStudentID);
        edtSignUpStudentEmail = (EditText)findViewById(R.id.edtSignUpStudentEmail);
        edtSignUpPW = (EditText)findViewById(R.id.edtSignUpPW);
        edtSignUpCKPW = (EditText)findViewById(R.id.edtSignUpCKPW);
        //設定單選群組指向變數
        Gender = (RadioGroup)findViewById(R.id.rdgroupSignUpGender);
        //設定Button指向變數
        btnSignUpOK = (Button)findViewById(R.id.btnSignUpOK);
        btnSignUpClear = (Button)findViewById(R.id.btnSingUpClear);
        //建立Button監聽類別
        btnSignUpOK.setOnClickListener(btnListener);
        btnSignUpClear.setOnClickListener(btnListener);
    }

    private Button.OnClickListener btnListener = new Button.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()){
                //按下確定註冊
                case R.id.btnSignUpOK:{
                    //判別性別單選內容
                    if (Gender.getCheckedRadioButtonId() == (R.id.rdbtnSignUpMan)){
                        GenderText = "男";
                    }else if(Gender.getCheckedRadioButtonId() == (R.id.rdbtnSignUpWoman)){
                        GenderText = "女";
                    }else {
                        Toast.makeText(getApplicationContext(),"請選擇性別!",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    //確認資料是否輸入完全
                    if(edtSignUpName.getText().toString().equals("") || edtSignUpClass.getText().toString().equals("")|| edtSignUpStudentID.getText().toString().equals("")|| edtSignUpPW.getText().toString().equals("")|| edtSignUpCKPW.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(),"請檢查資料是否輸入齊全",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    //確認學號輸入格式
                    if(checkStudentIDData(edtSignUpStudentID.getText().toString())){
                        Toast.makeText(getApplicationContext(),"請再次確認學號是否為第一碼英文( N or n ) + 7位數字!",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    //確認密碼輸入與檢查密碼是否相同
                    if(!edtSignUpPW.getText().toString().equals(edtSignUpCKPW.getText().toString())){
                        Toast.makeText(getApplicationContext(),"密碼與確認密碼不相符!",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    //確認輸入Email是否符合格式
                    if(isValidEmail(edtSignUpStudentEmail.getText().toString())){
                        Toast.makeText(getApplicationContext(),"請輸入正確的Email格式!",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    //跳出訊息窗告知註冊成功
                    new AlertDialog.Builder(SignUpPage.this)
                            .setTitle("登入")
                            .setMessage("註冊成功！\n歡迎使用本應用程式！")
                            .setPositiveButton("確定", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialoginterface, int i) {
                                    //執行使用者資料寫入資料庫
                                    AddUserInfo addUserInfo = new AddUserInfo();
                                    addUserInfo.execute("");
                                    //跳轉至功能頁面
                                    Intent intent = new Intent();
                                    intent.setClass(SignUpPage.this, FunctionPage.class);
                                    startActivity(intent);
                                }
                            })
                            .show();
                    break;
                }
                //按下清除按鈕
                case R.id.btnSingUpClear:{
                    //清除輸入框內容
                    edtSignUpStudentID.setText("");
                    edtSignUpPW.setText("");
                    edtSignUpCKPW.setText("");
                    edtSignUpName.setText("");
                    edtSignUpClass.setText("");
                    edtSignUpStudentEmail.setText("");
                    //清除單選內容
                    Gender.clearCheck();
                    //將Spinner內容還原預設
                    spnSignUpStatue.setSelection(0);
                    spnSignUpDepartment.setSelection(0);
                    break;
                }
            }
        }
    };
    //寫入資料庫類別
    public class AddUserInfo extends AsyncTask<String,String,String>{
        //建立回饋訊息資串
        String z = "";
        //建立判斷資料取得是否成功
        Boolean isSuccess = false;
        //取得輸入框資料
        String StudentID = edtSignUpStudentID.getText().toString();
        String StudentPW = edtSignUpPW.getText().toString();
        String StudentName = edtSignUpName.getText().toString();
        String StudentEmail = edtSignUpStudentEmail.getText().toString();
        String StudentStatue = spnSignUpStatue.getSelectedItem().toString();
        String StudentDepartment = spnSignUpDepartment.getSelectedItem().toString();
        String StudentClass = edtSignUpClass.getText().toString();

        @Override
        protected void onPostExecute(String r){
            //訊息回饋
            Toast.makeText(SignUpPage.this,r,Toast.LENGTH_SHORT).show();

            if(isSuccess){
                //資料寫入訊息回饋
                Toast.makeText(SignUpPage.this,r,Toast.LENGTH_SHORT).show();
                //判別是否已有資料
                if(UserID.equals("")||UserPW.equals("")){
                    //將資料寫入各儲存空間
                    preferences.edit()
                            .putString("userid",StudentID)
                            .putString("userpw",StudentPW)
                            .putString("username",StudentName)
                            .putString("usergender",GenderText)
                            .putString("useremail",StudentEmail)
                            .putString("userstatue",StudentStatue)
                            .putString("userdepartment",StudentDepartment)
                            .putString("userclass",StudentClass)
                            .commit();
                }
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                //執行資料庫連接
                Connection con = new ConnectionClass().CONN();
                if(con == null){
                    z = "伺服器連接失敗!";
                }else {
                    //執行寫入資料庫
                    String dates = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                    String query = "insert into 使用者資訊 (帳號,密碼,使用者名稱,性別,電子郵件,身分,系別,班級,時間) values ('"
                            + StudentID + "','" + StudentPW + "','" + StudentName + "','" + GenderText  + "','" + StudentEmail + "','" + StudentStatue+ "','" + StudentDepartment + "','" + StudentClass + "','" + dates + "')" ;
                    PreparedStatement preparedStatement = con.prepareStatement(query);
                    preparedStatement.executeUpdate();
                    //回饋資料庫寫入成功
                    z = "使用者資料註冊成功!";
                    isSuccess = true;
                }
            }catch (Exception ex){
                //資料庫寫入失敗
                isSuccess =false;
                z = "Exceptions";
            }
            return z;
        }
    }
    //檢查學號格式是否正確
    public boolean checkStudentIDData(String StudentID){
        byte[] ID = StudentID.getBytes();
        if(ID[0] == 78 || ID[0] == 110  ){
            if(StudentID.length() == 8){
                try{
                    String sevenNB =  StudentID.substring(1,8);
                    int i = Integer.parseInt(sevenNB);
                    return false;
                }catch (Exception e){
                    return true;
                }
            }
        }
        return true;
    }
    //檢查Email格式是否正確
    public static boolean isValidEmail(String email) {
        if (EMAIL_PATTERN.matcher(email).matches()) {
            return  false;
        }
        return true;
    }

}
