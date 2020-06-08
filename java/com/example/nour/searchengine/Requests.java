package com.example.nour.searchengine;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class Requests   {

    public void getTenLinks(final Context context,final String index,final String searchQuery) {
        String connectionString = Constants.LINKS_URL+"index="+index+"&searchQuery="+searchQuery;

        Uri.Builder builder = Uri.parse(connectionString).buildUpon();

        StringRequest stringrequest = new StringRequest(Request.Method.GET,
                builder.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            int numberOfLinks = jsonObject.getInt("NumberOfLinks");
                            JSONArray jsonArray = jsonObject.getJSONArray("Links");
                            final ArrayList<SearchItem> links = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {


                                JSONObject link = jsonArray.getJSONObject(i);
                                String search_title = link.getString("title");
                                String search_url=link.getString("url");
                                String serch_content = link.getString("content");



                                SearchItem Item = new SearchItem(serch_content,search_title,search_url);
                                links.add(Item);


                            }
                            Constants.LINKS.LinksResponse(links,numberOfLinks);

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();

                    }
                })


        {

            @Override
            protected Map<String, String> getParams() {


                Map<String, String> params = new HashMap<>();
                params.put("index", index);
                params.put("searchQuery", searchQuery);
                Toast.makeText(context, params.toString(), Toast.LENGTH_SHORT).show();

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "null");
                return headers;
            }
        };
        stringrequest.setRetryPolicy(new DefaultRetryPolicy(
                100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(context).addToRequestQueue(stringrequest);

    }
}
