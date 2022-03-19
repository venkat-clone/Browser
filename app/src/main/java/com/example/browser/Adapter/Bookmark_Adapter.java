package com.example.browser.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.browser.Booksmarks;
import com.example.browser.R;
import com.example.browser.Tabes_Activity;
import com.example.browser.database.Others;

import java.util.ArrayList;

public class Bookmark_Adapter extends RecyclerView.Adapter<Bookmark_Adapter.ViewHolder>{
    Context context;
    Context Basecontext;
    ArrayList<String> url_list;


    public Bookmark_Adapter(Context context, Context basecontext, ArrayList list){
        this.context = context;
        url_list = list;
        Basecontext = basecontext;
    }

    @NonNull
    @Override
    public Bookmark_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Bookmark_Adapter.ViewHolder holder, int pos) {
        int position = pos;


        holder.url.setText(url_list.get(position));
        holder.position = position;
        holder.remove = holder.itemView.findViewById(R.id.Bookmark_remove);
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemRemoved(position);
                Log.v("Bookmark_Adapter/ViewHolder","notified");
                Booksmarks.bookmarks.remove(url_list.get(position),Others.BOOKMARKS);
                url_list.remove(position);
            }
        });

    }


    @Override
    public int getItemCount() {
        return url_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Context context;
        ImageView logo;
        TextView url;
        View ItemView;
        public int position;
        ImageView remove;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        logo = itemView.findViewById(R.id.Bookmarks_logo);
        url = itemView.findViewById(R.id.Bookmark_url);
        ItemView = itemView;
        position = getAdapterPosition();
        remove = (ImageView) itemView.findViewById(R.id.Bookmark_remove);


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Booksmarks)context).finish();
            }
        });

        }
    }
}
