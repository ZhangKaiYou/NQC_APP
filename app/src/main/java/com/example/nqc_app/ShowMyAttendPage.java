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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nqc_app.util.ActionBarTitle;
import com.example.nqc_app.util.ConnectionClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by USER on 2016/7/21.
 */
public class ShowMyAttendPage extends AppCompatActivity{
    //使用者資料變數建立
    ConnectionClass connectionClass;
    SharedPreferences preferences;

    //UI變數建立
    Button btnAttendShowAll,btnAttendGetClassList;
    TextView txtShowAttend;
    ListView listAttend;
    Spinner spnAttendYear,spnAttendSemester;
    String UserID,ClassIDArea;

    //課程資料二維陣列建立
    List<Map<String,String>> ClassListADA = new ArrayList<Map<String,String>>();
    //查詢所有缺曠紀錄
    List<Map<String,String>> AllShowAttendADA = new ArrayList<Map<String,String>>();
    //查詢個別缺曠紀錄
    List<Map<String,String>> SelectShowAttendADA = new ArrayList<Map<String,String>>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showattend_page);
        connectionClass = new ConnectionClass();
        preferences = getSharedPreferences("UserData",MODE_PRIVATE); //取得內部儲存檔案
        readUserData(); //讀取使用者資料
        initUI(); //執行UI建立
        setSpinnerData();
        DBGetUserClassList dbGetUserClassList = new DBGetUserClassList(); //取得課程資料功能類別建立
        dbGetUserClassList.execute(""); //執行課程資料取得功能
        DBGetUserAttend dbGetUserAttend = new DBGetUserAttend();
        dbGetUserAttend.execute("");
        ActionBar actionBar = getSupportActionBar(); //取得ActionBar
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        ActionBarTitle.applyFont(actionBar,this,this.getTitle().toString()); //執行ActionBarTilte
    }

    private Button.OnClickListener btnListener = new Button.OnClickListener(){

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btnAttendShowAll:
                    int[] AttendListView = {R.id.txtShowAttendListDay,R.id.txtShowAttendListStatue}; //設定顯示清單元件
                    String[] from2 = {"簽到日期","出席狀況"};
                    final SimpleAdapter ShowAttendList = new SimpleAdapter(ShowMyAttendPage.this,AllShowAttendADA,R.layout.activity_showattendlist_itme,from2,AttendListView); //設定資料陣列
                    //設定陣列指向
                    listAttend.setAdapter(ShowAttendList);
                    ShowAttendList.notifyDataSetChanged();
                    txtShowAttend.setText("目前清單顯示狀態：顯示全部");
                    break;
                case R.id.btnAttendGetClassList:  //取得課堂資訊按鈕

                    LayoutInflater layoutInflater = LayoutInflater.from(ShowMyAttendPage.this); //建立介面類別
                    View v = layoutInflater.inflate(R.layout.activity_classlist_item,null); //設定清單介面
                    final AlertDialog dialogBuilder = new AlertDialog.Builder(ShowMyAttendPage.this).create();
                    //設定清單元件
                    ListView lstClassList = (ListView)v.findViewById(R.id.lstClassList);
                    final TextView txtClassItemTitle = (TextView)v.findViewById(R.id.txtClassListTitle);
                    Button btnClassListCancel = (Button)v.findViewById(R.id.btnClassListCancel);
                    Button btnClassListOK = (Button)v.findViewById(R.id.btnClassListOK);
                    //設定資料來源
                    String[] from = {"課程名稱"};
                    int[] ClassListView = {R.id.txtClassListItemName}; //設定顯示清單
                    final SimpleAdapter ClassList = new SimpleAdapter(ShowMyAttendPage.this,ClassListADA,R.layout.activity_classlistitem_item,from,ClassListView); //設定資料陣列
                    final String[] ShowClass = new String[1];
                    ClassList.notifyDataSetChanged();
                    //設定陣列指向
                    lstClassList.setAdapter(ClassList);
                    //清單點選監聽
                    lstClassList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            HashMap<String, String> obj = (HashMap<String, String>) ClassListADA.get(i);
                            txtClassItemTitle.setText("你選擇了：" + obj.get("課程名稱"));
                            ClassIDArea = obj.get("課程編號");
                            ShowClass[0] = obj.get("課程名稱");
                        }
                    });
                    //清單按鈕點選監聽
                    btnClassListOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SelectShowAttendADA.clear();
                            txtShowAttend.setText("你目前選擇的課程：" + ShowClass[0]);
                            DBGetUserSelectClassList dbGetUserSelectClassList = new DBGetUserSelectClassList();
                            dbGetUserSelectClassList.execute("");
                            dialogBuilder.cancel();
                        }
                    });
                    //清單點選監聽
                    btnClassListCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogBuilder.cancel();
                        }
                    });
                    dialogBuilder.setView(v);
                    dialogBuilder.show();

                    break;
            }
        }
    };


    public void initUI(){
        //將Button指向變數
        btnAttendShowAll = (Button)findViewById(R.id.btnAttendShowAll);
        btnAttendGetClassList = (Button)findViewById(R.id.btnAttendGetClassList);
        //將文字指向變數
        txtShowAttend = (TextView)findViewById(R.id.txtShowAttend);
        //將ListView指向變數
        listAttend = (ListView)findViewById(R.id.lstAttnedList);
        //將Spinner指向變數
        spnAttendYear = (Spinner)findViewById(R.id.spnAttendYear);
        spnAttendSemester = (Spinner)findViewById(R.id.spnAttendSemester);
        //建立Button點擊監聽
        btnAttendShowAll.setOnClickListener(btnListener);
        btnAttendGetClassList.setOnClickListener(btnListener);
        //建立Spinner選擇監聽
        spnAttendYear.setOnItemSelectedListener(spnListener);
        spnAttendSemester.setOnItemSelectedListener(spnListener);
    }

    private Spinner.OnItemSelectedListener spnListener = new Spinner.OnItemSelectedListener(){
        //偵測Spinner選擇
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            DBGetUserAttend dbGetUserAttend = new DBGetUserAttend();
            dbGetUserAttend.execute("");
            DBGetUserClassList dbGetUserClassList = new DBGetUserClassList(); //取得課程資料功能類別建立
            dbGetUserClassList.execute(""); //執行課程資料取得功能
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            //選擇後重新查詢資料

        }
    };

    public void setSpinnerData(){
        //Spinner資料建立
        String [] Semester = new String [] {"1","2"};
        ArrayList YearList = new ArrayList();
        int YearNow = Integer.parseInt(new SimpleDateFormat("yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime()));
        for (int i = 0; i <= 3; i++){
            int yearLast = YearNow - 1911 - i;
            String yearToString = Integer.toString(yearLast);
            YearList.add(yearToString);
        }

        //設定Semester Spinner 相關資訊
        ArrayAdapter<String> SemesterADA = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,Semester);
        SemesterADA.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnAttendSemester.setAdapter(SemesterADA);
        //設定Year Spinner 相關資訊
        ArrayAdapter<String> YearADA = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,YearList);
        YearADA.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnAttendYear.setAdapter(YearADA);
    }


    public void readUserData(){
        //取得使用者ID、名字
        UserID = preferences.getString("userid","");
    }

    public class DBGetUserAttend extends AsyncTask<String,String,String>{
        String z = "";
        Boolean isSuccess = false;
        //取得Spinner選擇項目
        String year = spnAttendYear.getSelectedItem().toString();
        String Semester = spnAttendSemester.getSelectedItem().toString();

        @Override
        protected void onPostExecute(String z){
            if(isSuccess){
                int[] AttendListView = {R.id.txtShowAttendListDay,R.id.txtShowAttendListStatue}; //設定顯示清單元件
                String[] from2 = {"簽到日期","出席狀況"};
                 SimpleAdapter ShowAttendList = new SimpleAdapter(ShowMyAttendPage.this,AllShowAttendADA,R.layout.activity_showattendlist_itme,from2,AttendListView); //設定資料陣列
                //設定陣列指向
                listAttend.setAdapter(ShowAttendList);
                ShowAttendList.notifyDataSetChanged();
            }else {
                Toast.makeText(ShowMyAttendPage.this,z,Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                Connection con = connectionClass.CONN();
                if(con == null){
                    z = "伺服器連接失敗!";
                }else {

                    //SQL查詢指令
                    String query = "select 簽到日期,出席狀況 from 出席紀錄 where 簽到簽退 = '簽到' and  學年度 ='" + year +"' and 學期 ='" + Semester + "' and 學生代號='" + UserID + "'";
                    //DB資料取得
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    AllShowAttendADA.clear();
                    //DB各項資料裝箱至ClassListInfo
                    while (rs.next()) {
                        Map<String,String> AttendList = new HashMap<String, String>();
                        AttendList.put("簽到日期",rs.getString("簽到日期"));
                        AttendList.put("出席狀況",rs.getString("出席狀況"));
                        //ClassListInfo裝至ClassListADA二維陣列
                        AllShowAttendADA.add(AttendList);
                    }
                    //資料取得成功回報
                    isSuccess = true;
                }
            }catch (Exception e){
                //資料取得失敗回報
                isSuccess = false;

                z = "資料取得失敗!";
            }
            //回覆資料取得狀態訊息回onPostExecute
            return z;
        }
    }


    public class DBGetUserSelectClassList extends  AsyncTask<String,String,String>{
        String z = "";
        Boolean isSuccess ;
        String year = spnAttendYear.getSelectedItem().toString();
        String Semester = spnAttendSemester.getSelectedItem().toString();
        @Override
        protected void onPostExecute(String z){
            if(isSuccess){
                int[] AttendListView = {R.id.txtShowAttendListDay,R.id.txtShowAttendListStatue}; //設定顯示清單元件
                String[] from2 = {"簽到日期","出席狀況"};
                final SimpleAdapter ShowAttendList = new SimpleAdapter(ShowMyAttendPage.this,SelectShowAttendADA,R.layout.activity_showattendlist_itme,from2,AttendListView); //設定資料陣列
                //設定陣列指向
                listAttend.setAdapter(ShowAttendList);
                ShowAttendList.notifyDataSetChanged();
            }else {
                Toast.makeText(ShowMyAttendPage.this,z,Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                Connection con = connectionClass.CONN();
                if(con == null){
                    z = "伺服器連接失敗!";
                }else {


                    //SQL查詢指令
                    String query = "select 簽到日期,出席狀況 from 出席紀錄 where 簽到簽退 = '簽到'  and  學生代號 ='" + UserID + "' and 課程代號 ='" + ClassIDArea + "'";
                    //DB資料取得
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    SelectShowAttendADA.clear();
                    //DB各項資料裝箱至ClassListInfo
                    while (rs.next()) {
                        Map<String,String> AttendList = new HashMap<String, String>();
                        AttendList.put("簽到日期",rs.getString("簽到日期"));
                        AttendList.put("出席狀況",rs.getString("出席狀況"));
                        //ClassListInfo裝至ClassListADA二維陣列
                        SelectShowAttendADA.add(AttendList);
                    }
                    //資料取得成功回報
                    isSuccess = true;
                }
            }catch (Exception e){
                //資料取得失敗回報
                isSuccess = false;

                z = "資料取得失敗!";
            }
            //回覆資料取得狀態訊息回onPostExecute
            return z;
        }
    }



    public class DBGetUserClassList extends AsyncTask<String,String,String> {
        String z = ""; //建立回報訊息變數
        Boolean isSuccess = false; //建立辨別成功變數
        String year = spnAttendYear.getSelectedItem().toString();
        String Semester = spnAttendSemester.getSelectedItem().toString();
        @Override
        protected void onPostExecute(String z){
            if(!isSuccess){
                Toast.makeText(ShowMyAttendPage.this,z,Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                Connection con = connectionClass.CONN();
                if(con == null){
                    z = "伺服器連接失敗!";
                }else {
                    //SQL查詢指令
                    String query = "select 課程資訊.*,課程學生清單.*,教室資訊.*  " +
                            "from 課程資訊,課程學生清單,教室資訊 " +
                            "where 教室資訊.教室編號 = 課程資訊.上課地點 and 課程資訊.課程編號 = 課程學生清單.課程編號 " +
                            " and 課程學生清單.學生代號 ='" + UserID + "'" +
                            " and 課程資訊.學年度 ='" + year + "' and 課程資訊.學期 ='" + Semester + "'";

                    //DB資料取得
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    ClassListADA.clear();
                    //DB各項資料裝箱至ClassListInfo
                    while (rs.next()) {
                        Map<String,String> ClassListInfo = new HashMap<String, String>();
                        ClassListInfo.put("課程編號",rs.getString("課程編號"));
                        ClassListInfo.put("課程名稱",rs.getString("課程名稱"));
                        ClassListInfo.put("教室編號",rs.getString("教室編號"));
                        ClassListInfo.put("教室名稱",rs.getString("教室名稱"));
                        //ClassListInfo裝至ClassListADA二維陣列
                        ClassListADA.add(ClassListInfo);
                    }
                    //資料取得成功回報
                    isSuccess = true;
                }
            }catch (Exception e){
                //資料取得失敗回報
                isSuccess = false;
                z = "資料取得失敗!";
            }
            //回覆資料取得狀態訊息回onPostExecute
            return z;
        }
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
                intent.setClass(ShowMyAttendPage.this,AboutPage.class);
                startActivity(intent);
                break;
            //點擊LogOutMenuItem
            case R.id.action_LogOut:
                new AlertDialog.Builder(ShowMyAttendPage.this)
                        .setTitle("登出")
                        .setMessage("確認是否要登出!?")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                preferences.edit().clear().commit();
                                Intent intentHome = new Intent();
                                intentHome.setClass(ShowMyAttendPage.this,HomePage.class);
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
                intentContactUs.setClass(ShowMyAttendPage.this,ContactUsPage.class);
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
