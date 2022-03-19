package com.example.browser.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.browser.modal.Tabs;

import java.util.ArrayList;
//
//public class DataBase extends SQLiteOpenHelper {
//    public static final String DB_Name="DATABASE";
//    public static final String TABLE_NAME="TAB";
//    public static final String COL_URL="URLS";
//    public static final String COL_ID="ID";
//    public static final int DB_VERSION=1;
//
//
//    public DataBase(@Nullable Context context) {
//        super(context, DB_Name,null, DB_VERSION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE "+TABLE_NAME+
//                " ("+COL_ID+" INTEGER PRIMARY KEY,"+COL_URL+" TEXT);");
//
//    }
//
//    public void AddTab(int id){
//        SQLiteDatabase db =this.getWritableDatabase();
//        db.execSQL("CREATE TABLE "+TABLE_NAME+id+
//                " ("+COL_ID+" INTEGER PRIMARY KEY,"+COL_URL+" TEXT);");
//    }
//
//    public ArrayList<Tabs> getTab(int id){
//        SQLiteDatabase db = getReadableDatabase();
//        ArrayList <Tabs> arrayList = new ArrayList<>();
//        String query = "SELECT * FROM " + TABLE_NAME+id +" ORDER BY id ASC";
//        Cursor cv = db.rawQuery(query, null);
//        while (cv.moveToNext()){
//            Tabs tabs = new Tabs();
//            tabs.id = Integer.parseInt(cv.getString(0));
//            tabs.url = cv.getString(1);
//            arrayList.add(tabs);
//        }
//        return arrayList;
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db .execSQL("Drop table IF EXISTS "+TABLE_NAME);
//    }
//
//    public void insert(String url){
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COL_URL,url);
//        db.insert(TABLE_NAME,null,contentValues);
//        db.close();
//    }
//
//    public void update(Tabs tabs){
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COL_URL,tabs.getUrl());
//        contentValues.put(COL_ID,tabs.getId());
//        db.update(TABLE_NAME,contentValues,"id=?",new String[]{String.valueOf(tabs.getId())});
//        db.close();
//    }
//
//    public ArrayList<Tabs> Get_DB(){
//        SQLiteDatabase db = getReadableDatabase();
//        ArrayList <Tabs> arrayList = new ArrayList<>();
//
//        String query = "SELECT * FROM " + TABLE_NAME +" ORDER BY id ASC";
//        Cursor cv = db.rawQuery(query, null);
//        while (cv.moveToNext()){
//            Tabs tabs = new Tabs();
//            tabs.id = Integer.parseInt(cv.getString(0));
//            tabs.url = cv.getString(1);
//            arrayList.add(tabs);
//        }
//        return arrayList;
//    }
//
//    public void delete(int id){
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_NAME,COL_ID+"="+id,null);
//        db.close();
//    }
//}


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DataBase extends SQLiteOpenHelper {
    public static final String DB_NAME ="TABS_DATA";
    public static final String TABLE ="TAB_";
    public static final String ID ="ID";
    public static final String URL ="URL";
    public static final int DB_VERSION = 1;
    public static final String home="HOME-PAGE";
    public int tab_id;
    public Cursor cursor;
    public int position = 0;

    public DataBase(@Nullable Context context,int id) {
        super(context, DB_NAME, null, DB_VERSION);
        tab_id = id;
    }

    public DataBase(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP IF "+TABLE+tab_id+"EXIST");

    }


    // Table functions

    public String Create_table(int id){
        tab_id = id;
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("CREATE TABLE "+TABLE+tab_id+ "("+ID+" INTEGER PRIMARY KEY,"+URL+" TEXT );");
        return TABLE+tab_id;
    }


    public ArrayList get_Tabs(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =db.rawQuery("SELECT * FROM sqlite_master WHERE type='table'",null);

        ArrayList<Tabs> table = new ArrayList<>();
        cursor.moveToFirst();

        if(cursor.getPosition()!=-1) Log.v("DataBase/getTabs",cursor.getPosition()+""+cursor.getString(1));
            while ( cursor.moveToNext() ) {
                if (cursor.getString(1).indexOf("TAB")!=-1) {
                    String Table_name = cursor.getString(1);
                    Log.v("DATABASE",Table_name);
                    Tabs tabs = new Tabs();
                    tabs.setUrl(get_last(Table_name));
                    tabs.setTable_name(Table_name);
                    table.add(tabs);
                }
            }

        db.close();

        return table;
    }

    public ArrayList<String> TABLE_NAMES(){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =db.rawQuery("SELECT * FROM sqlite_master WHERE type='table'",null);

        ArrayList<String> table = new ArrayList<>();

        if (cursor.moveToFirst()) {
            while ( !cursor.isAfterLast() ) {
                if (cursor.getString(1).indexOf("TAB")!=-1) {
                    Log.v("DATABASE",cursor.getString(1));
                    table.add(cursor.getString(1));
                }
                cursor.moveToNext();
            }
        }
        db.close();

        return table;
    }


    public ArrayList<Integer> TABLE_IDS(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =db.rawQuery("SELECT * FROM sqlite_master WHERE type='table'",null);

        ArrayList<Integer> table = new ArrayList<>();

        if (cursor.moveToFirst()) {
            while ( !cursor.isAfterLast() ) {
                if (cursor.getString(1).indexOf("TAB")!=-1) {
                    String st = cursor.getString(1);
                    Log.v("DATABASE",cursor.getString(1));
                    table.add(Integer.parseInt(st.substring(st.indexOf("_")+1)));
                }
                cursor.moveToNext();
            }
        }
        db.close();

        return table;
    }

    public void DEL_TABLE(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE "+TABLE+tab_id);
    }


    public void DEL_TABLE(String Table){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE "+Table);
    }

    // uesing Table Name


    public void add_url(String Table,String url){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+Table,null);
        while (cursor.moveToNext()){
            Log.v("DataBase/add_url","printing list url "+cursor.getString(1)+" position "+cursor.getString(0));
        }

        cursor.moveToLast();
        while (cursor.moveToPrevious() && Integer.parseInt(cursor.getString(0)) >= position+1 ){
            Log.v("DataBase","Deleting the "+cursor.getString(1));
            int i =db.delete(TABLE+tab_id,"ID=?",new String[]{String.valueOf(cursor.getString(0))});
            Log.v("DataBase","Deleting the "+String.valueOf(i)+":"+cursor.getString(0));
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(URL,url);
        db.insert(Table,null,contentValues);
        db.close();
        position++;



//        SQLiteDatabase  read = this.getReadableDatabase();
//        Cursor cursor =read.rawQuery("SELECT * FROM "+TABLE+tab_id,null);
//        Log.v("DataBase", String.valueOf(cursor.getPosition()));
//        cursor.move(position+2);
//        this.cursor = cursor;
//        read.close();

    }

    public void add_home(String Table){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(URL,home);
        db.insert(Table,null,contentValues);
        db.close();
        position=0;


    }

    public ArrayList get_all(String TABLE){
        ArrayList<String> arrayList =new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =db.rawQuery("SELECT * FROM "+TABLE,null);
        while (cursor.moveToNext()){
            arrayList.add(cursor.getString(1));
            Log.v("DataBase",cursor.getPosition()+":"+cursor.getString(1));
        }
        db.close();
        return arrayList;

    }

    public int length(String Table){
        SQLiteDatabase db =this.getReadableDatabase();
        Cursor cursor =db.rawQuery("SELECT * FROM "+Table,null);
        int result =cursor.getCount();
        db.close();
        return result;
    }

    public String get_present(String Table){
        if (position<0) add_home(Table);
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + Table, null);
            cursor.moveToFirst();
            cursor.move(position);
            db.close();
            return cursor.getString(1);
        }
        catch (Exception e){
            Log.e("DataBase/get_present",e.getLocalizedMessage());
//            add_url(Table,home);
            return home;
        }
