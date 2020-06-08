package com.example.nour.searchengine;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.Math;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    int pageNum=1;
    int numberOfPages=0;
    String searchQuery="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Requests req = new Requests();
        final ArrayList<SearchItem> SearchItemList=new ArrayList<SearchItem>();

        final LinearLayout shift_layout=(LinearLayout)findViewById(R.id.shift_layout);
        shift_layout.setVisibility(View.INVISIBLE);
        final TextView pages_num=(TextView)findViewById(R.id.page_number);

        final ImageButton left=(ImageButton)findViewById(R.id.left_arrow);

        final ImageButton right=(ImageButton)findViewById(R.id.right_arrow);

        final  SearchAdapter adapter= new SearchAdapter(this,SearchItemList);
        Constants.LINKS= new LinksCallBack(){
            @Override
            public void LinksResponse(ArrayList<SearchItem> links,int numbOfLinks) {
                adapter.clear();
                adapter.addAll(links);
                numberOfPages= (int) Math.ceil(numbOfLinks/10.0);
                pages_num.setText(pageNum+"/"+numberOfPages);
                shift_layout.setVisibility(View.VISIBLE);

            }
        };


        right.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(pageNum!=numberOfPages) {
                    pageNum++;
                    if(pageNum==numberOfPages)
                        right.setClickable(false);
                    req.getTenLinks(MainActivity.this,String.valueOf(pageNum),searchQuery);
                    left.setClickable(true);
                }
                else
                {
                    Toast.makeText(MainActivity.this,"End Of Pages", Toast.LENGTH_LONG).show();
                }
            }
        });

        left.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pageNum>1) {
                    pageNum--;
                    if(pageNum==1)
                        left.setClickable(false);
                    req.getTenLinks(MainActivity.this,String.valueOf(pageNum),searchQuery);
                    right.setClickable(true);

                }
                else
                {

                    Toast.makeText(MainActivity.this,"Start Of Pages", Toast.LENGTH_LONG).show();
                }
            }
        });

        SearchView searchView;
        searchView = (SearchView) findViewById(R.id.search_bar);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                pageNum=1;
                searchQuery=query;
                req.getTenLinks(MainActivity.this,"1",query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });



        final ListView listView =(ListView) findViewById(R.id.search_list);
        listView.setAdapter(adapter);




    }
}
