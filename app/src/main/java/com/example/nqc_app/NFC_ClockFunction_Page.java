package com.example.nqc_app;


import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
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
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.nqc_app.NFCFunction.util.BobNdefMessage;
import com.example.nqc_app.util.ActionBarTitle;
import com.example.nqc_app.util.ConnectionClass;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by USER on 2016/7/21.
 */

public class NFC_ClockFunction_Page extends AppCompatActivity {
        //UI變數建立
        TextView txtNFCStudentID,txtNFCStudentName,txtNFCClassName,txtNFCClassRoom,txtNFCClassTime,txtNFCClassRoomClock,txtNFCClassTimeClock,txtNFCAttendStatue;
        Button btnGetClassList,btnNFCClear;
        ToggleButton tgbtnNFCTag;
        RadioGroup Check = null;

        //使用者資料變數建立
        SharedPreferences preferences;
        ConnectionClass connectionClass;

        //NFCAPI變數建立
        private NfcAdapter mNfcAdapter = null;
        private PendingIntent mNfcPendingIntent = null;

        //全域資料變數建立
        String UserID,UserName,UserStatue;
        String ClassRoomArea , ClassIDArea = null ,ClassNameArea = null,ClassRoomIDArea = null,AttendStatudArea = null;
        int ClassTimeHHArea, ClassTimeMMArea,ClassLongArea,ClassWeekArea;

        //課程資料二維陣列建立
        List<Map<String,String>> ClassListADA = new ArrayList<Map<String,String>>();
        //判斷能否開啟NFCP2P功能
        Boolean initNFCP2P = false;

        //建立標題Bar類別
        private ActionBarTitle actionBarTitle;

