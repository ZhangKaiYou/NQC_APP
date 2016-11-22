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
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nqc_app.util.ActionBarTitle;
import com.example.nqc_app.util.ConnectionClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aa2646 on 2016/11/15.
 */
public class ShowClassHoursPageT extends AppCompatActivity {
    //使用者資料變數建立
    ConnectionClass connectionClass;
    SharedPreferences preferences;

    //UI變數建立
    Button btnClassHrsShowAllT, btnClassHrsGetClassListT;
    TextView txtShowClassHrsT, txtShowClassHrsValueT;
    ListView listClassHrsT;
    String UserID,ClassIDArea,ClassNameArea,StudentIDAttendArea;
    int ClassHrsSelectSum;

    //使用者課程清單二維陣列建立
    List<Map<String,String>> ClassListADA = new ArrayList<Map<String,String>>();
    //該課程學生清單二維陣列建立
    List<Map<String,String>> DBGetClassAllStudentListADA = new ArrayList<Map<String,String>>();
    //總學習時數資料二維陣列建立
    List<Map<String,String>> ClassHrsSelectADA = new ArrayList<Map<String,String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showclasshourst_page);
        connectionClass = new ConnectionClass(); //執行連接資料庫類別
        preferences = getSharedPreferences("UserData",MODE_PRIVATE); //取得內部儲存檔案
        readUserData(); //讀取使用者資料
        initUI(); //執行介面元件建立
        DBGetUserClassList dbGetUserClassList = new DBGetUserClassList(); //取得課程資料功能類別建立
        dbGetUserClassList.execute(""); //執行課程資料取得功能

        ActionBar actionBar = getSupportActionBar(); //取得ActionBar
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        ActionBarTitle.applyFont(actionBar,this,this.getTitle().toString()); //執行ActionBarTilte
    }

    public void readUserData(){
        //取得使用者ID、名字
        UserID = preferences.getString("userid","");
    }

    public void initUI(){
        //將Button指向變數
        btnClassHrsGetClassListT = (Button)findViewById(R.id.btnClassHrsGetClassListT);
        btnClassHrsShowAllT = (Button)findViewById(R.id.btnClassHrsShowAllT);
        btnClassHrsGetClassListT.setVisibility(View.GONE);
        //將TextView指向變數
        txtShowClassHrsT = (TextView)findViewById(R.id.txtShowClassHrsT);
        txtShowClassHrsValueT = (TextView)findViewById(R.id.txtShowClassHrsValueT);
        //將ListView指向變數
        listClassHrsT = (ListView)findViewById(R.id.lstClassHrsListT);
        //建立Button點擊監聽
        btnClassHrsGetClassListT.setOnClickListener(btnListener);
        btnClassHrsShowAllT.setOnClickListener(btnListener);
        //建立ListView點擊監聽
        listClassHrsT.setOnItemClickListener(listListener);
    }

    //Button點擊監聽
    private Button.OnClickListener btnListener = new Button.OnClickListener(){

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                //點擊取得課程資料
//                case R.id.btnClassHrsGetClassListT:
//                    LayoutInflater layoutInflater = LayoutInflater.from(ShowClassHoursPageT.this); //建立介面類別
//                    View v = layoutInflater.inflate(R.layout.activity_classlist_item,null); //設定清單介面
//                    final AlertDialog dialogBuilder = new AlertDialog.Builder(ShowClassHoursPageT.this).create();
//                    //設定清單元件
//                    ListView lstClassList = (ListView)v.findViewById(R.id.lstClassList);
//                    final TextView txtClassItemTitle = (TextView)v.findViewById(R.id.txtClassListTitle);
//                    Button btnClassListCancel = (Button)v.findViewById(R.id.btnClassListCancel);
//                    Button btnClassListOK = (Button)v.findViewById(R.id.btnClassListOK);
//                    //設定資料來源
//                    String[] from = {"課程名稱"};
//                    int[] ClassListView = {R.id.txtClassListItemName}; //設定顯示清單
//                    final SimpleAdapter ClassList = new SimpleAdapter(ShowClassHoursPageT.this,ClassListADA,R.layout.activity_classlistitem_item,from,ClassListView); //設定資料陣列
//                    final String[] ShowClass = new String[1];
//                    //設定陣列指向
//                    lstClassList.setAdapter(ClassList);
//                    //清單點選監聽
//                    lstClassList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                            HashMap<String, String> classListADA = (HashMap<String, String>) ClassListADA.get(i);
//                            txtClassItemTitle.setText("你選擇了：" + classListADA.get("課程名稱"));
//                            ClassIDArea = classListADA.get("課程編號");
//                            ShowClass[0] = classListADA.get("課程名稱");
//                            ClassNameArea = classListADA.get("課程名稱");
//                        }
//                    });
//                    //清單按鈕點選監聽
//                    btnClassListOK.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            txtShowClassHrsT.setText("你目前選擇的課程：" + ShowClass[0]);
//                            ClassHrsSelectADA.clear();
//                            DBGetClassHrsSelect dbGetClassHrsSelect = new DBGetClassHrsSelect();
//                            dbGetClassHrsSelect.execute("");
//                            dialogBuilder.cancel();
//                        }
//                    });
//                    //清單點選監聽
//                    btnClassListCancel.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            dialogBuilder.cancel();
//                        }
//                    });
//                    dialogBuilder.setView(v);
//                    dialogBuilder.show();
//                    break;
                case R.id.btnClassHrsShowAllT:
                    DBGetUserClassList dbGetUserClassList = new DBGetUserClassList();
                    dbGetUserClassList.execute("");
                    txtShowClassHrsValueT.setText("目前清單顯示狀態：顯示全部(分鐘)");
                    txtShowClassHrsT.setText("目前清單顯示狀態：顯示全部");
                    break;
            }
        }
    };

    private ListView.OnItemClickListener listListener = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            try{
                      final HashMap<String, String> obj = (HashMap<String, String>) ClassListADA.get(i);
                      ClassIDArea = obj.get("課程編號");
                      ClassNameArea = obj.get("課程名稱");
                      final DBGetClassAllStudentList dbGetClassAllStudentList = new DBGetClassAllStudentList();
                      dbGetClassAllStudentList.execute("");

                  }catch (Exception e){

                  }
        }
    };


    //取得該學生個人學習時數
    public class DBGetStudentClassHrsList extends AsyncTask<String,String,String>{
        String z = ""; //建立回報訊息變數
        Boolean isSuccess = false; //建立辨別成功變數
        int NewClassHrs;

        @Override
        protected void onPostExecute(String z){
            if(isSuccess){
                txtShowClassHrsValueT.setText(ClassNameArea + "總共：" + ClassHrsSelectSum + "分鐘");
                int[] AttendListView = {R.id.txtShowAttendListDay,R.id.txtShowAttendListStatue}; //設定顯示清單元件
                String[] from2 = {"學習日期","總時數"};
                SimpleAdapter ShowClassHrsList = new SimpleAdapter(ShowClassHoursPageT.this,ClassHrsSelectADA,R.layout.activity_showattendlist_itme,from2,AttendListView); //設定資料陣列
                //設定陣列指向
                listClassHrsT.setAdapter(ShowClassHrsList);
                ShowClassHrsList.notifyDataSetChanged();
            }else {
                Toast.makeText(ShowClassHoursPageT.this,z,Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                Connection con = connectionClass.CONN();
                if(con == null){
                    z = "伺服器連接失敗!";
                }else {
                    String query = "Select  學習日期,課程代號,課程名稱, 學習時數 As 總時數 From 學習時數,課程資訊 " +
                            "Where 課程資訊.課程編號 = 學習時數.課程代號  " +
                            "and  學生代號 ='" + StudentIDAttendArea + "'and 課程代號 ='"+ ClassIDArea +"'";
                    //DB資料取得
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    ClassHrsSelectADA.clear();

                    while(rs.next()){
                        Map<String,String> ClassHrsSelect = new HashMap<String, String>();
                        ClassHrsSelect.put("學習日期",rs.getString("學習日期"));
                        ClassHrsSelect.put("課程名稱",rs.getString("課程名稱"));
                        ClassHrsSelect.put("課程代號",rs.getString("課程代號"));
                        ClassHrsSelect.put("總時數",rs.getString("總時數"));
                        NewClassHrs = Integer.parseInt(rs.getString("總時數")) + NewClassHrs ;
                        //ClassListInfo裝至ClassListADA二維陣列
                        ClassHrsSelectADA.add(ClassHrsSelect);
                    }
                    ClassHrsSelectSum = NewClassHrs;
                    isSuccess = true;
                }
            }catch (Exception e){
                isSuccess = false;
                z = "資料取得失敗!";
            }
            return z;
        }
    }

    //取得該課程所有學生清單
    public class DBGetClassAllStudentList extends AsyncTask<String,String,String>{
        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPostExecute(String z){
            if(isSuccess){
                LayoutInflater layoutInflater = LayoutInflater.from(ShowClassHoursPageT.this); //建立介面類別
                View v = layoutInflater.inflate(R.layout.activity_classlist_item,null); //設定清單介面
                final AlertDialog dialogBuilder = new AlertDialog.Builder(ShowClassHoursPageT.this).create();
                //設定清單元件
                ListView lstClassList = (ListView)v.findViewById(R.id.lstClassList);
                final TextView txtClassItemTitle = (TextView)v.findViewById(R.id.txtClassListTitle);
                Button btnClassListCancel = (Button)v.findViewById(R.id.btnClassListCancel);
                Button btnClassListOK = (Button)v.findViewById(R.id.btnClassListOK);
                //設定資料來源
                String[] from = {"使用者名稱"};
                int[] ClassListView = {R.id.txtClassListItemName}; //設定顯示清單
                final String[] StudentName = new String[1];
                final SimpleAdapter ClassList = new SimpleAdapter(ShowClassHoursPageT.this,DBGetClassAllStudentListADA,R.layout.activity_classlistitem_item,from,ClassListView); //設定資料陣列
                ClassList.notifyDataSetChanged();
                //設定陣列指向
                lstClassList.setAdapter(ClassList);
                txtClassItemTitle.setText("你選擇的課程是：" + ClassNameArea);

                lstClassList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        HashMap<String, String> obj = (HashMap<String, String>) DBGetClassAllStudentListADA.get(i);
                        txtClassItemTitle.setText("你選擇了：" + obj.get("使用者名稱"));
                        StudentIDAttendArea = obj.get("帳號");
                        StudentName[0] = obj.get("使用者名稱");

                    }
                });
                //清單按鈕點選監聽
                btnClassListOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DBGetStudentClassHrsList dbGetStudentClassHrsList = new DBGetStudentClassHrsList();
                        dbGetStudentClassHrsList.execute("");
                        txtShowClassHrsT.setText("目前顯示為：" + StudentName[0] + "的學習時數");
                        dialogBuilder.cancel();
                    }
                });
                btnClassListCancel.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        dialogBuilder.cancel();
                    }
                });
                dialogBuilder.setView(v);
                dialogBuilder.show();
            }else {
                Toast.makeText(ShowClassHoursPageT.this,z,Toast.LENGTH_SHORT).show();
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
                    String query = "select 使用者名稱,帳號 From 使用者資訊,課程資訊,課程學生清單 " +
                            "where 使用者資訊.帳號 = 課程學生清單.學生代號 and 課程學生清單.課程編號 = 課程資訊.課程編號 and 課程資訊.課程編號='" + ClassIDArea + "'";
                    //DB資料取得
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    DBGetClassAllStudentListADA.clear();
                        while (rs.next()) {
                            Map<String,String> StudendAttent = new HashMap<String, String>();
                            StudendAttent.put("使用者名稱",rs.getString("使用者名稱"));
                            StudendAttent.put("帳號",rs.getString("帳號"));
                            //ClassListInfo裝至ClassListADA二維陣列
                            DBGetClassAllStudentListADA.add(StudendAttent);
                    }
                    isSuccess = true;
                }
            }catch (Exception e){
             z = "資料取得失敗!";
            }
            return z;
        }
    }

    //取得個人課程清單
    public class DBGetUserClassList extends AsyncTask<String,String,String> {
        String z = ""; //建立回報訊息變數
        Boolean isSuccess = false; //建立辨別成功變數

        @Override
        protected void onPostExecute(String z){
            if(isSuccess){
                int[] AttendListView = {R.id.txtShowAttendListDay}; //設定顯示清單元件
                String[] from2 = {"課程名稱"};
                SimpleAdapter ShowClassList = new SimpleAdapter(ShowClassHoursPageT.this,ClassListADA,R.layout.activity_showattendlist_itme,from2,AttendListView); //設定資料陣列
                //設定陣列指向
                listClassHrsT.setAdapter(ShowClassList);
                ShowClassList.notifyDataSetChanged();
            }else {
                Toast.makeText(ShowClassHoursPageT.this,z,Toast.LENGTH_SHORT).show();
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
                   String query = "select 課程資訊.*,教室資訊.* " +
                            "from 課程資訊,教室資訊" +
                            " where 教室資訊.教室編號 = 課程資訊.上課地點 and 課程資訊.授課老師 ='" + UserID + "'";

                    //DB資料取得
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    ClassListADA.clear();
                    //DB各項資料裝箱至ClassListInfo
                    while (rs.next()) {
                        Map<String,String> ClassListInfo = new HashMap<String, String>();
                        ClassListInfo.put("課程名稱",rs.getString("課程名稱"));
                        ClassListInfo.put("課程編號",rs.getString("課程編號"));
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
                intent.setClass(ShowClassHoursPageT.this,AboutPage.class);
                startActivity(intent);
                break;
            //點擊LogOutMenuItem
            case R.id.action_LogOut:
                new AlertDialog.Builder(ShowClassHoursPageT.this)
                        .setTitle("登出")
                        .setMessage("確認是否要登出!?")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                preferences.edit().clear().commit();
                                Intent intentHome = new Intent();
                                intentHome.setClass(ShowClassHoursPageT.this,HomePage.class);
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
                intentContactUs.setClass(ShowClassHoursPageT.this,ContactUsPage.class);
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
