package com.example.browser.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Others extends SQLiteOpenHelper {
    public Context context;
    public static final String DB="OTHERS";
    public static final String BOOKMARKS ="BOOKMARKS";
    public static final String HISTORY ="HISTOR";
    public static final int DB_VERSION=1;
    public static final String ID="ID";
    public static final String URL="URL";

    public Others(@Nullable Context context) {
        super(context, DB,null,DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ BOOKMARKS + "("+ID+" INTEGER PRIMARY KEY,"+URL+" TEXT );");
        db.execSQL("CREATE TABLE "+ HISTORY + "("+ID+" INTEGER PRIMARY KEY,"+URL+" TEXT );");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void add(String url,final String TABLE){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(URL,url);
        db.insert(TABLE,null,cv);
        db.close();
    }
    public ArrayList<String> get(final String TABLE){
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE,null);
        cursor.moveToFirst();
        while (cursor.moveToNext()){
            list.add(cursor.getString(1));
            Log.v("Others/ArryList",cursor.getString(1));
        }
        cursor.close();
        db.close();
        return list;
    }
    public Boolean Book_Exist(String url){
        String ur="";
        Log.v("Others/Book_Exist",url+":"+ur);
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + BOOKMARKS ,null );
            cursor.moveToFirst();
            while (cursor.moveToNext()){
                if (cursor.getString(1).equals(url)) break;
            }
            ur = cursor.getString(1);
            cursor.close();
            db.close();
            Log.v("Others/Book_Exist",ur);
            return  ur.equals(url);
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public int remove(String url,final String TABLE){
        SQLiteDatabase db = this.getWritableDatabase();
        int re = db.delete(TABLE,URL+" = ? ",new String[]{url});
        db.close();
        return re;
    }

    public int Remove(final String TABLE){
        SQLiteDatabase db = this.getWritableDatabase();
        int re = db.delete(TABLE,null,null);
        return re;
    }
    public int remove_list(String[] list,final String TABLE){
        SQLiteDatabase db = this.getWritableDatabase();
        int num =0;
        for (String url : list) {
            num += db.delete(TABLE, URL + "=?", new String[]{url});
        }
        return num;
    }


}
