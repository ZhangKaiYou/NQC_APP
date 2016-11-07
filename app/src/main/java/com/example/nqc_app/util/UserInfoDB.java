package com.example.nqc_app.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by USER on 2016/10/16.
 */
public class UserInfoDB {
    public SQLiteDatabase db = null;
    private final static String DATABASE_NAME= "db1.db";
    private final static String TABLE_NAME="UserInfo";
    private final static String _ID = "_id";
    private final static String NAME = "name";
    private final static String GENDER = "gender";
    private final static String STUDENTID = "studentid";
    private final static String CLASSS = "classs";
    private final static String DEPARTMENT = "department";
    private final static String EMAIL = "email";
    private final static String STATUE = "statue";
    private final static String PASSWORD = "password";

    private final static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY," + STUDENTID + " TEXT," +
            PASSWORD + " TEXT," + NAME + " TEXT,"+ GENDER + " TEXT,"+ EMAIL + " TEXT,"+ STATUE + " TEXT,"+ DEPARTMENT + " TEXT,"+ CLASSS + " TEXT)";
    private Context mCtx = null;

    public UserInfoDB(Context ctx){
        this.mCtx = ctx;
    }

    public void open() throws SQLException {
        db = mCtx.openOrCreateDatabase(DATABASE_NAME, 0 ,null);
        try{
            db.execSQL(CREATE_TABLE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void close(){
        db.close();
    }

    public Cursor getAll(){
        return db.rawQuery("SELECT * FROM " + TABLE_NAME,null);
    }

    public boolean delete(long rowId){
        return db.delete(TABLE_NAME,_ID + "=" +rowId,null) > 0;
    }

    public void deleteAll(){
        db.execSQL("delete from "+ TABLE_NAME);
    }

    public Cursor get(long rowId) throws SQLException{
        Cursor mCursor = db.query(TABLE_NAME,new String[] {_ID,STUDENTID,PASSWORD,NAME, GENDER,EMAIL,STATUE, DEPARTMENT,CLASSS},_ID + "=" + rowId,null,null,null,null,null);
        if(mCursor != null){
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public long append(String studentid,String password,String name,String gender,String email,String statue,String department,String classs){
        ContentValues args = new ContentValues();
        args.put(STUDENTID,studentid);
        args.put(PASSWORD,password);
        args.put(NAME,name);
        args.put(GENDER,gender);
        args.put(EMAIL,email);
        args.put(STATUE,statue);
        args.put(DEPARTMENT,department);
        args.put(CLASSS,classs);
        return db.insert(TABLE_NAME,null,args);
    }

    public boolean update(long rowId,String name,String department,String gender,String studentid,String classs,String email,String statue,String password){
        ContentValues args = new ContentValues();
        args.put(STUDENTID,studentid);
        args.put(PASSWORD,password);
        args.put(NAME,name);
        args.put(GENDER,gender);
        args.put(EMAIL,email);
        args.put(STATUE,statue);
        args.put(DEPARTMENT,department);
        args.put(CLASSS,classs);
        return db.update(TABLE_NAME, args ,_ID + "=" +rowId,null) > 0;
    }

}
