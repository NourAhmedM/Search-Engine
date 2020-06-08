package com.example.nour.searchengine;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class SearchAdapter extends ArrayAdapter<SearchItem> {

    public SearchAdapter(@NonNull Context context, @NonNull List<SearchItem> objects) {
        super(context, 0, objects);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View ListItemView = convertView;
        if(ListItemView==null){//for making new List_item if there is no main one to change its data
            ListItemView= LayoutInflater.from(getContext()).inflate(R.layout.search_item,parent,false);
        }
        SearchItem CurrentItem=getItem(position);// getting the current word in the arraylist


        TextView content =(TextView) ListItemView.findViewById(R.id.content_snippet);
        content.setText(CurrentItem.getContent());



        TextView title =(TextView) ListItemView.findViewById(R.id.search_title);
        title.setText(CurrentItem.getTitle());

        TextView url =(TextView) ListItemView.findViewById(R.id.url);
        url.setText(CurrentItem.getUrl());


        return ListItemView;
    }
}
