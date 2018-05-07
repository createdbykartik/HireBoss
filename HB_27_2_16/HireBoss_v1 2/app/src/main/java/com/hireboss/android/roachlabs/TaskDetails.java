package com.hireboss.android.roachlabs;
/*
 *Author:Gurdev Singh
 *date  :15 feb 2016
 *version:1.0
 */
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.hireboss.android.roachlabs.Util.TextViewWithFont;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TaskDetails extends AppCompatActivity {
    TextViewWithFont id,desc,title,startt,endt, duration, distance, ba, itemamount,itemdesc, tf, df, cc, tax, at, ap,name,profilepic;
    ImageView imageView;
    Intent prev;
    FeedItem feeditem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TaskDetails.this, OneFragment.class));
            }
        });

//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        feeditem=(FeedItem)getIntent().getExtras().getSerializable("feeditem");

        id=(TextViewWithFont)findViewById(R.id.taskid_value);
        id.setText(feeditem.getId());
        startt=(TextViewWithFont)findViewById(R.id.createdat_value);
        startt.setText(feeditem.getStart_time());
        endt=(TextViewWithFont)findViewById(R.id.finishedat_value);
        endt.setText(feeditem.getEnd_time());
        title=(TextViewWithFont)findViewById(R.id.task_value);
        title.setText(feeditem.getTitle());
        desc=(TextViewWithFont)findViewById(R.id.taskdescription_value);
        desc.setText(feeditem.getDesc());
        duration=(TextViewWithFont)findViewById(R.id.tasktime_value);
        duration.setText(Integer.parseInt(feeditem.getDuration())+" minutes");
        distance=(TextViewWithFont)findViewById(R.id.taskdistance_value);
        distance.setText((Double.parseDouble(feeditem.getDistance())/1000)+" km(s)");
        ba=(TextViewWithFont)findViewById(R.id.baseamount_value);
        ba.setText("₹"+feeditem.getBase_amount()+"/-");
        itemamount=(TextViewWithFont)findViewById(R.id.itemamount_value);
        itemamount.setText("₹"+feeditem.getItem_amount()+"/-");
        itemdesc=(TextViewWithFont)findViewById(R.id.itemdescription_value);
        itemdesc.setText(feeditem.getItem_desc());
        tf=(TextViewWithFont)findViewById(R.id.timefare_value);
        tf.setText("₹"+feeditem.getTime_fare()+"/-");
        df=(TextViewWithFont)findViewById(R.id.distancefare_value);
        df.setText("₹"+feeditem.getDistance_fare()+"/-");
        cc=(TextViewWithFont)findViewById(R.id.conveniencecharges_value);
        cc.setText("₹"+feeditem.getConvenience_charges()+"/-");
//        tax=(TextViewWithFont)findViewById(R.id.tax_value);
//        tax.setText(feeditem.gett);
        ap=(TextViewWithFont)findViewById(R.id.amountpaid_value);
        ap.setText("₹"+feeditem.getAmount_total()+"/-");
        name=(TextViewWithFont)findViewById(R.id.username_value);
        name.setText(feeditem.getName());
        imageView=(ImageView)findViewById(R.id.user_pic);
        new ImageLoadTask(feeditem.getThumbnail(), imageView).execute();


    }

    private void parseResult(String result) {
        try {

            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("status");

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                FeedItem item = new FeedItem();
                item.setTitle(post.optString("task_title"));
                item.setThumbnail(post.optString("user_picture"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }

}
