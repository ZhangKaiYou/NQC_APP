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
    private EditText edtLoginStudentID,edtLoginStudentPW;
    private TextView txtForgetPW;
    private Button btnLoginOK, btnLoginClear;
    ConnectionClass connectionClass;
    private SharedPreferences preferences;
    private String UserID,UserPW;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        connectionClass = new ConnectionClass();
        initUI();
        preferences = getSharedPreferences("UserData",MODE_PRIVATE);
        UserID = preferences.getString("userid","");
        UserPW = preferences.getString("userpw","");
    }

    public void initUI(){
        edtLoginStudentID = (EditText)findViewById(R.id.edtLoginStudentID);
        edtLoginStudentPW = (EditText)findViewById(R.id.edtLoginPW);
        txtForgetPW = (TextView)findViewById(R.id.txtForgetPassword);
        btnLoginOK = (Button)findViewById(R.id.btnLoginOK);
        btnLoginClear = (Button)findViewById(R.id.btnLoginClear);

        btnLoginOK.setOnClickListener(btnListener);
        btnLoginClear.setOnClickListener(btnListener);
        txtForgetPW.setOnClickListener(btnListener);
    }

    private Button.OnClickListener btnListener = new Button.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()){
                case R.id.btnLoginOK:
                    DoLogin doLogin = new DoLogin();
                    doLogin.execute("");
                    break;

                case R.id.btnLoginClear:
                    edtLoginStudentID.setText("");
                    edtLoginStudentPW.setText("");
                    break;

                case R.id.txtForgetPassword:
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
        String z = "";
        Boolean isSuccess = false;
        String userid = edtLoginStudentID.getText().toString();
        String password = edtLoginStudentPW.getText().toString();

        String StudentID,StudentPW,StudentName,StudentGender,StudentEmail,StudentStatue,StudentDepartment,StudentClass;

        @Override
        protected void onPostExecute(String r){
            Toast.makeText(LoginPage.this,r,Toast.LENGTH_SHORT).show();

            if(isSuccess){
                Toast.makeText(LoginPage.this,r,Toast.LENGTH_SHORT).show();

                if(UserID.equals("")||UserPW.equals("")){
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
                Intent intent = new Intent();
                intent.setClass(LoginPage.this,FunctionPage.class);
                startActivity(intent);
            }
        }

        @Override
        protected String doInBackground(String... strings) {

         if(userid.trim().equals("") || password.trim().equals("")){
             z = "請輸入帳號或密碼!";
         }else {
             try {
                 Connection con = connectionClass.CONN();
                 if(con == null){
                     z = "伺服器連接失敗!";
                 }else {
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
