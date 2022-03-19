package com.example.browser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.browser.Adapter.Tab_Adapter;
import com.example.browser.database.DataBase;
import com.example.browser.modal.Tabs;


import java.util.ArrayList;
import java.util.Collections;

public class Tabes_Activity extends AppCompatActivity {
    public static final String home="HOME-PAGE";
    TextView textView ;
    static DataBase db;
    static ArrayList<String> names;
    static ArrayList<String> url_list;
    static ArrayList<Tabs> tabs;
    RecyclerView listView;
    static Tab_Adapter tab_adapter;
    String Table_name;
    public static String Present;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // views
        Intent intent =getIntent();
        Present = intent.getStringExtra("Table_name");
        Log.v("Tabs_Activity",Present);



        // DataBase
        {
            db = new DataBase(this);
//            url_list = new ArrayList<String>();
//            names = db.TABLE_NAMES();
//            if (names.size() == 0) {
//                db.Create_table(0);
//                names = db.TABLE_NAMES();
//                for (String name : names){
//                    url_list.add(db.get_last(name));
//                }
//            }

            tabs = db.get_Tabs();
            if (tabs.size() == 0) {
                db.Create_table(0);
                tabs = db.get_Tabs();
            }
            for (Tabs t : tabs){
                Log.v("Tabs_Activity",t.getTable_name()+":"+t.getUrl());
            }
        }

        // Recyclar and TabAdapter
        {
            listView = findViewById(R.id.recyclerview);
            tab_adapter = new Tab_Adapter(this,getBaseContext(), tabs);
            listView.setLayoutManager(new GridLayoutManager(this, 2));
            listView.setAdapter(tab_adapter);
        }



    }


    public  void create_default(){
        Log.v("MainActivity","create_default running");
        int i = 0;
        if (names.size()==0){
            DataBase db = new DataBase(this,i);
            Table_name = db.Create_table(i);
            db.add_url(Table_name,home);
//            Tabs tab = new Tabs();
//            tab.setTable_name(Table_name);
//            tab.setUrl("TAB_"+String.valueOf(i));
            tab_adapter.Add(Table_name);
            tab_adapter.notifyDataSetChanged();
        }
        else return;
    }

    public void Create_Tab(View view) {
        int i=0;
        ArrayList<Integer> arry = db.TABLE_IDS();
        Collections.sort(arry);
        for (Integer s : arry) {
//            Log.v("MainActivity",Integer.parseInt(s.substring(s.length() - 1)) +""+i);
            if (s != i) break;
            i++;
        }
        Table_name =db.Create_table(i);
        db.add_home(Table_name);
        Tabs tab = new Tabs();
        tab.setTable_name(Table_name);
        tab.setUrl("TAB_"+String.valueOf(i));
        tab_adapter.Add(Table_name);
        tab_adapter.notifyItemInserted(i);
        names = db.TABLE_NAMES();
//        tab_adapter.Add("TAB_"+String.valueOf(i));

    }
    static public void Delete_tab(String Table_name,int position){
        tab_adapter.Tab_list.remove(position);
        tab_adapter.notifyItemRemoved(position);
//        tab_adapter.notifyDataSetChanged();
        db.DEL_TABLE(Table_name);

    }

    public static void open_url(Context context,int position,String name){
        if (!Present.equals(name)){
        MainActivity.Table_id = position;
        MainActivity.Table_name = name;
        MainActivity.ischangrd = true;
        }

        ((Tabes_Activity)context).finish();
    }


}