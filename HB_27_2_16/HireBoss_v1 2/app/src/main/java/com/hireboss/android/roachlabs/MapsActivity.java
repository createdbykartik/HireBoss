package com.hireboss.android.roachlabs;
/*
 *Author:Gurdev Singh
 *date  :8 jan 2016
 *version:1.0
 */
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;

import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Toast;
import android.location.LocationListener;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hireboss.android.roachlabs.AbstractRouting;
import com.hireboss.android.roachlabs.GPSTracker;
import com.hireboss.android.roachlabs.R;
import com.hireboss.android.roachlabs.Routing;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public GoogleMap mMap;
    public Location loc1 = new Location("");


    public Location loc2 = new Location("");
    public LatLng start = new LatLng(18.5203, 73.8567);
    public LatLng end = new LatLng(18.4200, 73.80);
    public ArrayList<LatLng> markerPoints;
    public Handler handler;
    public String lat, lon;
    SharedPreferences sp, sp1;
    public String boss_name, boss_number, request_id, account_id, type, task_description,origin_address,destination_address;
    public Marker marker;
    Runnable r;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        sp = getSharedPreferences("LOCATION", Context.MODE_PRIVATE);
        boss_number = sp.getString("boss_number", "nodata");
        request_id = sp.getString("request_id", "nodata");
        boss_name = sp.getString("boss_name", "nodata");
        type = sp.getString("type", "nodata");
        task_description = sp.getString("task_description", "nodata");
        origin_address = sp.getString("origin_address", "nodata");
        destination_address = sp.getString("destination_address", "nodata");
        Log.d("bos_num", boss_number + ":" + request_id);


        sp1 = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        account_id = sp1.getString("account_id", "nodata");

        //handler 10 sec
        /*handler = new Handler();
        final Runnable r = new Runnable() {

            public void run() {
                GPSTracker gps = new GPSTracker(getApplicationContext());
                if (gps.canGetLocation()) {

                    lat = gps.getLatitude();
                    lon = gps.getLongitude();

                    // \n is for new line
                          Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + lat + "\nLong: " + lon, Toast.LENGTH_SHORT).show();
                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }
//                Toast.makeText(getApplicationContext(),"Hello World",Toast.LENGTH_SHORT).show();
                handler.postDelayed(this, 10000);
            }
        };

        handler.postDelayed(r, 1000);
*/
        setUpMapIfNeeded();
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(start)
                .zoom(18)
                .bearing(300)
                .tilt(90)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                3000, null);
        GPSTracker gt = new GPSTracker(this);
        if (gt.canGetLocation) {
            double latitude = gt.getLatitude();
            double longitude = gt.getLongitude();
            loc2.setLatitude(latitude);
            loc2.setLongitude(longitude);
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        String path="/data/data/com.hireboss.android.roachlabs/shared_prefs/FINISHTASK.xml";
        File file = new File ( path );
        Log.d("gdev",path);

        if ( file.exists() )
        {
            SharedPreferences sp=getSharedPreferences("FINISHTASK",Context.MODE_PRIVATE);
            //Toast.makeText(MainActivitydrawer.this, "1 ongoing job", Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(getApplicationContext(),MapsActivity.class));
            String title = sp.getString("task_title","nodata");
            String amount = sp.getString("task_amount","nodata");
            String details=sp.getString("task_description","nodata");

            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsActivity.this);

                // set title
                alertDialogBuilder.setTitle("TASK FINISH REQUEST");


                // set dialog message
                alertDialogBuilder
                        .setMessage(

                                        "TASK DETAILS:" + details + "\n" +
                                        "TOTAL AMOUNT:" + amount + "\n"



                        )
                        .setCancelable(false)
                        .setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences fintas=getSharedPreferences("FINISHTASK",Context.MODE_PRIVATE);
                                handler.removeCallbacksAndMessages(r);
                                handler.removeCallbacksAndMessages(null);
                                WebRequestFin wrf=new WebRequestFin();
                                wrf.execute("http://www.hireboss.co/webservices/task_finish.php?request_id="+request_id+"&account_id="+account_id+"&item_amount="+fintas.getString("task_amount","0nodata")+"&item_description="+fintas.getString("task_description","nodata")+"&user_response=1");
                                Log.d("req", "http://www.hireboss.co/webservices/task_finish.php?request_id="+request_id+"&account_id="+account_id+"&item_amount="+fintas.getString("task_amount","0nodata")+"&item_description="+fintas.getString("task_description","nodata")+"&user_response=1");
                            }
                        })
                        .setNegativeButton("DECLINE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences fintas=getSharedPreferences("FINISHTASK",Context.MODE_PRIVATE);
                                WebRequestFindecl wrfd=new WebRequestFindecl();
                                wrfd.execute("http://www.hireboss.co/webservices/task_finish.php?request_id="+request_id+"&account_id="+account_id+"&item_amount="+fintas.getString("task_amount","0nodata")+"&item_description="+fintas.getString("task_description","nodata")+"&user_response=0");
                               Log.d("req","http://www.hireboss.co/webservices/task_finish.php?request_id="+request_id+"&account_id="+account_id+"&item_amount="+fintas.getString("task_amount","0nodata")+"&item_description="+fintas.getString("task_description","nodata")+"&user_response=1");
                                dialog.dismiss();
                            }
                        });


                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        }
    }


    @Override
    protected void onResume() {
        handler.postDelayed(r, 5000);
        super.onResume();

        setUpMapIfNeeded();

    }


    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

        }
        if (mMap != null) {
            setUpMap();
        }
    }

