package com.hireboss.android.roachlabs;

/*
 *Author:Gurdev Singh
 *date  :9 jan 2016
 *version:1.0
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SmsActivity extends AppCompatActivity {
    Button resend;
    private static SmsActivity inst;
    //    ArrayList<String> smsMessagesList = new ArrayList<String>();
    EditText smsListView;
    //    ArrayAdapter arrayAdapter;
    String code="";
    public static SmsActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsactivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        smsListView = (EditText) findViewById(R.id.txtName);
        resend=(Button)findViewById(R.id.resnd);
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SmsActivity.this,MainActivity.class));
            }
        });
        // arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsMessagesList);
        // smsListView.setAdapter(arrayAdapter);
        // smsListView.setOnItemClickListener(this);



//        refreshSmsInbox();

    }
/*

    public void refreshSmsInbox() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
        // arrayAdapter.clear();
        String str = smsInboxCursor.getString(indexBody);


//        do {
            //        arrayAdapter.add(str);


   Toast.makeText(this, code, Toast.LENGTH_SHORT);
//        } while (smsInboxCursor.moveToNext());
    }
*/


    public void updateList(final String smsMessage) {
        int x=34;

        while(x!=28){code=smsMessage.charAt(x)+code;x--;}
        smsListView.setText(code);
        String str = "http://hireboss.co/webservices/verify_otp.php";
        SharedPreferences sp = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String acid=sp.getString("account_id", "noData");
       WebRequest wb = new WebRequest();
      wb.execute(str + "?otp_code=" + smsListView.getText().toString() + "&account_id=" + acid+"&user_type=users");


//        String str = "http://hireboss.co/webservices/verify_otp.php";
//        SharedPreferences sp = getSharedPreferences("MyData", Context.MODE_APPEND);
//        String acid=sp.getString("account_id", "noData");
//        WebRequest wb = new WebRequest();
//        wb.execute(str + "?otp_code=" + smsListView.getText().toString() + "&account_id=" + acid);

//        smsListView.addTextChangedListener(new TextWatcher() {
//
//            public void afterTextChanged(Editable s) {
//
//
//            }
//
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                Thread t=new Thread(){public void run(){
//                    try{
//                        sleep(2000);
//                    }
//                    catch(Exception e){e.printStackTrace();}
//                    finally{startActivity(new Intent(getApplicationContext() ,MainActivity.class));
//
//                    }
//
//                }};
//                t.start();
//            }
//
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//        });


    }





    public class WebRequest extends AsyncTask<String,String,String> {
        String data;
        String toast;
        String success,message;
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

                if(!success.equals("0")) {
//                    success = jobj.getString("success");
//                    getandid(account_id);
                   toast="Your status:" + success;
                    // Toast.makeText(SmsActivity.this, "status:" + toast, Toast.LENGTH_SHORT).show();

//                    SharedPreferences sp=getSharedPreferences("MyData", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor=sp.edit();
//                    editor.putString("account_id", account_id.toString());

//                    editor.commit();
//                    Intent intent =new Intent(getApplicationContext(),SmsActivity.class);
//                    startActivity(intent);



                }


                else {
                    toast = "fail";
                    // Toast.makeText(getApplicationContext(),toast,Toast.LENGTH_SHORT).show();}
                }


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
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //  textv.append("\n" + s);
//            pdia.dismiss();
//            Toast.makeText(getApplicationContext(),toast, Toast.LENGTH_SHORT).show();
            if(toast.equals("Your status:1"))
            {Intent fblogin = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(fblogin);}
            else{Toast.makeText(getApplicationContext(),""+message,Toast.LENGTH_SHORT).show();}

//           {    PackageManager pm = this.getPackageManager();
//                pm.setComponentEnabledSetting(receiver,
//                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                        PackageManager.DONT_KILL_APP);
//                Toast.makeText(this, 'Disabled broadcst receiver', Toast.LENGTH_SHORT).show();
//            }


        }

    }
}
    /*

    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        try {
            String[] smsMessages = smsMessagesList.get(pos).split("\n");
            String address = smsMessages[0];
            String smsMessage = "";
            for (int i = 1; i < smsMessages.length; ++i) {
                smsMessage += smsMessages[i];
            }

            String smsMessageStr = address + "\n";
            smsMessageStr += smsMessage;
            Toast.makeText(this, smsMessageStr, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
