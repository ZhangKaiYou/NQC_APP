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
    ConnectionClass connectionClass;
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
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^\\w+\\.*\\w+@(\\w+\\.){1,5}[a-zA-Z]{2,3}$");

    @Override
    protected void onCreate(Bundle savedInstabceState){
        super.onCreate(savedInstabceState);
        setContentView(R.layout.activtiy_signup_page);
        connectionClass = new ConnectionClass();
        initUI();
        //設定Spinner 相關資訊
        ArrayAdapter<String> StudentStatue = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,statue);
        StudentStatue.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnSignUpStatue.setAdapter(StudentStatue);

        ArrayAdapter<String> StudentDepartment = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,Department);
        StudentDepartment.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnSignUpDepartment.setAdapter(StudentDepartment);

        preferences = getSharedPreferences("UserData",MODE_PRIVATE);
        UserID = preferences.getString("userid","");
        UserPW = preferences.getString("userpw","");
    }

    public void initUI(){
        spnSignUpStatue = (Spinner)findViewById(R.id.spnSignUpStatue);
        spnSignUpDepartment = (Spinner)findViewById(R.id.spnSignUpDepartment);

        edtSignUpName = (EditText)findViewById(R.id.edtSignUpName);
        edtSignUpClass = (EditText)findViewById(R.id.edtSignUpClass);
        edtSignUpStudentID = (EditText)findViewById(R.id.edtSignUpStudentID);
        edtSignUpStudentEmail = (EditText)findViewById(R.id.edtSignUpStudentEmail);
        edtSignUpPW = (EditText)findViewById(R.id.edtSignUpPW);
        edtSignUpCKPW = (EditText)findViewById(R.id.edtSignUpCKPW);
        Gender = (RadioGroup)findViewById(R.id.rdgroupSignUpGender);
        btnSignUpOK = (Button)findViewById(R.id.btnSignUpOK);
        btnSignUpClear = (Button)findViewById(R.id.btnSingUpClear);
        btnSignUpOK.setOnClickListener(btnListener);
        btnSignUpClear.setOnClickListener(btnListener);
    }

    private Button.OnClickListener btnListener = new Button.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()){
                case R.id.btnSignUpOK:{

                    if (Gender.getCheckedRadioButtonId() == (R.id.rdbtnSignUpMan)){
                        GenderText = "男";
                    }else if(Gender.getCheckedRadioButtonId() == (R.id.rdbtnSignUpWoman)){
                        GenderText = "女";
                    }else {
                        Toast.makeText(getApplicationContext(),"請選擇性別!",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if(edtSignUpName.getText().toString().equals("") || edtSignUpClass.getText().toString().equals("")|| edtSignUpStudentID.getText().toString().equals("")|| edtSignUpPW.getText().toString().equals("")|| edtSignUpCKPW.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(),"請檢查資料是否輸入齊全",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if(checkStudentIDData(edtSignUpStudentID.getText().toString())){
                        Toast.makeText(getApplicationContext(),"請再次確認學號是否為第一碼英文( N or n ) + 7位數字!",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if(!edtSignUpPW.getText().toString().equals(edtSignUpCKPW.getText().toString())){
                        Toast.makeText(getApplicationContext(),"密碼與確認密碼不相符!",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if(isValidEmail(edtSignUpStudentEmail.getText().toString())){
                        Toast.makeText(getApplicationContext(),"請輸入正確的Email格式!",Toast.LENGTH_SHORT).show();
                        break;
                    }

                    new AlertDialog.Builder(SignUpPage.this)
                            .setTitle("登入")
                            .setMessage("註冊成功！\n歡迎使用本應用程式！")
                            .setPositiveButton("確定", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialoginterface, int i) {
                                    AddUserInfo addUserInfo = new AddUserInfo();
                                    addUserInfo.execute("");
                                    Intent intent = new Intent();
                                    intent.setClass(SignUpPage.this, FunctionPage.class);
                                    startActivity(intent);
                                }
                            })
                            .show();
                    break;
                }
                case R.id.btnSingUpClear:{
                    edtSignUpStudentID.setText("");
                    edtSignUpPW.setText("");
                    edtSignUpCKPW.setText("");
                    edtSignUpName.setText("");
                    edtSignUpClass.setText("");
                    edtSignUpStudentEmail.setText("");
                    Gender.clearCheck();
                    spnSignUpStatue.setSelection(0);
                    spnSignUpDepartment.setSelection(0);
                    break;
                }
            }
        }
    };

    public class AddUserInfo extends AsyncTask<String,String,String>{

        String z = "";
        Boolean isSuccess = false;
        String StudentID = edtSignUpStudentID.getText().toString();
        String StudentPW = edtSignUpPW.getText().toString();
        String StudentName = edtSignUpName.getText().toString();
        String StudentEmail = edtSignUpStudentEmail.getText().toString();
        String StudentStatue = spnSignUpStatue.getSelectedItem().toString();
        String StudentDepartment = spnSignUpDepartment.getSelectedItem().toString();
        String StudentClass = edtSignUpClass.getText().toString();

        @Override
        protected void onPostExecute(String r){
            Toast.makeText(SignUpPage.this,r,Toast.LENGTH_SHORT).show();

            if(isSuccess){
                Toast.makeText(SignUpPage.this,r,Toast.LENGTH_SHORT).show();
                if(UserID.equals("")||UserPW.equals("")){
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
                Connection con = new ConnectionClass().CONN();
                if(con == null){
                    z = "伺服器連接失敗!";
                }else {
                    String dates = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                    String query = "insert into 使用者資訊 (帳號,密碼,使用者名稱,性別,電子郵件,身分,系別,班級,時間) values ('"
                            + StudentID + "','" + StudentPW + "','" + StudentName + "','" + GenderText  + "','" + StudentEmail + "','" + StudentStatue+ "','" + StudentDepartment + "','" + StudentClass + "','" + dates + "')" ;
                    PreparedStatement preparedStatement = con.prepareStatement(query);
                    preparedStatement.executeUpdate();
                    z = "使用者資料註冊成功!";
                    isSuccess = true;
                }
            }catch (Exception ex){
                isSuccess =false;
                z = "Exceptions";
            }
            return z;
        }
    }

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

    public static boolean isValidEmail(String email) {
        if (EMAIL_PATTERN.matcher(email).matches()) {
            return  false;
        }
        return true;
    }

}
