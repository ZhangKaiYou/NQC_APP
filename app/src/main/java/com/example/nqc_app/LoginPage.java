package com.example.nqc_app;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nqc_app.util.ConnectionClass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by USER on 2016/7/25.
 */
public class LoginPage extends AppCompatActivity{
    //建立元件變數
    private EditText edtLoginStudentID,edtLoginStudentPW;
    private TextView txtForgetPW;
    private Button btnLoginOK, btnLoginClear;
    //建立連接資料庫類別變數
    ConnectionClass connectionClass;
    //建立本地資料分享變數
    private SharedPreferences preferences;
    //建立使用者ID、PassWord儲存字串
    private String UserID,UserPW;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        //建立連接資料庫類別
        connectionClass = new ConnectionClass();
        //執行UI介面建立
        initUI();
        //取得User資料
        preferences = getSharedPreferences("UserData",MODE_PRIVATE);
        UserID = preferences.getString("userid","");
        UserPW = preferences.getString("userpw","");
    }

    public void initUI(){
        //將元件指向變數
        edtLoginStudentID = (EditText)findViewById(R.id.edtLoginStudentID);
        edtLoginStudentPW = (EditText)findViewById(R.id.edtLoginPW);
        txtForgetPW = (TextView)findViewById(R.id.txtForgetPassword);
        btnLoginOK = (Button)findViewById(R.id.btnLoginOK);
        btnLoginClear = (Button)findViewById(R.id.btnLoginClear);
        //建立按鈕及文字點擊監聽
        btnLoginOK.setOnClickListener(btnListener);
        btnLoginClear.setOnClickListener(btnListener);
        txtForgetPW.setOnClickListener(btnListener);
    }

    private Button.OnClickListener btnListener = new Button.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()){
                //點擊確認鈕
                case R.id.btnLoginOK:
                    //執行資料庫查詢
                    DoLogin doLogin = new DoLogin();
                    doLogin.execute("");
                    break;
                //點擊清除鈕
                case R.id.btnLoginClear:
                    //清除輸入按鈕內容
                    edtLoginStudentID.setText("");
                    edtLoginStudentPW.setText("");
                    break;
                //點擊忘記密碼鈕
                case R.id.txtForgetPassword:
                    //跳出提示視窗
                    new AlertDialog.Builder(LoginPage.this)
                            .setTitle("遺忘使用者密碼")
                            .setMessage("若遺忘密碼，請洽系辦助教!")
                            .setPositiveButton("確定", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialoginterface, int i) {
                                }
                            })
                            .show();
                    break;
            }
        }
    };

    public class DoLogin extends AsyncTask<String,String,String>{
        //建立回饋訊息資串
        String z = "";
        //判斷資料是否撈取成功
        Boolean isSuccess = false;
        //取得輸入框內容資料
        String userid = edtLoginStudentID.getText().toString();
        String password = edtLoginStudentPW.getText().toString();
        //建立儲存使用者相關資訊字串
        String StudentID,StudentPW,StudentName,StudentGender,StudentEmail,StudentStatue,StudentDepartment,StudentClass;

        @Override
        protected void onPostExecute(String r){
            //跳出提示訊息說明狀況
            Toast.makeText(LoginPage.this,r,Toast.LENGTH_SHORT).show();

            if(isSuccess){
                //跳出成功提示訊息
                Toast.makeText(LoginPage.this,r,Toast.LENGTH_SHORT).show();

                if(UserID.equals("")||UserPW.equals("")){ //判斷是否已有儲存資料
                    //將資料寫入各項空間
                    preferences.edit()
                            .putString("userid",StudentID)
                            .putString("userpw",StudentPW)
                            .putString("username",StudentName)
                            .putString("usergender",StudentGender)
                            .putString("userstatue",StudentStatue)
                            .putString("userdepartment",StudentDepartment)
                            .putString("userclass",StudentClass)
                            .putString("useremail",StudentEmail)
                            .commit();
                }
                //跳轉至功能頁面
                Intent intent = new Intent();
                intent.setClass(LoginPage.this,FunctionPage.class);
                startActivity(intent);
            }
        }

        @Override
        protected String doInBackground(String... strings) {
         //判斷是否將資料輸入完整
         if(userid.trim().equals("") || password.trim().equals("")){
             z = "請輸入帳號或密碼!";
         }else {
             try {
                 //執行連接伺服器
                 Connection con = connectionClass.CONN();
                 if(con == null){
                     z = "伺服器連接失敗!";
                 }else {
                     //執行資料庫查詢
                     String query = "select * from 使用者資訊 where 帳號='" + userid + "' and 密碼='" + password + "'";
                     Statement stmt = con.createStatement();
                     ResultSet rs = stmt.executeQuery(query);

                     if(rs.next()){
                         z = "登入成功";
                         StudentID = rs.getString("帳號");
                         StudentPW = rs.getString("密碼");
                         StudentName = rs.getString("使用者名稱");
                         StudentGender = rs.getString("性別");
                         StudentEmail = rs.getString("電子郵件");
                         StudentStatue = rs.getString("身分");
                         StudentDepartment = rs.getString("系別");
                         StudentClass = rs.getString("班級");
                         isSuccess = true;
                     }else {
                         z = "無效的帳號密碼!";
                         isSuccess = false;
                     }
                 }
             }catch (Exception e){
                 isSuccess = false;
                 z = "Execption";
             }
         }
            return z;
        }
    }
}
