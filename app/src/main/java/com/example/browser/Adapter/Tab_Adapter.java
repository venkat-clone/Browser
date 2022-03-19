package com.example.browser.Adapter;

import static com.example.browser.MainActivity.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.text.Transliterator;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.browser.MainActivity;
import com.example.browser.R;
import com.example.browser.Tabes_Activity;
import com.example.browser.modal.Tabs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;

public class Tab_Adapter extends RecyclerView.Adapter<Tab_Adapter.ViewHolder> {
    private final Context context;
    public ArrayList<Tabs> Tab_list;
    private Context baseContext;

    public String url;

    public File Dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

    public Tab_Adapter(Context context,Context baseContext, ArrayList<Tabs> contactList) {
        this.context = context;
        this.Tab_list = contactList;
        this.baseContext = baseContext;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tab, parent, false);
        return new ViewHolder(view);
    }
    public String getStringFromFile(String filePath) throws Exception{
        File file = new File(filePath);
        FileInputStream fInputStream = new FileInputStream(file);
        String wantedString = String.valueOf((fInputStream));
        fInputStream.close();
        return  wantedString;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String url = Tab_list.get(position).getUrl();
        if (!url.equals(home)) holder.textView.setText(url.substring(url.indexOf(".")+1));
        else holder.textView.setText("New Tab");

        holder.context = context;
        holder.Table_name = Tab_list.get(position).getTable_name();



        if (Tabes_Activity.Present.equals(holder.Table_name)){
            holder.tab.setSelected(true);
            holder.itemView.setPadding(3,3,3,3);
        }
        else {
            holder.tab.setSelected(false);
            holder.itemView.setPadding(0,0,0,0);

        }


        String filenameExternal = holder.Table_name+".png";
        String logo_file = holder.Table_name+"_LOGO"+".png";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.v("Tab_Adapter/reading", "Trying to read");
            holder.file = new File(baseContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), filenameExternal);
            holder.image = new File(baseContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), logo_file);
            try {
                FileInputStream fileInputStream = new FileInputStream(holder.file);
                Bitmap myBitmap = BitmapFactory.decodeFile(holder.file.getAbsolutePath());
                Bitmap favi = BitmapFactory.decodeFile(holder.image.getAbsolutePath());
                holder.favicon.setImageBitmap(favi);
                holder.webimage.setImageBitmap(myBitmap);
                Log.v("Tab_Adapter/reading","Tab_name "+holder.Table_name+":file path"+holder.file.getAbsolutePath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                holder.webimage.setImageBitmap(null);
                holder.favicon.setImageBitmap(null);
            }
        }


        holder.itemView.setOnClickListener(v -> {
            String Table_name = Tab_list.get(position).getTable_name();
            MainActivity.Table_id = Integer.parseInt(Table_name.substring(Table_name.indexOf("_")+1));
            Tabes_Activity.open_url(context,position,Tab_list.get(position).getTable_name());
//                ((MainActivity)context).finish();
        });


    }

    @Override
    public int getItemCount() {
        return Tab_list.size();
    }
    public void Add(String Tab_name){
        Tabs tab = new Tabs();
        tab.setUrl(home);
        tab.setTable_name(Tab_name);
        Tab_list.add(tab);
    }
//    public void delete(int index){
//        Tab_list.remove();
//    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textView;
        public ImageView webimage;
        public ImageView remove;
        public Context context;
        public String Table_name;
        public ImageView favicon;
        public RelativeLayout tab;
        File file, image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);



            // View
            {
                tab =itemView.findViewById(R.id.parent);
                textView = itemView.findViewById(R.id.url);
                remove = itemView.findViewById(R.id.remove);
                webimage = itemView.findViewById(R.id.mini);
                favicon = itemView.findViewById(R.id.favicon);
            }


            itemView.setOnClickListener(this::onClick);
            remove.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            Table_name = Tab_list.get(getAdapterPosition()).getTable_name();
            if (v.getId() == itemView.getId()) {

                MainActivity.Table_id = Integer.parseInt(Table_name.substring(Table_name.indexOf("_") + 1));

                ((MainActivity) context).finish();


//                Intent intent = new Intent(context, Display_Url.class);
//
//                intent.putExtra(TABLE_NAME, Tab_list.get(getAdapterPosition()));
            } else if (v.getId() == R.id.remove) {
                Tabes_Activity.Delete_tab(Tab_list.get(getAdapterPosition()).getTable_name(),getAdapterPosition());
                file.delete();
                image.delete();
            }
        }
    }
}
