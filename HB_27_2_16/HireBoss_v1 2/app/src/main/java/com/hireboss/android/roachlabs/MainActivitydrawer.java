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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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

public class MainActivitydrawer extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    LinearLayout layout_wallet_credit;
    TextView total_amount;
    String balance;
    private TextView resultText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maindrawer);
        SharedPreferences sp=getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String account_id=sp.getString("account_id","nodata");

        WebRequest wb = new WebRequest();
        wb.execute("http://www.hireboss.co/webservices/wallet_balance.php?" + "account_id=" + account_id + "&user_type=users");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog();
            }
        });

        /**
         *Setup the DrawerLayout and NavigationView
         */

             mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
             mNavigationView = (NavigationView) findViewById(R.id.shitstuff) ;
             mNavigationView.setItemIconTintList(null);

            //programatically setting menu item for item name_user
            SharedPreferences sp1=getSharedPreferences("FBDATA", Context.MODE_PRIVATE);
            String name=sp1.getString("fname", "nodata");
            Menu menu=mNavigationView.getMenu();
            MenuItem nameuser=menu.findItem(R.id.name_user);
            nameuser.setTitle("Howdy, "+ name+"!");



        /**
         * Lets inflate the very first deliveryfragment
         * Here , we are inflating the TabFragment as the first Fragment
         */

             mFragmentManager = getSupportFragmentManager();
             mFragmentTransaction = mFragmentManager.beginTransaction();
             mFragmentTransaction.replace(R.id.containerView,new TabFragment()).commit();
        /**
         * Setup click events on the Navigation View Items.
         */

             mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                 @Override
                 public boolean onNavigationItemSelected(MenuItem menuItem) {

                     mDrawerLayout.closeDrawers();
                     if(menuItem.getItemId()==R.id.jobs)
                     {
                         startActivity(new Intent(MainActivitydrawer.this,OneFragment.class));
                     }

                     if(menuItem.getItemId()==R.id.abtus)
                     {
                         startActivity(new Intent(MainActivitydrawer.this,RoachLabs.class));
                     }
                     if(menuItem.getItemId()==R.id.ratus)
                     {
                         Toast.makeText(MainActivitydrawer.this,"WAIT FOR IT",Toast.LENGTH_SHORT).show();
                     }

                 if (menuItem.getItemId() == R.id.ongoing) {
                     Context context=getApplicationContext();
                     String path="/data/data/com.hireboss.android.roachlabs/shared_prefs/DETAILTASK.xml";
                     File file = new File ( path );
                     Log.d("gdev",path);

                     if ( file.exists() )
                     {
                         Toast.makeText(MainActivitydrawer.this, "1 ongoing job", Toast.LENGTH_SHORT).show();
                         startActivity(new Intent(getApplicationContext(),MapsActivity.class));
                     }
                     else
                     {
                         // Toast File is not exists
                         Toast.makeText(MainActivitydrawer.this, "No ongoing job", Toast.LENGTH_SHORT).show();
                     }
                 }
                     if (menuItem.getItemId() == R.id.contactus)   {
                         Intent intent = new Intent(Intent.ACTION_DIAL);
                         intent.setData(Uri.parse("tel:8149801649"));
                         startActivity(intent);
                     }
//
//                if (menuItem.getItemId() == R.id.nav_item_inbox) {
//                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
//                    xfragmentTransaction.replace(R.id.containerView,new TabFragment()).commit();
//                }

                     return false;
                 }

             });

        /**
         * Setup Drawer Toggle of the Toolbar
         */


                android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
//        Menu menus=toolbar.getMenu();
//        onCreateOptionsMenu(menus);
                ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name,
                R.string.app_name);



        mDrawerLayout.setDrawerListener(mDrawerToggle);

                mDrawerToggle.syncState();

        //wallet


        layout_wallet_credit = (LinearLayout)findViewById(R.id.layout_wallet_credit);
        total_amount = (TextView)findViewById(R.id.total_amount);
        //total_currency =  (TextView) cview.findViewById(R.id.total_currency);


//
//        layout_wallet_credit.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//
//                total_amount.setText("amount!");
//            }
//        });




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

//        inflater.inflate(R.menu.items, menu);
//        MenuItem item = menu.findItem(R.id.cash);
//        item.setTitle("₹"+"asas");
        return super.onCreateOptionsMenu(menu);
    }




    @Override
    public void onBackPressed() {
        //minimizes the activity
        moveTaskToBack(true);
    }

    // TASK INTENTS

    public void taskSelectGrocery(View v)
    {
        SharedPreferences sp=getSharedPreferences("LOCATION", Context.MODE_APPEND);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("type","GROCERY");
        editor.commit();


        Intent n =new Intent(this,FetchPickupLoc.class);

        startActivity(n);
    }
    public void taskSelectShopping(View v)
    {
        SharedPreferences sp=getSharedPreferences("LOCATION", Context.MODE_APPEND);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("type","SHOPPING");
        editor.commit();

        Intent n =new Intent(this,FetchPickupLoc.class);
        startActivity(n);
    }public void taskSelectWaitfordelivery(View v)
    {
        SharedPreferences sp=getSharedPreferences("LOCATION", Context.MODE_APPEND);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("type","WAIT_FOR_DELIVERY");
        editor.commit();

        Intent n =new Intent(this,FetchPickupLoc.class);
        startActivity(n);
    }public void taskSelectPickupndelivery(View v)
    {
        SharedPreferences sp=getSharedPreferences("LOCATION", Context.MODE_APPEND);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("type","PICKUP_AND_DELIVERY");
        editor.commit();

        Intent n =new Intent(this,FetchPickupLoc.class);
        startActivity(n);
    }

    protected void showInputDialog() {

    // get prompts.xml view
    LayoutInflater layoutInflater = LayoutInflater.from(MainActivitydrawer.this);
    View promptView = layoutInflater.inflate(R.layout.promptpay, null);
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivitydrawer.this);
    alertDialogBuilder.setView(promptView);

       final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
    // setup a dialog window
    alertDialogBuilder.setCancelable(false)
            .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                  //  Toast.makeText(MainActivitydrawer.this, editText.getText().toString(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        super.onResume();
//        Intent intent =new Intent(getApplicationContext(),SmsActivity.class);
//        startActivity(intent);
        SharedPreferences sp=getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String account_id=sp.getString("account_id","nodata");
        balance=sp.getString("balance","nodata");
        WebRequest wb = new WebRequest();
        wb.execute("http://www.hireboss.co/webservices/wallet_balance.php?" + "account_id=" + account_id +  "&user_type=users");

        total_amount.setText(balance+"/-");
    }

    public class WebRequest extends AsyncTask<String,String,String> {
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
                balance=jobj.getString("balance");
//                account_id = jobj.getString("account_id");
//                getandid(account_id);
//                toast="Your account id:" + account_id;
//                //    Toast.makeText(MainActivity.this, "Your account id:" + andid, Toast.LENGTH_SHORT).show();

                SharedPreferences sp = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("balance", balance.toString());

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
//            pdia = new ProgressDialog(context);
//            pdia.setMessage("Loading...");
//            pdia.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //  textv.append("\n" + s);
//            pdia.dismiss();
        //    Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();

        }

    }

}