package com.example.browser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.browser.Adapter.Bookmark_Adapter;
import com.example.browser.database.Others;

import java.util.ArrayList;

public class Booksmarks extends AppCompatActivity {

    public static Others bookmarks;
    String TABLE;
    ArrayList<String> url_list;
    Bookmark_Adapter adapter ;
    RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booksmarks);

        url_list = new ArrayList<>();
        bookmarks = new Others(this);
        TABLE = Others.BOOKMARKS;
        url_list = bookmarks.get(TABLE);
        for (String url : url_list) Log.v("Booksmarks",url);


        adapter = new Bookmark_Adapter(this,getBaseContext(),url_list);
        recyclerView = findViewById(R.id.Bookmark_recyclar);
        recyclerView.setLayoutManager( new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }
    public static void open_url(Context context, int position, String name){
            MainActivity.Table_id = position;
            MainActivity.Table_name = name;
            MainActivity.ischangrd = true;

        ((Tabes_Activity)context).finish();
    }




}