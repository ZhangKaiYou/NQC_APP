package com.example.nqc_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nqc_app.util.ActionBarTitle;
import com.example.nqc_app.util.ConnectionClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by USER on 2016/7/21.
 */
public class EditMyDataPage extends AppCompatActivity {
    SharedPreferences preferences;
    ConnectionClass connectionClass;
    private TextView txtEditMyDataStudentID;
    private Button btnEditMyDataPW,btnEditMyDataCancel,btnEditMyDataOK;
    private EditText edtEditMyDataName,edtEditMyDataClass,edtEditMyDataStudentEmail;
    private RadioGroup rdbtnEditMyDataGender = null;
    private RadioButton rdbtnEditMyDataMan,rdbtnEditMyDataWoman;
    private String GenderText,NewUserPW;
    private Spinner spnEditMyDataStatue,spnEditMyDataDepartment;
    private String UserID,UserPW,UserName,UserGender,UserStatue,UserDepartment,UserClass,UserEmail;

    String [] statue = new String[]{"學生","老師","教授","TA"};
    String [] Department = new String[] {"資訊管理系","會計資訊系","財務金融系","企業管理系"};

    public static final Pattern EMAIL_PATTERN = Pattern.compile("^\\w+\\.*\\w+@(\\w+\\.){1,5}[a-zA-Z]{2,3}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editmydata_page);
        connectionClass = new ConnectionClass();
        initUI();
        //設定Spinner 相關資訊
        ArrayAdapter<String> StudentStatue = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,statue);
        StudentStatue.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnEditMyDataStatue.setAdapter(StudentStatue);