//        Log.v("DataBase",cursor.getString(0));
    }
    public String forword(String Table){
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + Table, null);
            position++;
            cursor.moveToFirst();
            cursor.move(position);
            db.close();
            return cursor.getString(1);
        }catch (Exception e){
            position--;
            Log.e("DataBase",e.getLocalizedMessage());
            return get_present(Table);
        }
    }
    public String Backword(String Table){
        String str;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + Table, null);
            position--;
            cursor.moveToFirst();
            cursor.move(position);
            db.close();
            str = cursor.getString(1);
        }
        catch (Exception e){
            position++;
            Log.e("DataBase","Backword"+e.getLocalizedMessage());
            return get_present(Table);
        }
        return str;
    }

    public void delete(String Table) {
        SQLiteDatabase read = this.getReadableDatabase();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = read.rawQuery("SELECT * FROM "+Table,null);
        for (int j =cursor.getCount();j>=position;j--){
            int i =db.delete(TABLE+tab_id,"ID=?",new String[]{String.valueOf(j)});
            Log.v("DataBase","Deleting the "+String.valueOf(i)+":"+j);
        }
        position--;
        if (position<1){
            this.add_url(Table,home);
        }
        read.close();
        db.close();
    }

    public boolean can_forword(String Table){
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + Table, null);
            while (cursor.moveToNext()){
                Log.v("DataBase/F",cursor.getString(1)+":"+cursor.getPosition());
            }
            cursor.moveToFirst();
            cursor.move(position+1);
            cursor.getString(1);
            Log.v("DataBase",cursor.getString(1));
            db.close();
            return true;
        }catch (Exception e){
            Log.e("DataBase","forword "+e.getLocalizedMessage());
            return false;
        }
    }

    public boolean can_Backword(String Table){

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + Table, null);
            while (cursor.moveToNext()){
                Log.v("DataBase/B",cursor.getString(1));
            }
            cursor.moveToFirst();
            cursor.move(position-1);
            cursor.getString(1);
            db.close();
            return true;
        }
        catch (Exception e){
            Log.e("DataBase","backword "+e.getLocalizedMessage());
            return false;
        }
    }

    public String get_last(String Table){
        String str;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Table, null);
        if (cursor.getCount()==0){
            return home;
        }
        cursor.moveToLast();
        str = cursor.getString(1);
        db.close();
        return str;
    }


}


