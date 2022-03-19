package com.example.browser;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.browser.database.DataBase;
import com.example.browser.database.Others;

import android.webkit.CookieManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {



    public boolean Desktopmode = false;
    public static Boolean ischangrd = false;
    public static final String home="HOME-PAGE";
    SearchView searchview ;
    static String Table_name;
    static public int Table_id=-1;
    WebView webView ;
    View home_view;
    DataBase db;
    Others History;
    ArrayList <String> tables;
    Button go_forword;
    Button go_Backword;
    public  Boolean store_history = true;
    public boolean reload = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // View and layouts
        {
            searchview = findViewById(R.id.search);
            webView = findViewById(R.id.web);
            home_view = findViewById(R.id.home_screem);
            go_forword = findViewById(R.id.front);
            go_Backword = findViewById(R.id.back);
            go_forword.setEnabled(false);
            go_Backword.setEnabled(false);
        }


        // DB initialinze
        {
            History = new Others(this);
            db = new DataBase(this);
            tables = db.TABLE_NAMES();
            Table_name = Create_Tab();

        }


        // vpn & proxy
        {
            ArrayAdapter<CharSequence> ad = ArrayAdapter.createFromResource(this, R.array.Country, android.R.layout.simple_spinner_item);
            ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        }


        // opctions settings
        {
            RelativeLayout nv = findViewById(R.id.opctions_view);
            nv.setVisibility(View.GONE);
            findViewById(R.id.search);
        }

        // setbrowser();
        {
            this.webView.setWebViewClient(new MyWebViewClient());
            webView.setWebChromeClient(new MyWebChrome());
            Common(webView);
        }

        // desktopmode
        {
            CheckBox checkBox = findViewById(R.id.check);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) Enable_desktop_mode();
                    else Enable_modile_mode();
                    toogle();
                }
            });
        }

        // search view settings
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                super.onQueryTextChange(query);

                searchview.clearFocus();
                findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
                findViewById(R.id.progress_bar).setEnabled(true);

                if (!URLUtil.isValidUrl(query)) query="https://www.google.com/search?q="+query;
                search(query);
                if (tables.size()<=0){
                Table_name = Create_Tab();
                db.tab_id = Table_id=Integer.parseInt(Table_name.substring(Table_name.indexOf("_")+1));
                db.add_home(Table_name);
                }

                if (query.equals(home)){
                    webView.setVisibility(View.GONE);
                    home_view.setVisibility(View.VISIBLE);
                    searchview.setQuery(null,false);
                    return true;
                }
                db.add_url(Table_name,query);
                toogle();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });


        // code testing
        {

            Test_Code();


        }


    }

    public void Test_Code(){
//        File logo;
//        logo = new File(getBaseContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Logo_name");
//        logo.mkdir();



    }

    public void toogle(){




        if (db.get_present(Table_name).equals(home)) findViewById(R.id.reload).setEnabled(false) ;
        else findViewById(R.id.reload).setEnabled(true);

        if (db.can_forword(Table_name)){
            go_forword.setEnabled(true);
        }
        else {
            go_forword.setEnabled(false);

        }


        if (db.can_Backword(Table_name)){
            go_Backword.setEnabled(true);

        }
        else {
            go_Backword.setEnabled(false);

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (ischangrd) {
            Desktopmode = false;
            webView.setVisibility(View.GONE);
            db = new DataBase(this);
            String url;
            webView.clearHistory();
            db.tab_id = Table_id;
            try {
                db.position = db.length(Table_name) - 1;
                url = db.get_present(Table_name);
            } catch (RuntimeException e) {
                Table_name = db.Create_table(Table_id);
                db.add_home(Table_name);
                url = home;
            }
            webView.loadUrl(url);
            searchview.setQuery(null,false);
            if (!(url.equals(home))) searchview.setQuery(url, false);
            Log.v("onRestart", url + " : " + db.position);
            toogle();
            ischangrd = false;
        }
        Log.v("onRestart",Table_name);

    }


    public void Create_Tab(View view) {
        DataBase urldb = new DataBase(this);
        int i=0;
        ArrayList<Integer> arry = urldb.TABLE_IDS();
        Collections.sort(arry);
//        db.tab_id = Table_id=Integer.parseInt(s.substring(s.indexOf("_")+1));

        for (Integer s : arry) {
            Log.v("MainActivity",s+":"+i);
            if (s != i) break;
            i++;
        }
        Table_id = i;
        DataBase db = new DataBase(this, i);
        Table_name = db.Create_table(i);
        db.add_url(Table_name, home);

    }

    public String Create_Tab() {
        DataBase urldb = new DataBase(this);
        int i=0;
        ArrayList<Integer> arry = urldb.TABLE_IDS();
        arry.size();
        if (arry.size()<=0){
            DataBase db = new DataBase(this, i);
            Table_name = db.Create_table(i);
            Log.v("MainActivity/Create_Tab",Table_name);
            db.add_url(Table_name, home);
            return Table_name;
        }

        Collections.sort(arry);
        for (Integer s : arry) {
            Log.v("MainActivity",s+":"+i);
            Log.v("MainActivity",arry.indexOf(s) +":"+i);
            if (s != i) break;
            i++;
        }
        Table_id = i-1;
        if (i>0 && !db.get_last("TAB_" + Integer.toString(Table_id )).equals(home) ) {
            Table_id++;
            DataBase db = new DataBase(this, i);
            Table_name = db.Create_table(i);
            Log.v("MainActivity/Create_Tab",Table_name);
            db.add_url(Table_name, home);
        }
        else {
            Table_name = "TAB_" + Integer.toString(Table_id ) ;
        }
        return  Table_name;

    }

    public void search(String url){
        Log.v("MainAcitivity",url);
        if (url.equals(home)) {
            webView.setVisibility(View.GONE);
            home_view.setVisibility(View.VISIBLE);
        }
        else
        {
            Log.v("MainActivity/search","internet connnection"+checkConnection());
            if (!checkConnection()) {
                findViewById(R.id.progress_bar).setVisibility(View.GONE);
                findViewById(R.id.progress_bar).setEnabled(true);
                return;
            }

            webView.loadUrl(url);
        }


    }

    public void setbrowser(){

        CookieManager cookieManager;
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        this.webView.setWebViewClient(new MyWebViewClient());
        webSettings.setSaveFormData(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setBlockNetworkImage(false);
        webSettings.setBlockNetworkLoads(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setNeedInitialFocus(false);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    }


    Boolean OnKeyUp(int KeyCode, KeyEvent event){
        switch (KeyCode){
            case KeyEvent.KEYCODE_SEARCH:
                if (event.isShiftPressed()){
                    search("h");
                }
                else {

                }
        }

        return false;
    }

    public void Make_Toast(String str){
        Toast.makeText(getApplicationContext(),str,Toast.LENGTH_LONG).show();
    }

    public void Show(View view){

    Log.v("MainActivity","Opend");
        RelativeLayout nv = findViewById(R.id.opctions_view);
    nv.setVisibility(View.VISIBLE);
    }
    public void Close(View view){
        Log.v("MainActivity","Closed");
        RelativeLayout nv = findViewById(R.id.opctions_view);
        nv.setVisibility(View.GONE);
    }
    public void click(View view){
        
        if (!checkConnection()) return;
        WebSettings webSettings = webView.getSettings();
        
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/19.246");
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        this.webView.setWebViewClient(new MyWebViewClient());
        
        webSettings.setSaveFormData(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setBlockNetworkImage(false);
        webSettings.setBlockNetworkLoads(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setNeedInitialFocus(false);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setUserAgentString("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.96 Safari/537.36");

        webView.setVisibility(View.VISIBLE);
        home_view.setVisibility(View.GONE);
        webView.loadUrl("https://web.whatsapp.com");

    }

    public boolean checkConnection(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            if (cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ||
                    cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    cm.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH).getState() == NetworkInfo.State.CONNECTED)
                return true;
            else {
                Toast.makeText(this.getApplicationContext(),"Please check your internet connection",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        catch (Exception e){
            Toast.makeText(this.getApplicationContext(),"Please check your internet connection",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String con[] = getResources().getStringArray(R.array.Country);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    public void openTabs(View view) {
        Intent intent = new Intent(this,Tabes_Activity.class);
        intent.putExtra("Table_name",Table_name);
        intent.putExtra("Table_id",Table_id);
        startActivity(intent);

    }

    public void go_back(View view) {
        Log.v("Go_back",db.position+"-"+db.get_present(Table_name));
        String url =db.Backword(Table_name);
        if (!url.equals(home)) searchview.setQuery(url,false);
        Log.v("MainActivity","Moving to -B "+url);
        toogle();
        Log.v("Go_back",db.position+"-"+db.get_present(Table_name));
        search(url);


    }

    public void go_forword(View view) {
        toogle();
        Log.v("Go_forword",db.position+"-"+db.get_present(Table_name));
        String url = db.forword(Table_name);
        Log.v("MainActivity","Moving to -F"+url);
        if (!url.equals(home)) searchview.setQuery(url,false);
        Log.v("Go_forword",db.position+"-"+db.get_present(Table_name));
        toogle();
        search(url);
        
    }

    public void reload(View view) {
        webView.reload();

        toogle();
    }

    public void Bookmark(View view) {
        Intent intent = new Intent(this,Booksmarks.class);
        startActivity(intent);
    }

    public void Bookmark_togle(View view) {
        Log.v("MainActivity/BookMarks","clicked");
        if (!view.isSelected()){
            History.add(db.get_present(Table_name),Others.BOOKMARKS);
            view.setSelected(true);

        }
        else{
            History.remove(db.get_present(Table_name),Others.BOOKMARKS);
            view.setSelected(false);


        }
    }

    public void Share_url(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.setType("text/plane");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
        Intent shareIntent = Intent.createChooser(sendIntent, "Share Url");
        startActivity(shareIntent);
    }
    public void Enable_modile_mode(){
        webView = null;
        webView = findViewById(R.id.web);
        webView.getSettings().setUserAgentString(WebSettings.getDefaultUserAgent(this));

        WebSettings webSettings = this.webView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setMediaPlaybackRequiresUserGesture(true);
        webSettings.setUserAgentString(WebSettings.getDefaultUserAgent(this));
        Log.v("MainActivity/UserAgent",webSettings.getUserAgentString());
    }

    public void Common(WebView webView){
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSaveFormData(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setBlockNetworkImage(false);
        webSettings.setBlockNetworkLoads(false);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        // false in anonamus
        webSettings.setGeolocationEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    }
    public void Enable_desktop_mode(){
        WebSettings webSettings = webView.getSettings();
        webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/19.246");
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setNeedInitialFocus(false);
        webSettings.setUserAgentString("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.96 Safari/537.36");
//
//        webView.getSettings().setUserAgentString("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.96 Safari/537.36");
        webView.getSettings().setUseWideViewPort(true);
        reload = true;
        webView.loadUrl(webView.getUrl());
        reload = false;




    }

    public void copy_url(View view) {
        ClipboardManager clipboardManager = (ClipboardManager) ContextCompat.getSystemService(this, ClipboardManager.class);

        assert clipboardManager != null;
        clipboardManager.setPrimaryClip(ClipData.newPlainText(webView.getTitle(),webView.getUrl()));
    }

    public void downnloadpage(View view) throws MalformedURLException {
        URL url = new URL(webView.getUrl());
        webView.saveWebArchive(url.getHost());
    }


    class MyWebChrome extends WebChromeClient {




        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
            File logo;
            String Logo_name = Table_name+"_LOGO"+".png";
            try {
                {if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    ImageView i = findViewById(R.id.icon);
                    i.setImageBitmap(icon);
                    logo = new File(getBaseContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), Logo_name);
                    FileOutputStream logo_ = new FileOutputStream(logo);
                    Boolean b = icon.compress(Bitmap.CompressFormat.PNG, 100, logo_);
                    Log.v("MainActivity/OnReceivedIcon",  b+ ""+logo.getAbsolutePath());
                }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }


        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            ProgressBar p =findViewById(R.id.progress_bar);
            p.setProgress(webView.getProgress());
        }
    }


    class MyWebViewClient extends WebViewClient {

    @Override
    public void onPageFinished(WebView webView, String str) {
        findViewById(R.id.progress_bar).setVisibility(View.GONE);
        File file,logo;



        String filenameExternal = Table_name+".png";
//        String Logo_name = Table_name+"_LOGO"+".png";

        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                // webpage bitmap
                {
                    file = new File(getBaseContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), filenameExternal);
                    FileOutputStream ostream = new FileOutputStream(file);
                    Bitmap image = Bitmap.createBitmap(webView.getWidth(), webView.getWidth() * 16 / 15, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(image);
                    webView.draw(canvas);
                    boolean b = image.compress(Bitmap.CompressFormat.PNG, 100, ostream);

                    Log.v("MainActivity/Writing", "url is writen to local storage :" + b);
                }




            }
            else Log.v("MainActivity/Writing","if condition is getting failed");
        }
        catch (Exception e){

            Log.e("MainActivity/writing",e.getLocalizedMessage());
        }
        super.onPageFinished(webView,str);
    }

        @Override
        public void onLoadResource(WebView view, String url) {
            if (reload) {
                super.shouldOverrideUrlLoading(view, url);
                reload = false;
                return;
            }
            super.onLoadResource(view, url);
        }


        @Override
    public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
        if (store_history) History.add(str,Others.HISTORY);


        super.onPageStarted(webView,str,bitmap);

            if (str.equals("http://home-page/") || str.equals(home)){
            searchview.clearFocus();
            webView.setVisibility(View.GONE);
            home_view.setVisibility(View.VISIBLE);
            return;
        }
            Log.v("onPageStarted",str+" loading ");
        searchview.setQuery(str,false);
        searchview.clearFocus();
        webView.setVisibility(View.VISIBLE);
        home_view.setVisibility(View.GONE);
        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);

    }


        @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String str) {
        db.add_url(Table_name,str);
        toogle();
        return super.shouldOverrideUrlLoading(webView, str);
    }

}



}