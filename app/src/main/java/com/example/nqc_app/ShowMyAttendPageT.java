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
import android.widget.Spinner;
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
 * Created by USER on 2016/7/21.
 */
public class ShowMyAttendPageT extends AppCompatActivity{
    //使用者資料變數建立
    ConnectionClass connectionClass;
    SharedPreferences preferences;

    //UI變數建立
    Button btnAttendReLoad,btnAttendGetClassList;
    TextView txtShowAttendT;
    ListView listAttendT;
    String UserID,ClassIDArea,ClassNameArea;

    //課程資料二維陣列建立
    List<Map<String,String>> ClassListADA = new ArrayList<Map<String,String>>();
    //查詢所有缺曠紀錄
    List<Map<String,String>> AllSudentAttendADA = new ArrayList<Map<String,String>>();
    //查詢個別缺曠紀錄
    List<Map<String,String>> SelectShowAttendADA = new ArrayList<Map<String,String>>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showattendt_page);
        connectionClass = new ConnectionClass();
        preferences = getSharedPreferences("UserData",MODE_PRIVATE); //取得內部儲存檔案

        readUserData(); //讀取使用者資料
        initUI(); //執行UI建立

        DBGetUserClassList dbGetUserClassList = new DBGetUserClassList(); //取得課程資料功能類別建立
        dbGetUserClassList.execute(""); //執行課程資料取得功能

        ActionBar actionBar = getSupportActionBar(); //取得ActionBar
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        ActionBarTitle.applyFont(actionBar,this,this.getTitle().toString()); //執行ActionBarTilte
    }

    private Button.OnClickListener btnListener = new Button.OnClickListener(){

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btnAttendReLoadT:
                    DBGetUserClassList dbGetUserClassList = new DBGetUserClassList(); //取得課程資料功能類別建立
                    dbGetUserClassList.execute(""); //執行課程資料取得功能
                    txtShowAttendT.setText("目前清單顯示狀態：今日課程");
                    break;
                case R.id.btnAttendGetClassListT:  //取得課堂資訊按鈕

                    LayoutInflater layoutInflater = LayoutInflater.from(ShowMyAttendPageT.this); //建立介面類別
                    View v = layoutInflater.inflate(R.layout.activity_classlist_item,null); //設定清單介面
                    final AlertDialog dialogBuilder = new AlertDialog.Builder(ShowMyAttendPageT.this).create();
                    //設定清單元件
                    ListView lstClassList = (ListView)v.findViewById(R.id.lstClassList);
                    final TextView txtClassItemTitle = (TextView)v.findViewById(R.id.txtClassListTitle);
                    Button btnClassListCancel = (Button)v.findViewById(R.id.btnClassListCancel);
                    Button btnClassListOK = (Button)v.findViewById(R.id.btnClassListOK);
                    //設定資料來源
                    String[] from = {"課程名稱"};
                    int[] ClassListView = {R.id.txtClassListItemName}; //設定顯示清單
                    final SimpleAdapter ClassList = new SimpleAdapter(ShowMyAttendPageT.this,ClassListADA,R.layout.activity_classlistitem_item,from,ClassListView); //設定資料陣列
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
                            txtShowAttendT.setText("你目前選擇的課程：" + ShowClass[0]);
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
        btnAttendReLoad = (Button)findViewById(R.id.btnAttendReLoadT);
        btnAttendGetClassList = (Button)findViewById(R.id.btnAttendGetClassListT);
        //將文字指向變數
        txtShowAttendT = (TextView)findViewById(R.id.txtShowAttendT);
        //將ListView指向變數
        listAttendT = (ListView)findViewById(R.id.lstAttnedListT);

        //建立Button點擊監聽
        btnAttendReLoad.setOnClickListener(btnListener);
        btnAttendGetClassList.setOnClickListener(btnListener);

        btnAttendGetClassList.setVisibility(View.GONE);

        //建立ListView項目點選監聽
        listAttendT.setOnItemClickListener(listListener);
    }

    private ListView.OnItemClickListener listListener = new ListView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            HashMap<String, String> obj = (HashMap<String, String>) ClassListADA.get(i);
            ClassIDArea = obj.get("課程編號");
            ClassNameArea = obj.get("課程名稱");
            DBGetStudentAttend dbGetStudentAttend = new DBGetStudentAttend();
            dbGetStudentAttend.execute("'");

            LayoutInflater layoutInflater = LayoutInflater.from(ShowMyAttendPageT.this); //建立介面類別
            View v = layoutInflater.inflate(R.layout.activity_classlist_item,null); //設定清單介面
            final AlertDialog dialogBuilder = new AlertDialog.Builder(ShowMyAttendPageT.this).create();
            //設定清單元件
            ListView lstClassList = (ListView)v.findViewById(R.id.lstClassList);
            final TextView txtClassItemTitle = (TextView)v.findViewById(R.id.txtClassListTitle);
            Button btnClassListCancel = (Button)v.findViewById(R.id.btnClassListCancel);
            btnClassListCancel.setVisibility(View.GONE);
            Button btnClassListOK = (Button)v.findViewById(R.id.btnClassListOK);
            //設定資料來源
            String[] from = {"使用者名稱"};
            int[] ClassListView = {R.id.txtClassListItemName}; //設定顯示清單
            final SimpleAdapter ClassList = new SimpleAdapter(ShowMyAttendPageT.this,AllSudentAttendADA,R.layout.activity_classlistitem_item,from,ClassListView); //設定資料陣列
            ClassList.notifyDataSetChanged();
            //設定陣列指向
            lstClassList.setAdapter(ClassList);
            txtClassItemTitle.setText("你選擇的課程是：" + ClassNameArea);
            //清單按鈕點選監聽
            btnClassListOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogBuilder.cancel();
                }
            });
            dialogBuilder.setView(v);
            dialogBuilder.show();
        }
    };

    public void readUserData(){
        //取得使用者ID、名字
        UserID = preferences.getString("userid","");
    }


    public class DBGetStudentAttend extends AsyncTask<String,String,String>{
        String z = "";
        Boolean isSuccess = false;
        protected void onPostExecute(String z){
            if(isSuccess){
            }else {
                Toast.makeText(ShowMyAttendPageT.this,z,Toast.LENGTH_SHORT).show();
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
                    String query = "select 使用者名稱 from 課程資訊,出席紀錄,使用者資訊" +
                            " where 出席紀錄.學生代號 = 使用者資訊.帳號 and 出席紀錄.課程代號 = 課程資訊.課程編號 " +
                            "and 出席紀錄.課程代號 ='"  + ClassIDArea + "' and 出席紀錄.簽到簽退 = '簽到'  Group By 使用者資訊.使用者名稱" ;

                    //DB資料取得
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    AllSudentAttendADA.clear();
                    //DB各項資料裝箱至ClassListInfo
                    while (rs.next()) {
                        Map<String,String> StudendAttend = new HashMap<String, String>();
                        StudendAttend.put("使用者名稱",rs.getString("使用者名稱"));
                        //ClassListInfo裝至ClassListADA二維陣列
                        AllSudentAttendADA.add(StudendAttend);
                    }
                    //資料取得成功回報
                    isSuccess = true;
                }
            }catch (Exception e){
                //資料取得失敗回報
                isSuccess = false;
                z = "資料取得失敗!";
            }
            return z;
        }
    }

    public class DBGetUserClassList extends AsyncTask<String,String,String> {
        String z = ""; //建立回報訊息變數
        Boolean isSuccess = false; //建立辨別成功變數
        @Override
        protected void onPostExecute(String z){
            if(isSuccess){
                int[] AttendListView = {R.id.txtShowAttendListDay,R.id.txtShowAttendListStatue}; //設定顯示清單元件
                String[] from2 = {"課程名稱","到課人數"};
                SimpleAdapter ShowClassList = new SimpleAdapter(ShowMyAttendPageT.this,ClassListADA,R.layout.activity_showattendlist_itme,from2,AttendListView); //設定資料陣列
                //設定陣列指向
                listAttendT.setAdapter(ShowClassList);
                ShowClassList.notifyDataSetChanged();
            }else {
                Toast.makeText(ShowMyAttendPageT.this,z,Toast.LENGTH_SHORT).show();
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
                    String query = "select 課程名稱,Count(課程編號) AS 到課人數,課程編號 " +
                            "from 課程資訊,出席紀錄" +
                            " where 課程資訊.課程編號 = 出席紀錄.課程代號 and 出席紀錄.簽到簽退 ='簽到' and 出席紀錄.簽到日期 ='1130'  Group By 課程名稱,課程編號";
                    //DB資料取得
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    ClassListADA.clear();
                    //DB各項資料裝箱至ClassListInfo
                    while (rs.next()) {
                        Map<String,String> ClassListInfo = new HashMap<String, String>();
                        ClassListInfo.put("課程名稱",rs.getString("課程名稱"));
                        ClassListInfo.put("到課人數",rs.getString("到課人數"));
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
                intent.setClass(ShowMyAttendPageT.this,AboutPage.class);
                startActivity(intent);
                break;
            //點擊LogOutMenuItem
            case R.id.action_LogOut:
                new AlertDialog.Builder(ShowMyAttendPageT.this)
                        .setTitle("登出")
                        .setMessage("確認是否要登出!?")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                preferences.edit().clear().commit();
                                Intent intentHome = new Intent();
                                intentHome.setClass(ShowMyAttendPageT.this,HomePage.class);
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
                intentContactUs.setClass(ShowMyAttendPageT.this,ContactUsPage.class);
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