/*
    public void onZoom(View view)
    {
        if (view.getId() == R.id.Bzoomin)
        {
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
        }
        if (view.getId() == R.id.Bzoomout)
        {
            mMap.animateCamera(CameraUpdateFactory.zoomOut());
        }
    }
*/


    public void onClick(View view) {
        if (view.getId() == R.id.nav) {
            sp = getSharedPreferences("LOCATION", Context.MODE_APPEND);
            String msg = sp.getString("message", "nodata");
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsActivity.this);

            // set title
            alertDialogBuilder.setTitle("TASK DETAILS");


            // set dialog message
            alertDialogBuilder
                    .setMessage(
                            "TASK TITLE:" + type + "\n" +
                                    "TASK DESCRIPTION:" + task_description + "\n" +
                                    "PICKUP ADDRESS:" + origin_address + "\n" +
                                    "DROP ADDRESS:" + destination_address + "\n" +
                                    "BOSS NAME:" + boss_name + "\n"

                    )
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close
                            // current activity

                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        } else if (view.getId() == R.id.call) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + boss_number));
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsActivity.this);

        // set title
        alertDialogBuilder.setTitle("CALL FOR HELP!");


        // set dialog message
        alertDialogBuilder
                .setMessage("Are you sure?")
                .setCancelable(false)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        WebRequestsos wbsos = new WebRequestsos();
                        wbsos.execute("http://www.hireboss.co/webservices/sos.php?" + "account_id=" + account_id +  "&user_type=users");
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        return super.onOptionsItemSelected(item);
    }

    private void setUpMap() {

        //mMap.addMarker(new MarkerOptions().position(start).title("Marker"));
        Location loc1 = new Location("");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);

        SharedPreferences sspp = getSharedPreferences("LOCATION", Context.MODE_APPEND);
        double boss_lat = Double.parseDouble(sspp.getString("boss_lat", "nodata")),
                boss_lon = Double.parseDouble(sspp.getString("boss_lon", "nodata"));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        //blank starting marker
        start = new LatLng(boss_lat, boss_lon);
        marker = mMap.addMarker(new MarkerOptions().position(start).icon(BitmapDescriptorFactory.fromResource(R.drawable.blankico)).visible(false));

        //handler 6 sec
        handler = new Handler();
        r = new Runnable() {

            public void run() {

                marker.remove();
               // Toast.makeText(getApplicationContext(), "marker removed", Toast.LENGTH_SHORT).show();
               // Toast.makeText(getApplicationContext(), "...", Toast.LENGTH_SHORT).show();
                WebRequest wb = new WebRequest();
                wb.execute("http://www.hireboss.co/webservices/boss_location_fetch.php?" + "request_id=" + request_id + "&account_id=" + account_id);
                Log.d("url", "http://www.hireboss.co/webservices/boss_location_fetch.php?" + "request_id=" + request_id + "&account_id=" + account_id);

                SharedPreferences sspp = getSharedPreferences("LOCATION", Context.MODE_APPEND);
                double boss_lat = Double.parseDouble(sspp.getString("boss_lat", "nodata")),
                        boss_lon = Double.parseDouble(sspp.getString("boss_lon", "nodata"));

                start = new LatLng(boss_lat, boss_lon);
                marker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.bossmarker)).position(start).title("BOSS").visible(true));


                handler.postDelayed(this, 5000);
            }
        };
        //now just 1 sec
        handler.postDelayed(r, 0);


        Log.d("f loc", boss_lat + ":" + boss_lon);