        ArrayAdapter<String> StudentDepartment = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,Department);
        StudentDepartment.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnEditMyDataDepartment.setAdapter(StudentDepartment);

        preferences = getSharedPreferences("UserData",MODE_PRIVATE);
        ActionBar actionBar = getSupportActionBar(); //取得ActionBar
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        ActionBarTitle.applyFont(actionBar,this,this.getTitle().toString()); //執行ActionBarTilte
        readUserData();
    }

    public void initUI(){
        spnEditMyDataStatue = (Spinner)findViewById(R.id.spnEditMyDataStatue);
        spnEditMyDataDepartment = (Spinner)findViewById(R.id.spnEditMyDataDepartment);

        txtEditMyDataStudentID = (TextView)findViewById(R.id.txtEditMyDataStudentID);
        btnEditMyDataPW = (Button)findViewById(R.id.btnEditMyDataPW);
        btnEditMyDataCancel = (Button)findViewById(R.id.btnEditMyDataCancel);
        btnEditMyDataOK = (Button)findViewById(R.id.btnEditMyDataOK);

        edtEditMyDataName = (EditText)findViewById(R.id.edtEditMyDataName);
        edtEditMyDataClass = (EditText)findViewById(R.id.edtEditMyDataClass);
        edtEditMyDataStudentEmail = (EditText)findViewById(R.id.edtEditMyDataStudentEmail);

        rdbtnEditMyDataGender = (RadioGroup)findViewById(R.id.rdgroupEditMyDataGender);
        rdbtnEditMyDataMan = (RadioButton)findViewById(R.id.rdbtnEditMyDataMan);
        rdbtnEditMyDataWoman = (RadioButton)findViewById(R.id.rdbtnEditMyDataWoman);

        btnEditMyDataPW.setOnClickListener(btnListener);
        btnEditMyDataCancel.setOnClickListener(btnListener);
        btnEditMyDataOK.setOnClickListener(btnListener);
    }

    public void readUserData(){
        UserID = preferences.getString("userid","");
        UserPW = preferences.getString("userpw","");
        UserName = preferences.getString("username","");
        UserGender = preferences.getString("usergender","");
        UserStatue = preferences.getString("userstatue","");
        UserDepartment = preferences.getString("userdepartment","");
        UserClass = preferences.getString("userclass","");
        UserEmail = preferences.getString("useremail","");

        txtEditMyDataStudentID.setText("學號：" + UserID);

        edtEditMyDataName.setText(UserName);
        edtEditMyDataStudentEmail.setText(UserEmail);
        edtEditMyDataClass.setText(UserClass);

        if (UserGender.equals("男")){
            rdbtnEditMyDataMan.setChecked(true);
        }else {
            rdbtnEditMyDataWoman.setChecked(true);
        }

        if (UserDepartment.equals("資訊管理系")){
            spnEditMyDataDepartment.setSelection(0);
        }else if(UserDepartment.equals("會計資訊系")){
            spnEditMyDataDepartment.setSelection(1);
        }else if(UserDepartment.equals("財務金融系")){
            spnEditMyDataDepartment.setSelection(2);
        }else {
            spnEditMyDataDepartment.setSelection(3);
        }

        if (UserStatue.equals("學生")){
            spnEditMyDataStatue.setSelection(0);
        }else if(UserStatue.equals("老師")){
            spnEditMyDataStatue.setSelection(1);
        }else if(UserStatue.equals("教授")){
            spnEditMyDataStatue.setSelection(2);
        }else {
            spnEditMyDataStatue.setSelection(3);
        }

    }
    private Button.OnClickListener btnListener  = new Button.OnClickListener(){
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btnEditMyDataCancel:
                    Intent intent = new Intent();
                    intent.setClass(EditMyDataPage.this,FunctionPage.class);
                    startActivity(intent);
                    break;
                case R.id.btnEditMyDataPW:
                    LayoutInflater layoutInflater = LayoutInflater.from(EditMyDataPage.this);
                    View v = layoutInflater.inflate(R.layout.activity_editmydatapw_item,null);
                    final AlertDialog dialogBuilder = new AlertDialog.Builder(EditMyDataPage.this).create();
                    final EditText edtOPWItem = (EditText)v.findViewById(R.id.edtEditOPWItem);
                    final EditText edtNPWItem = (EditText)v.findViewById(R.id.edtEditNPWItem);
                    final EditText edtCKNPWItem = (EditText)v.findViewById(R.id.edtEditCKNPWItem);
                    Button btnEditPWOK = (Button)v.findViewById(R.id.btnEditPWOK);
                    Button btnEditPWCancel = (Button)v.findViewById(R.id.btnEditPWCancel);

                    btnEditPWOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String OPW,NPW;
                            NPW = edtNPWItem.getText().toString();
                            OPW = edtOPWItem.getText().toString();
                            NewUserPW = edtCKNPWItem.getText().toString();
                            if(OPW.equals("")||NPW.equals("")||NewUserPW.equals("")){
                                Toast.makeText(EditMyDataPage.this,"資料請填寫完整!",Toast.LENGTH_SHORT).show();
                            }
                            if(OPW.equals(UserPW)){
                                if (NPW.equals(NewUserPW)){

                                    UpdateUserPW updateUserPW = new UpdateUserPW();
                                    updateUserPW.execute("");
                                    Toast.makeText(EditMyDataPage.this,"密碼變更完成!",Toast.LENGTH_SHORT).show();
                                    dialogBuilder.cancel();
                                }else {
                                    Toast.makeText(EditMyDataPage.this,"新密碼與確認密碼不相同!",Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(EditMyDataPage.this,"舊密碼輸入錯誤!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    btnEditPWCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogBuilder.cancel();
                        }
                    });
                    dialogBuilder.setView(v);
                    dialogBuilder.show();
                    break;

                case R.id.btnEditMyDataOK:
                    if (rdbtnEditMyDataGender.getCheckedRadioButtonId() == (R.id.rdbtnEditMyDataMan)){
                        GenderText = "男";
                    }else if(rdbtnEditMyDataGender.getCheckedRadioButtonId() == (R.id.rdbtnEditMyDataWoman)){
                        GenderText = "女";
                    }else {
                        Toast.makeText(getApplicationContext(),"請選擇性別!",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if(edtEditMyDataName.getText().toString().equals("") || edtEditMyDataClass.getText().toString().equals("")|| edtEditMyDataStudentEmail.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(),"請檢查資料是否輸入齊全",Toast.LENGTH_SHORT).show();
                        break;
                    }

                    if(isValidEmail(edtEditMyDataStudentEmail.getText().toString())){
                        Toast.makeText(getApplicationContext(),"請輸入正確的Email格式!",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    UpdateUserInfo updateUserInfo = new UpdateUserInfo();
                    updateUserInfo.execute("");
                    break;
            }
        }
    };

    public void DeleteOldUserData(){
        preferences.edit()
                .remove("username")
                .remove("usergender")
                .remove("useremail")
                .remove("userstatue")
                .remove("userdepartment")
                .remove("userclass")
                .commit();
    }

    public void WriteNewUserData(){
        String StudentName = edtEditMyDataName.getText().toString();
        String StudentEmail = edtEditMyDataStudentEmail.getText().toString();
        String StudentStatue = spnEditMyDataStatue.getSelectedItem().toString();
        String StudentDepartment = spnEditMyDataDepartment.getSelectedItem().toString();
        String StudentClass = edtEditMyDataClass.getText().toString();

        preferences.edit()
                .putString("username",StudentName)
                .putString("usergender",GenderText)
                .putString("useremail",StudentEmail)
                .putString("userstatue",StudentStatue)
                .putString("userdepartment",StudentDepartment)
                .putString("userclass",StudentClass)
                .commit();
    }

    public class UpdateUserInfo extends AsyncTask<String,String,String>{
        String z ="";
        Boolean isSuccess = false;

        @Override
        protected void onPostExecute(String r){
            Toast.makeText(EditMyDataPage.this,r, Toast.LENGTH_SHORT).show();
            if(isSuccess == true){
                DeleteOldUserData();
                WriteNewUserData();
                readUserData();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                Connection con = connectionClass.CONN();
                if(con ==  null){
                    z = "伺服器連接失敗!";
                }else {
                    String dates = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                    String query = "Update 使用者資訊 set 使用者名稱='" + UserName + "',性別='" + UserGender +"',電子郵件='"
                            + UserEmail + "',身分='" + UserStatue + "',系別='" + UserDepartment + "',班級='" + UserClass + "',時間='"
                            + dates +"' where 帳號='" + UserID + "'";
                    PreparedStatement preparedStatement = con.prepareStatement(query);
                    preparedStatement.executeUpdate();
                    z = "使用者資訊更新成功!";
                    isSuccess =true;
                }
            }catch (Exception ex){
                isSuccess = false;
                z = "Exceptions";
            }
            return z;
        }
    }

    public class UpdateUserPW extends AsyncTask<String,String,String>{
        String z ="";
        Boolean isSuccess = false;

        @Override
        protected void onPostExecute(String r){
            Toast.makeText(EditMyDataPage.this,r, Toast.LENGTH_SHORT).show();
            if(isSuccess == true){
                preferences.edit()
                        .remove("userpw")
                        .commit();
                preferences.edit()
                        .putString("userpw",NewUserPW)
                        .commit();
                readUserData();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                Connection con = connectionClass.CONN();
                if(con ==  null){
                    z = "伺服器連接失敗!";
                }else {
                    String dates = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                    String query = "Update 使用者資訊 set 密碼='" + UserPW + "',時間='" + dates +"' where 帳號='" + UserID + "'";
                    PreparedStatement preparedStatement = con.prepareStatement(query);
                    preparedStatement.executeUpdate();
                    z = "使用者資訊更新成功!";
                    isSuccess =true;
                }
            }catch (Exception ex){
                isSuccess = false;
                z = "Exceptions";
            }
            return z;
        }
    }

    public static boolean isValidEmail(String email) {
        if (EMAIL_PATTERN.matcher(email).matches()) {
            return  false;
        }
        return true;
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
                intent.setClass(EditMyDataPage.this,AboutPage.class);
                startActivity(intent);
                break;
            //點擊LogOutMenuItem
            case R.id.action_LogOut:
                new AlertDialog.Builder(EditMyDataPage.this)
                        .setTitle("登出")
                        .setMessage("確認是否要登出!?")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                preferences.edit().clear().commit();
                                Intent intentHome = new Intent();
                                intentHome.setClass(EditMyDataPage.this,HomePage.class);
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
                intentContactUs.setClass(EditMyDataPage.this,ContactUsPage.class);
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
