package com.hireboss.android.roachlabs;
/*
 *Author:Gurdev Singh
 *date  :8 jan 2016
 *version:1.0
 */
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.content.DialogInterface;
import android.widget.Toast;

import com.hireboss.android.roachlabs.Util.ConnectionDetection;
import com.hireboss.android.roachlabs.notification.GcmInitializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    final Context context = this;
     String andid,msg;
    double latitude,longitude;
    String gcm_token;
    //protected TextView textv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        final EditText mob=(EditText)findViewById(R.id.mobno);
      //  mob.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        Button send=(Button)findViewById(R.id.btsend);

//TO CHECK CONNECTION
        ConnectionDetection cd = new ConnectionDetection(this);
        Boolean conn = cd.isConnectingToInternet();
//IMEI
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String device_id = tm.getDeviceId();
//ANDROID ID
        final String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID).toString();
//IP ADDRESS

        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
//LOCATION
        GPSTracker gps = new GPSTracker(this);

        // check if GPS enabled
        for(int i=1;i<=1;i++) {
            if (gps.canGetLocation()) {

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                // \n is for new line
          //      Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }
        }
        SharedPreferences gcmsp=getSharedPreferences("GCMDATA",Context.MODE_PRIVATE);
        gcm_token =gcmsp.getString("gcm_token","nodata");

        String s="?";
        s += "gcm_token="+gcm_token+"&";
        s += "imei="+device_id+"&";
        s += "android_id="+android_id+"&";
        s += "carrier_name="+tm.getSimOperatorName().replace(" ","")+"&";
        s += "ip_addr=" + IpAddr.getIPAddress(true)+"&";
//        s += "longitude="+ longitude+"&";
//        s += "latitude=" + latitude+"&";
//check internet connection
        if (conn == true) {
           // textv.append("..connected..\n" + s);
            final String str="http://hireboss.co/webservices/registration.php"+s;
            //String data="";

            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    String mobile_temp=mob.getText().toString();
                    if(mobile_temp.length()!=10){mob.setText("");Toast.makeText(getApplicationContext(), "Incorrect Number", Toast.LENGTH_SHORT).show();}
                    else {

                        WebRequest wb = new WebRequest();
                        wb.execute(str + "mobile_no=" + mobile_temp.replace(" ", "") + "&device_model=" + android.os.Build.DEVICE+"&user_type=users");

//                        Intent otpwait=new Intent(getApplicationContext(),OtpWait.class);
//                        startActivity(otpwait);

                        //{Toast.makeText(MainActivity.this,"Your account id:"+andid, Toast.LENGTH_SHORT).show();}

                        //Toast.makeText(MainActivity.this,"Your account id:"+andid, Toast.LENGTH_SHORT).show();
                        //Toast.makeText(MainActivity.this, str + "mobile_no=" + mobile_temp + "&device_model=" + android.os.Build.DEVICE, Toast.LENGTH_SHORT).show();
                    }

                }
            });






        } else {
            new AlertDialog.Builder(context)
                    .setTitle("No Internet Detected")
                    .setMessage("Wish to go to settings?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            moveTaskToBack(true);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            }
    }



    //fetch account_id received by web service
    public void getandid(String and)
    {
        andid=and;
    }


 public class WebRequest extends AsyncTask<String,String,String>  {
    String data;
     String toast;
String success,message,account_id;
    @Override
    protected String doInBackground(String... params) {

        HttpURLConnection connection = null;
        BufferedReader br = null;
        StringBuffer sb = null;
        data=null;
        try {
            URL url;
            url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            // response received in input stream

            InputStream is = connection.getInputStream();

            br = new BufferedReader(new InputStreamReader(is));
            //reading o/p line by line

            sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data=sb.toString();

            //json parsing when array based
            /*
            JSONObject jobj=new JSONObject(data);
            JSONArray jarr=jobj.getJSONArray("");//array name
            JSONObject finaljobj=jarr.getJSONObject(0);//

            String key1=finaljobj.getString("key1")//keys retrieval
            String key2=finaljobj.getString("key2")//keys retrieval
            */
            //json non array based
            JSONObject jobj=new JSONObject(data);
             success=jobj.getString("success");
              message=jobj.getString("message");

                account_id = jobj.getString("account_id");
                getandid(account_id);
            //    toast="Your account id:" + account_id;
            //    Toast.makeText(MainActivity.this, "Your account id:" + andid, Toast.LENGTH_SHORT).show();

                SharedPreferences sp=getSharedPreferences("MyData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("account_id", account_id.toString());
                editor.putString("balance",0.00+"");

                editor.commit();
                Intent intent =new Intent(getApplicationContext(),SmsActivity.class);
                startActivity(intent);


//Toast.makeText(getApplicationContext(),"already registered",Toast.LENGTH_SHORT).show();}

            return data;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        return null;
    }
     private ProgressDialog pdia;
     @Override
     protected void onPreExecute() {
         super.onPreExecute();
         pdia = new ProgressDialog(context);
         pdia.setMessage("Loading...");
         pdia.show();
     }

     @Override
     protected void onPostExecute(String s) {
        super.onPostExecute(s);
      //  textv.append("\n" + s);
        pdia.dismiss();
    //     Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();

     }

}
}