//        double distanceInMeters = loc1.distanceTo(loc2);
//        Toast.makeText(getApplicationContext(), "Distance in meters" + distanceInMeters, Toast.LENGTH_LONG).show();


        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .waypoints(start, end)
                .key("AIzaSyA054qxL-pxqfklC8jK3jw-hV5IbxHta6g")
                .build();
        routing.execute();

//        Location loc=new Location("Arj");
//        loc.setLongitude(18.5203);
//        loc.setLatitude(73.8567);


//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
////                MarkerOptions z;
////                z = new MarkerOptions()
////                        .position(start);
////                //  loc.setLatitude(loc.getLatitude()-0.01);
//                //      loc.setLongitude(loc.getLongitude()-0.01);
//
//            }
//        },5000);

    }


    @Override
    public void onBackPressed() {

        moveTaskToBack(true);

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.hireboss.android.roachlabs/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.arjun.demomaps/http/host/path")
        );
        // AppIndex.AppIndexApi.end(client, viewAction);
        // client.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //    handler.removeCallbacks();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    //webservices


    public class WebRequest extends AsyncTask<String, String, String> {
        String data;
        String toast;
        String success, message, account_id;

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader br = null;
            StringBuffer sb = null;
            data = null;
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
                data = sb.toString();

                //json parsing when array based
            /*
            JSONObject jobj=new JSONObject(data);
            JSONArray jarr=jobj.getJSONArray("");//array name
            JSONObject finaljobj=jarr.getJSONObject(0);//

            String key1=finaljobj.getString("key1")//keys retrieval
            String key2=finaljobj.getString("key2")//keys retrieval
            */
                //json non array based
                JSONObject jobj = new JSONObject(data);
                success = jobj.getString("success");
                message = jobj.getString("message");
                lat = jobj.getString("latitude");
                lon = jobj.getString("longitude");
                Log.d("loc", lat + ":" + lon);
//                account_id = jobj.getString("account_id");
//
//                toast="Your account id:" + account_id;
//                //    Toast.makeText(MainActivity.this, "Your account id:" + andid, Toast.LENGTH_SHORT).show();

                SharedPreferences locs = getSharedPreferences("LOCATION", Context.MODE_APPEND);
                SharedPreferences.Editor editor = locs.edit();
                editor.putString("boss_lat", lat);
                editor.putString("boss_lon", lon);

                editor.commit();

//                Intent intent =new Intent(getApplicationContext(),SmsActivity.class);
//                startActivity(intent);


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
//            pdia = new ProgressDialog(MapsActivity.this);
//            pdia.setMessage("Tracking BOSS...");
//            pdia.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //  textv.append("\n" + s);
//            pdia.dismiss();
            //  Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onPause() {
        handler.removeCallbacksAndMessages(r);
        handler.removeCallbacks(r);
        handler.removeCallbacksAndMessages(null);
        super.onPause();
    }

    //webservice general
    public class WebService extends AsyncTask<String, String, String> {
        String data;
        String toast;
        String success, message, account_id;

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader br = null;
            StringBuffer sb = null;
            data = null;
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
                data = sb.toString();

                //json parsing when array based
            /*
            JSONObject jobj=new JSONObject(data);
            JSONArray jarr=jobj.getJSONArray("");//array name
            JSONObject finaljobj=jarr.getJSONObject(0);//

            String key1=finaljobj.getString("key1")//keys retrieval
            String key2=finaljobj.getString("key2")//keys retrieval
            */
                //json non array based
//                JSONObject jobj = new JSONObject(data);
//                success = jobj.getString("success");
//                message = jobj.getString("message");
//                Log.d("loc", lat + ":" + lon);
////                account_id = jobj.getString("account_id");
//
//                toast="Your account id:" + account_id;
//                //    Toast.makeText(MainActivity.this, "Your account id:" + andid, Toast.LENGTH_SHORT).show();

//                SharedPreferences locs = getSharedPreferences("LOCATION", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = locs.edit();
//                editor.putString("boss_lat", lat);
//                editor.putString("boss_lon", lon);
//
//                editor.commit();

//                Intent intent =new Intent(getApplicationContext(),SmsActivity.class);
//                startActivity(intent);


//Toast.makeText(getApplicationContext(),"already registered",Toast.LENGTH_SHORT).show();}

                return data;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            catch (JSONException e) {
//                e.printStackTrace();
//            }
            finally {
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
            pdia = new ProgressDialog(MapsActivity.this);
            pdia.setMessage("Ending Task ...");
            pdia.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //  textv.append("\n" + s);
            pdia.dismiss();
            Intent intents=new Intent(getApplicationContext(),FinishTask.class);
            intents.putExtra("jsondata",data);
            startActivity(intents);
            //  Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();

        }

    }
    public class WebRequestFin extends AsyncTask<String,String,String>  {
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
                String taskTitle,
                        taskDuration,
                        taskDistance,
                        itemAmount,
                        baseAmount,
                        timeFare,
                        distanceFare,
                        convenienceCharges,
                        amountTotal,
                        tax,
                        amountPaid,
                        profilepic;
                JSONObject jobj=new JSONObject(data);
               Log.d("datas",data);
                taskTitle=jobj.getString("title");
                taskDuration=jobj.getString("task_time");
                taskDistance=jobj.getString("task_distance");
                itemAmount=jobj.getString("item_amount");
                baseAmount=jobj.getString("base_amount");
                timeFare=jobj.getString("time_fare");
                distanceFare=jobj.getString("distance_fare");
                convenienceCharges=jobj.getString("convenience_charges");
                amountTotal=jobj.getString("amount_total");
                tax=jobj.getString("tax");
                amountPaid=jobj.getString("amount_paid");
                profilepic = jobj.getString("boss_picture");

                SharedPreferences fin=getSharedPreferences("FINISHTASKDETAILS",Context.MODE_PRIVATE);
                SharedPreferences.Editor editors=fin.edit();
                editors.putString("taskTitle",taskTitle);
                editors.putString("taskDuration",taskDuration);
                editors.putString("taskDistance",taskDistance);
                editors.putString("itemAmount",itemAmount);
                editors.putString("baseAmount",baseAmount);
                editors.putString("timeFare",timeFare);
                editors.putString("distanceFare",distanceFare);
                editors.putString("convenienceCharges",convenienceCharges);
                editors.putString("amountTotal",amountTotal);
                editors.putString("tax",tax);
                editors.putString("amountPaid",amountPaid);
                editors.putString("picture", profilepic);
                editors.commit();




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
            pdia = new ProgressDialog(MapsActivity.this);
            pdia.setMessage("Loading...");
            pdia.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //  textv.append("\n" + s);
            pdia.dismiss();
            Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MapsActivity.this,FinishTask.class));
        }

    }


    public class WebRequestFindecl extends AsyncTask<String,String,String>  {
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



//Toast.makeText(getApplicationContext(),"already registered",Toast.LENGTH_SHORT).show();}

                return data;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }  finally {
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
            pdia = new ProgressDialog(MapsActivity.this);
            pdia.setMessage("Loading...");
            pdia.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //  textv.append("\n" + s);
            pdia.dismiss();
        //    Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();

        }

    }

    public class WebRequestsos extends AsyncTask<String,String,String>  {
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



//Toast.makeText(getApplicationContext(),"already registered",Toast.LENGTH_SHORT).show();}

                return data;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }  finally {
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
            pdia = new ProgressDialog(MapsActivity.this);
            pdia.setMessage("Loading...");
            pdia.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //  textv.append("\n" + s);
            pdia.dismiss();
         //   Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();

        }

    }

}