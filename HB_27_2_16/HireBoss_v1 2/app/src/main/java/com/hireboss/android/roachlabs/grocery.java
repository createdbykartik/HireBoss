package com.hireboss.android.roachlabs;
/*
 *Author:Gurdev Singh
 *date  :8 jan 2016
 *version:1.0
 */
import android.app.AlertDialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class grocery extends AppCompatActivity {

    float toadd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent prevs=getIntent();
        final EditText what=(EditText)findViewById(R.id.whattoshop);

        //fetch pickupdrop loc from LOCATION sharedpreference
        SharedPreferences sp=getSharedPreferences("LOCATION", Context.MODE_APPEND);

        String  pickuplat=(sp.getString("pickuplat","nodata")),
                pickuplon=(sp.getString("pickuplon", "nodata")),
                droplat=(sp.getString("droplat", "nodata")),
                droplon=(sp.getString("droplat","nodata")),
                type=sp.getString("type","nodata");
        if(type.equalsIgnoreCase("SHOPPING"))
        {
            getSupportActionBar().setTitle("SHOPPING");
        }
        else if(type.equalsIgnoreCase("GROCERY"))

        {
            getSupportActionBar().setTitle("GROCERY");
        }
        else if(type.equalsIgnoreCase("WAIT_FOR_DELIVERY"))
        {
            getSupportActionBar().setTitle("WAIT FOR DELIVERY");
        }
        else if(type.equalsIgnoreCase("PICKUP_AND_DELIVERY"))
        {
            getSupportActionBar().setTitle("PICKUP AND DELIVERY");
        }




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences spacc = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                String account_id = spacc.getString("account_id", "nodata");
                String balance = spacc.getString("balance", "nodata");
                float actual_balance = Float.parseFloat(balance);
                if (actual_balance < 400)
                {
                     toadd=(float)400.00-actual_balance;
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(grocery.this);

                    // set title
                    alertDialogBuilder.setTitle("INSUFFICIENT BALANCE");


                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Minimum balance for hiring a Boss is 400/-")
                            .setCancelable(false)
                            .setPositiveButton("Add to Wallet", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    showInputDialog(toadd);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
                else
                {

                    SharedPreferences sp1 = getSharedPreferences("LOCATION", Context.MODE_APPEND);

                    String pickuplat = (sp1.getString("pickuplat", "nodata")),
                            pickuplon = (sp1.getString("pickuplon", "nodata")),
                            droplat = (sp1.getString("droplat", "nodata")),
                            droplon = (sp1.getString("droplon", "nodata")),
                            type = sp1.getString("type", "nodata");
                    Log.d("gdev", pickuplat + ":" + pickuplon + ":" + droplat + ":" + droplon);


                    String whattext = what.getText().toString();
                    SharedPreferences.Editor editor = sp1.edit();
                    editor.putString("task_description", whattext);
                    editor.commit();
                    //whattext.replace("\n","\\n");


                    WebRequest wb = new WebRequest();
                    wb.execute("http://www.hireboss.co/webservices/request_boss.php?" + "task_description=" + whattext.replace(" ", "%20").replace("\n", ";").replace(",", ";") + "&account_id=" + account_id + "&current_latitude=" + pickuplat + "&current_longitude=" + pickuplon + "&task_title=" + type + "&user_type=users" + "&destination_latitude=" + droplat + "&destination_longitude=" + droplon);
                    Log.d("gdev", "http://www.hireboss.co/webservices/request_boss.php?" + "task_description=" + whattext.replace(" ", "%20") + "&account_id=" + account_id + "&current_latitude=" + pickuplat + "&current_longitude=" + pickuplon + "&task_title=" + type + "&user_type=users" + "&destination_latitude=" + droplat + "&destination_longitude=" + droplon);

//              startActivity(new Intent(getApplicationContext(), com.hireboss.android.roachlabs.MapsActivity.class));
//            Intent prev=getIntent();
//                Toast.makeText(getApplicationContext(),prev.getStringExtra("pickuplats")+":"+prev.getStringExtra("pickuplons"),Toast.LENGTH_SHORT).show();
                    try {
                        copy(new File("/data/data/com.hireboss.android.roachlabs/shared_prefs/LOCATION.xml"),new File("/data/data/com.hireboss.android.roachlabs/shared_prefs/DETAILTASK.xml"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    //webservice call


    public class WebRequest extends AsyncTask<String,String,String> {
        String data;
        String toast;
        String success,message,boss_loc,boss_name,boss_number,boss_lat,boss_lon,request_id,origin_address,destination_address;
        double[] location;
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
                Log.d("data",data);
                success=jobj.getString("success");
                message=jobj.getString("message");
                boss_loc=jobj.getString("boss_location");
                boss_name=jobj.getString("boss_full_name");
                boss_number=jobj.getString("boss_phone");
                location=cordinate(boss_loc);
                boss_lat=location[0]+"";
                boss_lon=location[1]+"";
                request_id=jobj.getString("request_id");
                origin_address=jobj.getString("origin_addresses");
                destination_address=jobj.getString("destination_addresses");
                Log.d("locs",boss_lat+":"+boss_lon);
                SharedPreferences sp=getSharedPreferences("LOCATION",Context.MODE_APPEND);
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("boss_lon",boss_lat);
                editor.putString("boss_lat",boss_lon);
                editor.putString("boss_name",boss_name);
                editor.putString("boss_number",boss_number);
                editor.putString("request_id",request_id);
                editor.putString("origin_address",origin_address);
                editor.putString("destination_address",destination_address);
                editor.commit();
                ////


//                if(!message.contains("already")) {
//                    account_id = jobj.getString("account_id");
//
//                    toast="Your account id:" + account_id;
//                    //    Toast.makeText(MainActivity.this, "Your account id:" + andid, Toast.LENGTH_SHORT).show();
//
//                    SharedPreferences sp=getSharedPreferences("MyData", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor=sp.edit();
//                    editor.putString("account_id", account_id.toString());
//
//                    editor.commit();
//                    Intent intent =new Intent(getApplicationContext(),SmsActivity.class);
//                    startActivity(intent);
//
//
//
//                }
//
//
//                else {
//                  //  toast="already registered";
//                    //Toast.makeText(getApplicationContext(),"already registered",Toast.LENGTH_SHORT).show();}
//                }

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
         ProgressDialog pdia;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdia = new ProgressDialog(grocery.this);
            pdia.setMessage("Finding a Boss nearby ...");
            pdia.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //  textv.append("\n" + s);
            pdia.dismiss();


            if (success == "1") {

                startActivity(new Intent(getApplicationContext(), com.hireboss.android.roachlabs.MapsActivity.class));
              //  Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
            else
            {
                String path="/data/data/com.hireboss.android.roachlabs/shared_prefs/LOCATION.xml";
                File file = new File ( path );
                file.delete();

                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
            Log.d("gdev",success+"success\n"+message+"message");

        }

    }
    //to return latt lonn of a location.eg.12.98789,72.87678
    public double[] cordinate(String cord)
    {
        double latt=0.0,lonn=0.0;
        String breaks="";
        String achar="";
        double lokesan[]=new double[2];
        for(int i=cord.length()-1;i>=0;i--)
        {   achar=cord.charAt(i)+"";
            if(!achar.equals(","))
            {
                breaks=achar+breaks;
            }
            else if(achar.equals(","))
            {
                latt=Double.parseDouble(breaks);
                breaks="";
            }

        }
        lonn=Double.parseDouble(breaks);
        lokesan[0]=latt;
        lokesan[1]=lonn;


        return lokesan;
    }
    protected void showInputDialog(float cash) {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(grocery.this);
        View promptView = layoutInflater.inflate(R.layout.promptpay, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(grocery.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        editText.setText(cash+"");
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                      //  Toast.makeText(grocery.this, editText.getText().toString(), Toast.LENGTH_SHORT).show();
                        String amount=editText.getText().toString();
                        Intent dem=new Intent(getApplicationContext(),PaymentAct.class);
                        dem.putExtra("amounts",amount);
                        startActivity(dem);
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }



    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

}