        @Override
        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_nfc_clockfunction_page);
            connectionClass = new ConnectionClass(); //連結資料庫伺服器類別建立
            preferences = getSharedPreferences("UserData",MODE_PRIVATE); //取得內部儲存檔案
            initUI(); //執行建立UI元件指向變數類別
            readUserData(); //讀取使用者資料
            ActionBar actionBar = getSupportActionBar(); //取得ActionBar
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
            ActionBarTitle.applyFont(actionBar,this,this.getTitle().toString()); //執行ActionBarTilte
            DBGetUserClassList dbGetUserClassList = new DBGetUserClassList(); //取得課程資料功能類別建立
            dbGetUserClassList.execute(""); //執行課程資料取得功能
            initNFC(); //執行建立NFC資落傳遞類別
            checkNFCFunction(); //檢查NFC功能是否正常
    }

    //按鈕監聽
    private Button.OnClickListener btnListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btnNFCClear:
                    //清除按鈕
                    txtNFCClassName.setText("課程名稱：");
                    txtNFCClassRoom.setText("上課地點：");
                    txtNFCClassTime.setText("上課時間：");
                    txtNFCClassRoomClock.setText("簽到地點：");
                    txtNFCClassTimeClock.setText("簽到時間：");
                    txtNFCAttendStatue.setText("簽到狀態：");
                    break;

                case R.id.btnGetClassList:
                    //取得課堂資訊
                    LayoutInflater layoutInflater = LayoutInflater.from(NFC_ClockFunction_Page.this);
                    View v = layoutInflater.inflate(R.layout.activity_classlist_item,null);
                    final AlertDialog dialogBuilder = new AlertDialog.Builder(NFC_ClockFunction_Page.this).create();

                    ListView lstClassList = (ListView)v.findViewById(R.id.lstClassList);
                    final TextView txtClassItemTitle = (TextView)v.findViewById(R.id.txtClassListTitle);
                    Button btnClassListCancel = (Button)v.findViewById(R.id.btnClassListCancel);
                    Button btnClassListOK = (Button)v.findViewById(R.id.btnClassListOK);
                    String[] from = {"課程名稱"};
                    int[] ClassListView = {R.id.txtClassListItemName};
                    final SimpleAdapter ClassList = new SimpleAdapter(NFC_ClockFunction_Page.this,ClassListADA,R.layout.activity_classlistitem_item,from,ClassListView);
                    final String[] ClassName = new String[1];
                    final String[] ClassTimeH = new String[1];
                    final String[] ClassTimeM = new String[1];
                    final String[] ClassRoom = new String[1];
                    lstClassList.setAdapter(ClassList);
                    lstClassList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            HashMap<String, String> obj = (HashMap<String, String>) ClassListADA.get(i);
                            txtClassItemTitle.setText("你選擇了：" + obj.get("課程名稱"));
                            ClassName[0] = "課程名稱：" + obj.get("課程名稱");
                            ClassRoom[0] = "上課地點：" + obj.get("教室名稱");
                            ClassTimeH[0] = obj.get("上課時間時");
                            ClassTimeM[0] = obj.get("上課時間分");
                            ClassNameArea = obj.get("課程名稱");
                            ClassIDArea = obj.get("課程編號");
                            ClassTimeHHArea = Integer.parseInt(obj.get("上課時間時"));
                            ClassTimeMMArea = Integer.parseInt(obj.get("上課時間分"));
                            ClassWeekArea = Integer.parseInt(obj.get("星期"));
                            ClassLongArea = Integer.parseInt(obj.get("時數"));
                            ClassRoomArea = obj.get("教室名稱");
                            ClassRoomIDArea = obj.get("教室編號");
                        }
                    });

                    btnClassListOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            txtNFCClassName.setText(ClassName[0]);
                            txtNFCClassRoom.setText(ClassRoom[0]);
                            txtNFCClassTime.setText("上課時間：" + ClassTimeH[0] + ":" + ClassTimeM[0]);
                            dialogBuilder.cancel();
                        }
                    });

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

    //開關按鈕監聽
    private ToggleButton.OnCheckedChangeListener tgbtnNFCTagListener = new ToggleButton.OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            try{
                if(UserStatue.equals("學生")){
                    Toast.makeText(NFC_ClockFunction_Page.this,"身份錯誤，無法使用此功能!",Toast.LENGTH_SHORT).show();
                    tgbtnNFCTag.setChecked(false);
                    initNFCP2P = false;
                }else {
                    if(isChecked == true) //當按鈕狀態為選取時
                    {
                        initNFCP2P = true;
                        Toast.makeText(NFC_ClockFunction_Page.this,"臨時標籤功能開啟!",Toast.LENGTH_SHORT).show();
                        initFunction();
                    }
                    else //當按鈕狀態為未選取時
                    {
                        initNFCP2P = false;
                        Toast.makeText(NFC_ClockFunction_Page.this,"臨時標籤功能關閉!",Toast.LENGTH_SHORT).show();
                        initFunction();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    protected void onNewIntent(Intent intent){
        setIntent(intent);
    }

    private void initFunction(){
        if(initNFCP2P){
            NdefMessage message = BobNdefMessage.getNdefMsg_from_RTD_TEXT(ClassRoomArea,false,false);
            mNfcAdapter.setNdefPushMessage(message,NFC_ClockFunction_Page.this);
            Toast.makeText(NFC_ClockFunction_Page.this,"已傳遞資料!",Toast.LENGTH_SHORT).show();
        }else {
            NdefMessage message = BobNdefMessage.getNdefMsg_from_RTD_TEXT("身分錯誤!",false,false);
            mNfcAdapter.setNdefPushMessage(message,NFC_ClockFunction_Page.this);
            Toast.makeText(NFC_ClockFunction_Page.this,"請開啟臨時標籤功能!",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onResume(){
        super.onResume();
        enableForegroundDispatch();
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())){
            resolveIntent(getIntent());
        }
    }

    void resolveIntent(Intent intent){
        String action = intent.getAction();
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)){
            NdefMessage[] messages = null;
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if(rawMsgs != null){
                messages = new NdefMessage[rawMsgs.length];
                for(int i = 0; i <rawMsgs.length; i++){
                    messages[i] = (NdefMessage) rawMsgs[i];
                }
            }else {
                byte[] empty = new  byte[] {};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,empty,empty,empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
                messages = new NdefMessage[] {msg};
            }
            processNDEFMsg(messages);
        }else {
            finish();
            return;
        }
    }

    void processNDEFMsg(NdefMessage[] messages){
        if(messages == null || messages.length == 0){
            return;
        }
        for(int i = 0;i < messages.length; i++){
            int length = messages[i].getRecords().length;
            NdefRecord[] records = messages[i].getRecords();
            for(int j = 0; j < length; j++){
                for (NdefRecord record:records){
                    if(isTextRecord(record)){
                        parseRTD_TEXTRecord_book(record);
                    }
                }
            }
        }
    }

    public static boolean isTextRecord(NdefRecord record){
        if(record.getTnf() == NdefRecord.TNF_WELL_KNOWN){
            if(Arrays.equals(record.getType(),NdefRecord.RTD_TEXT)){
                return  true;
            }else  {
                return false;
            }
        }else {
            return false;
        }
    }
    void parseRTD_TEXTRecord_book(NdefRecord record){
        String payloadStr="";
        byte[] payload = record.getPayload();
        Byte statusByte = record.getPayload()[0];
        String textEncoding = ((statusByte & 0200) == 0) ? "UTF-8" : "UTF-16";
        int languageCodeLength = 0;
        languageCodeLength = statusByte & 0077;
        String languageCode ="";
        languageCode = new  String(payload,1,languageCodeLength, Charset.forName("UTF-8"));
        try{
            payloadStr = new String(payload,languageCodeLength + 1 ,payload.length - languageCodeLength - 1,textEncoding);
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        Calendar calendar=Calendar.getInstance();
        String ClockTime = new SimpleDateFormat("MM-dd HH:mm", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        int ClockTimeHH = Integer.parseInt(new SimpleDateFormat("HH", Locale.ENGLISH).format(Calendar.getInstance().getTime()));
        int ClockTimeMM = Integer.parseInt(new SimpleDateFormat("mm", Locale.ENGLISH).format(Calendar.getInstance().getTime()));
        int ClockTimeWeek = calendar.get(Calendar.DAY_OF_WEEK) -1;
        txtNFCClassRoomClock.setText("簽到地點：" + payloadStr);
        txtNFCClassTimeClock.setText("簽到時間：" + ClockTime);

        try{
            if(ClassRoomArea == null) {
                Toast.makeText(NFC_ClockFunction_Page.this,"請確定是否選擇了課堂資訊!",Toast.LENGTH_SHORT).show();
            }else if(ClassRoomArea.equals(payloadStr)){
                if(ClockStatue(ClockTimeHH ,ClockTimeMM,ClockTimeWeek)){
                    UpAttendRecordData(UserID, ClassIDArea,ClassRoomIDArea,AttendStatudArea,ClockTimeWeek);
                }

            }else {
                Toast.makeText(NFC_ClockFunction_Page.this,"點名地點錯誤!請確認",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Boolean ClockStatue(int ClockTimeHH ,int ClockTimeMM,int ClockTimeWeek){
        String z = null;

        if(ClassWeekArea == ClockTimeWeek) {
            if ((ClockTimeHH - ClassTimeHHArea <= -2) || (ClockTimeHH - ClassTimeHHArea >= ClassLongArea)) {
                z = "現在不是點名時間!";
                txtNFCAttendStatue.setText("簽到狀態：" + z);
                AttendStatudArea = z;
                return false;
            } else {
                if (ClockTimeHH - ClassTimeHHArea == -1) {
                    z = "準時";
                } else {
                    if (ClockTimeHH - ClassTimeHHArea == 0) {
                        if (ClockTimeMM - ClassTimeMMArea > 15) {
                            z = "遲到";
                        } else {
                            z = "準時";
                        }
                    }
                }
            }
            txtNFCAttendStatue.setText("簽到狀態：" + z);
            AttendStatudArea = z;
            return true;
        }else {
            z = "現在不是點名時間!";
            txtNFCAttendStatue.setText("簽到狀態：" + z);
            AttendStatudArea = z;
            return false;
        }
    }

    public void UpAttendRecordData(String StudentID, String ClassID, String ClassRoomID,String AttendStatue,int ClockTimeWeek) throws ParseException {
        String z = null;
        int semester;
        String CheckStatue = null;
        String ClockReCodeDay = new SimpleDateFormat("MMdd", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        String ClockToday = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        String ClockTime = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        int YearNow = Integer.parseInt(new SimpleDateFormat("yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime())) - 1911;
        int MMNow = Integer.parseInt(new SimpleDateFormat("MM", Locale.ENGLISH).format(Calendar.getInstance().getTime()));
        if(MMNow >= 2 && MMNow <=6 ){
            semester = 2;
        }else {
            semester = 1;
        }
        String ClockReCode = StudentID + ClockReCodeDay ;
        if(Check.getCheckedRadioButtonId() == (R.id.rdbtnNFCCheckIn)){
            CheckStatue = "簽到";
            ClockReCode = ClockReCode + "A";
        }else if(Check.getCheckedRadioButtonId() == (R.id.rdbtnNFCCheckOut)){
            CheckStatue = "簽退";
            ClockReCode = ClockReCode + "B";
        }else {
            CheckStatue = null;
        }

        if(!CheckStatue.equals(null)){
            try {
                Connection con = new ConnectionClass().CONN();
                if(con == null){
                    z = "伺服器連接失敗!";
                }else {
                    String query = "insert into 出席紀錄 (簽到紀錄,學年度,學期,簽到星期,簽到日期,簽到時間,學生代號,課程代號,教室代號,出席狀況,簽到簽退) values ('"
                            + ClockReCode +"','" +  YearNow + "','" + semester  + "','"  + ClockTimeWeek + "','" + ClockToday + "','" + ClockTime + "','" + StudentID  + "','" + ClassID+ "','" + ClassRoomID + "','" + AttendStatue + "','" + CheckStatue + "')" ;
                    PreparedStatement preparedStatement = con.prepareStatement(query);
                    preparedStatement.executeUpdate();
                    z = CheckStatue + "成功!";
                }
            }catch (Exception ex){
                z = "請勿在同意時間簽到兩次以上!";
            }
            Toast.makeText(NFC_ClockFunction_Page.this,z,Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(NFC_ClockFunction_Page.this,"請選擇簽到或簽退!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        disableForegroundDispatch();
    }

    private void enableForegroundDispatch() {
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, null,null);
        }
    }

    private void disableForegroundDispatch() {
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    public void initNFC(){
        //建立NFC資料傳遞類別方法
        mNfcPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    public void initUI(){
        //TextView指向變數
        txtNFCStudentID = (TextView)findViewById(R.id.txtNFCStudentID);
        txtNFCStudentName = (TextView)findViewById(R.id.txtNFCStudentName);
        txtNFCClassName = (TextView)findViewById(R.id.txtNFCClassName);
        txtNFCClassRoom = (TextView)findViewById(R.id.txtNFCClassRoom);
        txtNFCClassRoomClock = (TextView)findViewById(R.id.txtNFCClassRoomClock);
        txtNFCClassTime = (TextView)findViewById(R.id.txtNFCClassTime);
        txtNFCClassTimeClock = (TextView)findViewById(R.id.txtNFCClassTimeClock);
        txtNFCAttendStatue = (TextView)findViewById(R.id.txtNFCAttendStatue);
        //Button指向變數
        btnGetClassList = (Button)findViewById(R.id.btnGetClassList);
        btnNFCClear= (Button)findViewById(R.id.btnNFCClear);
        //ToggleButton指向變數
        tgbtnNFCTag = (ToggleButton)findViewById(R.id.tgbtnNFCTag);
        //RadioGroup指向變數
        Check = (RadioGroup)findViewById(R.id.rdgroupNFCCheck);
        //Button按鈕監聽事件
        btnGetClassList.setOnClickListener(btnListener);
        btnNFCClear.setOnClickListener(btnListener);
        //ToggleButton按鈕監聽事件
        tgbtnNFCTag.setOnCheckedChangeListener(tgbtnNFCTagListener);
    }

    public void readUserData(){
        //取得使用者ID、名字
        UserID = preferences.getString("userid","");
        UserName = preferences.getString("username","");
        UserStatue = preferences.getString("userstatue","");
        //設定顯示使用者ID、姓名
        txtNFCStudentID.setText("學號：" + UserID);
        txtNFCStudentName.setText("姓名：" + UserName);
    }

    public class DBGetUserClassList extends AsyncTask<String,String,String>{
        String z = ""; //建立回報訊息變數
        Boolean isSuccess = false; //建立辨別成功變數

        @Override
        protected void onPostExecute(String z){
            Toast.makeText(NFC_ClockFunction_Page.this,z,Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                Connection con = connectionClass.CONN();
                if(con == null){
                    z = "伺服器連接失敗!";
                }else {
                    //SQL查詢指令
                    String query = "select 課程資訊.*,課程學生清單.*,教室資訊.* " +
                            "from 課程資訊,課程學生清單,教室資訊" +
                            " where 教室資訊.教室編號 = 課程資訊.上課地點 and 課程資訊.課程編號 = 課程學生清單.課程編號 and 課程學生清單.學生代號 ='" + UserID + "'";
                    //DB資料取得
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    //DB各項資料裝箱至ClassListInfo
                    while (rs.next()) {
                        Map<String,String> ClassListInfo = new HashMap<String, String>();
                        ClassListInfo.put("課程編號",rs.getString("課程編號"));
                        ClassListInfo.put("課程名稱",rs.getString("課程名稱"));
                        ClassListInfo.put("上課時間時",rs.getString("上課時間時"));
                        ClassListInfo.put("上課時間分",rs.getString("上課時間分"));
                        ClassListInfo.put("時數",rs.getString("時數"));
                        ClassListInfo.put("星期",rs.getString("星期"));
                        ClassListInfo.put("教室編號",rs.getString("教室編號"));
                        ClassListInfo.put("教室名稱",rs.getString("教室名稱"));

                        //ClassListInfo裝至ClassListADA二維陣列
                        ClassListADA.add(ClassListInfo);
                    }
                    //資料取得成功回報
                    z ="課程資料取得成功!";
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

    //簡查NFC功能類別
    public boolean checkNFCFunction(){
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter ==null){
            Toast.makeText(getApplicationContext(),"NFC apdater is not Available!", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            if(!mNfcAdapter.isEnabled()){
                Toast.makeText(getApplicationContext(),"NFC is not Enabled!",Toast.LENGTH_SHORT).show();

                Intent setnfc = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(setnfc);
                return false;
            }else if(!mNfcAdapter.isNdefPushEnabled()){
                Toast.makeText(getApplicationContext(),"NFC Beam is not Enabled!",Toast.LENGTH_SHORT).show();
                Intent setnfc = new Intent(Settings.ACTION_NFCSHARING_SETTINGS);
                startActivity(setnfc);
                return false;

            }else {
                Toast.makeText(getApplicationContext(),"NFC is working!",Toast.LENGTH_SHORT).show();
                return true;
            }
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
                intent.setClass(NFC_ClockFunction_Page.this,AboutPage.class);
                startActivity(intent);
                break;
            //點擊LogOutMenuItem
            case R.id.action_LogOut:
                new AlertDialog.Builder(NFC_ClockFunction_Page.this)
                        .setTitle("登出")
                        .setMessage("確認是否要登出!?")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                preferences.edit().clear().commit();
                                Intent intentHome = new Intent();
                                intentHome.setClass(NFC_ClockFunction_Page.this,HomePage.class);
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
                intentContactUs.setClass(NFC_ClockFunction_Page.this,ContactUsPage.class);
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
