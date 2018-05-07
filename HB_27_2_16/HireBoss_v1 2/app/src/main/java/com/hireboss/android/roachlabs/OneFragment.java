package com.hireboss.android.roachlabs;
/*
 *Author:Gurdev Singh
 *date  :8 jan 2016
 *version:1.0
 */
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class OneFragment extends AppCompatActivity {


    final String TAG = "RecyclerViewExample";
    List<FeedItem> feedsList;
    RecyclerView mRecyclerView;
    MyRecyclerAdapter adapter;
    ProgressBar progressBar;
    SharedPreferences sp;
    String account_id;
    String result;

    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabcontent_1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OneFragment.this, MainActivitydrawer.class));
            }
        });

        // Inflate the layout for this fragment
        sp=getSharedPreferences("MyData", Context.MODE_PRIVATE);
        account_id=sp.getString("account_id","nodata");
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Initialize recycler view
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.space);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));



        // Downloading data from below url
        final String url = "http://hireboss.co/webservices/task_history.php?account_id="+account_id+"&user_type=users";
        Log.d("url", url);
        new AsyncHttpTask().execute(url);




    }

    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            //setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            HttpURLConnection urlConnection;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                int statusCode = urlConnection.getResponseCode();

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }
                    parseResult(response.toString());
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed to fetch data!";
                }
            } catch (Exception e) {
                // Log.d(TAG, e.getLocalizedMessage());
            }
            return result; //"Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Let us update UI
            progressBar.setVisibility(View.GONE);

            if (result == 1) {
                adapter = new MyRecyclerAdapter(OneFragment.this, feedsList);
                mRecyclerView.setAdapter(adapter);
            } else {
            //    Toast.makeText(OneFragment.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void parseResult(String result) {
        try {
            this.result = result;
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("status");
            feedsList = new ArrayList<>();
            String[] det;
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                FeedItem item = new FeedItem();
                item.setTitle(post.optString("task_title"));
                item.setTime_fare(post.optString("time_fare"));
                item.setThumbnail(post.optString("boss_picture"));
                item.setAmount_total(post.optString("amount_total"));
                item.setBase_amount(post.optString("base_amount"));
                item.setConvenience_charges(post.optString("convenience_charges"));
                item.setDesc(post.optString("task_description"));
                item.setDistance(post.optString("task_distance"));
                item.setDistance_fare(post.optString("distance_fare"));
                item.setDuration(post.optString("task_time"));
                item.setEnd_time(post.optString("finished_at"));
                item.setId(post.optString("id"));
                item.setItem_amount(post.optString("item_amount"));
                item.setItem_desc(post.optString("item_description"));
                item.setStart_time(post.optString("created_at"));
                item.setName(post.optString("boss_name"));




                feedsList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            outRect.top = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0)
                outRect.top = space;
        }
    }

}



