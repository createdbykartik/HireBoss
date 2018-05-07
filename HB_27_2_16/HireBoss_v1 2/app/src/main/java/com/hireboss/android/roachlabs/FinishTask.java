package com.hireboss.android.roachlabs;
/*
 *Author:Gurdev Singh
 *date  :8 jan 2016
 *version:1.0
 */
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;

import com.hireboss.android.roachlabs.Util.TextViewWithFont;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FinishTask extends AppCompatActivity {
    ImageView imageView;
    private WebView webView;
    String request_id,account_id;
    TextViewWithFont title,duration,distance,ba,ia,tf,df,cc,tax,at,ap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sploc=getSharedPreferences("LOCATION", Context.MODE_APPEND);
        request_id=sploc.getString("request_id","nodata");

        SharedPreferences spacc=getSharedPreferences("MyData", Context.MODE_PRIVATE);
        account_id=spacc.getString("account_id","nodata");



        SharedPreferences sp=getSharedPreferences("FINISHTASKDETAILS", Context.MODE_PRIVATE);
        String taskTitle=sp.getString("taskTitle","nodata"),
                taskDuration=sp.getString("taskDuration","nodata"),
                taskDistance=sp.getString("taskDistance","nodata"),
                itemAmount=sp.getString("itemAmount","nodata"),
                baseAmount=sp.getString("baseAmount","nodata"),
                timeFare=sp.getString("timeFare","nodata"),
                distanceFare=sp.getString("distanceFare","nodata"),
                convenienceCharges=sp.getString("convenienceCharges","nodata"),
                amountTotal=sp.getString("amountTotal","nodata"),
                taxamount=sp.getString("tax","nodata"),
                amountPaid=sp.getString("amountPaid","nodata"),
                profilepic=sp.getString("picture", "nodata");

        title=(TextViewWithFont)findViewById(R.id.task_value);
        title.setText(taskTitle);
        duration=(TextViewWithFont)findViewById(R.id.taskduration_value);
        duration.setText(taskDuration);
        distance=(TextViewWithFont)findViewById(R.id.taskdistance_value);
        distance.setText(taskDistance);
        ba=(TextViewWithFont)findViewById(R.id.baseamount_value);
        ba.setText(baseAmount);
        ia=(TextViewWithFont)findViewById(R.id.itemamount_value);
        ia.setText(itemAmount);
        tf=(TextViewWithFont)findViewById(R.id.timefare_value);
        tf.setText(timeFare);
        df=(TextViewWithFont)findViewById(R.id.distancefare_value);
        df.setText(distanceFare);
        cc=(TextViewWithFont)findViewById(R.id.conveniencecharges_value);
        cc.setText(convenienceCharges);
        tax=(TextViewWithFont)findViewById(R.id.tax_value);
        tax.setText(taxamount);
        ap=(TextViewWithFont)findViewById(R.id.amountpaid_value);
        ap.setText(amountPaid);




        imageView = (ImageView)findViewById(R.id.profile_image);

        new ImageLoadTask(profilepic, imageView).execute();

//        Intent intents=getIntent();
//        String data=intents.getStringExtra("data");


//            //delete LOCATION sharedpref file
//        File deletePrefFile = new File("/data/data/com.hireboss.android.roachlabs/shared_prefs/LOCATION.xml");
//        deletePrefFile.delete();


        //setting up and loading webView with remote URL
        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new MyWebViewClient());

        //String url = "http://hireboss.co/webservices/map.php?request_id=";
        String url = "http://hireboss.co/webservices/map.php?request_id="+request_id+"&account_id="+account_id;
        Log.d("maps","http://hireboss.co/webservices/map.php?request_id="+request_id+"&account_id="+account_id);
        webView.getSettings().setJavaScriptEnabled(true);
        Log.d("requestid:accountid=",request_id+":"+account_id);

        webView.loadUrl(url);

        //webView loading ends
       Button fin=(Button)findViewById(R.id.buttfin);
        fin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(getApplicationContext(),DialogMediaActivity.class));
            }
        });
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
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
