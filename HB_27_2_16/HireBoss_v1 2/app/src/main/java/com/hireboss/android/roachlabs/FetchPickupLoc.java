package com.hireboss.android.roachlabs;
/*
 *Author:Gurdev Singh
 *date  :8 jan 2016
 *version:1.0
 */
import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FetchPickupLoc extends AppCompatActivity implements OnMapReadyCallback {
    String url;
    private static final String TAG_RESULT = "predictions";
    JSONObject json;
    JSONArray contacts = null;
    AutoCompleteTextView ed;
    String[] search_text;
    ArrayList<String> names;
    ArrayAdapter<String> adp;
    String browserKey="AIzaSyBjHJV0EtRJhWSQCOQ6bkvzMLEncGT1deQ";
    double lat,lon;
    LatLng starts;
    Button next;
    public ArrayAdapter<String> adapter;
    private GoogleMap mMap;
    String addressList[]=new String [3];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_pickup_loc);
        next=(Button)findViewById(R.id.nextbutpickup);
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
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ed=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);
        ed.setThreshold(0);
        names=new ArrayList<String>();
        ed.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                int len = ed.getText().length() - 1;
                if (ed.getText().toString().isEmpty()) {
                    Log.d("gdev", "empty");
                } else if (ed.getText().toString().charAt(len) != ' ') {
                    search_text = ed.getText().toString().split(" ");
                    Log.d("gdev", search_text[0]);
                    url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + search_text[0] + "&radius=500&sensor=true&key=" + browserKey;
                    if (search_text.length <= 2) {
                        names = new ArrayList<String>();
                        Log.d("URL", url);
                        paserdata parse = new paserdata();
                        parse.execute();
                    } else {
                    }
                }

            }
        });
     starts=new LatLng(0.0,0.0);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(starts.latitude!=0.0  ) {

                        SharedPreferences sp=getSharedPreferences("LOCATION", Context.MODE_APPEND);
                        SharedPreferences.Editor editor=sp.edit();
                        editor.putString("pickuplon",starts.longitude+"");
                        editor.putString("pickuplat",starts.latitude+"");
                        editor.commit();


                        Intent next = new Intent(getApplicationContext(), FetchDropLoc.class);

                        startActivity(next);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "please select a location", Toast.LENGTH_SHORT).show();

                    }


            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);




        // Add a marker in Sydney and move the camera
        LatLng pune = new LatLng(18.5203, 73.8567);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pune));
        LatLng start=new LatLng(lat,lon);
        CameraPosition cameraPosition=new CameraPosition.Builder()
                .target(pune)
                .zoom(12)
                .bearing(300)
                .tilt(90)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    public void geosuggest(View v) throws IOException
    {
        AutoCompleteTextView et=(AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        String locationName=et.getText().toString();

        Geocoder geo = new Geocoder(getBaseContext());



        List<Address> listOfAddress = geo.getFromLocationName(locationName, 1);;
        try {
            listOfAddress = geo.getFromLocationName(locationName, 1);
            if(listOfAddress != null && !listOfAddress.isEmpty()){
                Address address = listOfAddress.get(0);

                String country = address.getCountryCode();
                String adminArea= address.getAdminArea();
                String locality= address.getLocality();
                lat = address.getLatitude();
                lon = address.getLongitude();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*TextView et=(TextView) findViewById(R.id.geotext);
        Spinner ad=(Spinner)findViewById(R.id.addresslist);
        String location =et.getText().toString();
        Geocoder gc=new Geocoder(this);
        List<Address> list=gc.getFromLocationName(locationName,1);
        if(list != null) {

            for (int j=0; j<1; j++){
                Address returnedAddress = list.get(j);
                StringBuilder strReturnedAddress = new StringBuilder();
                for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                addressList[j] = strReturnedAddress.toString();
            }

            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, addressList);
            adapter.setDropDownViewResource(android.R.layout.
                    simple_spinner_dropdown_item);
            ad.setAdapter(adapter);
        }*/
     //   Toast.makeText(this,lat+":"+lon,Toast.LENGTH_LONG).show();
        starts=new LatLng(lat,lon);
        CameraPosition cameraPosition=new CameraPosition.Builder()
                .target(starts)
                .zoom(20)
                .bearing(300)
                .tilt(90)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.addMarker(new MarkerOptions().position(starts).title("Pickup"));
        Log.d("gdev", starts.latitude + "");
        Log.d("gdev",starts.longitude+"");

        try  {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {

        }
    }
    public class paserdata extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub


            JSONParser jParser = new JSONParser();

            // getting JSON string from URL
            json = jParser.getJSONFromUrl(url.toString());
            if(json !=null)
            {
                try {
                    // Getting Array of Contacts
                    contacts = json.getJSONArray(TAG_RESULT);

                    for(int i = 0; i < contacts.length(); i++){
                        JSONObject c = contacts.getJSONObject(i);
                        String description = c.getString("description");
                        Log.d("description", description);
                        names.add(description);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            adp = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_1, names) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text = (TextView) view.findViewById(android.R.id.text1);
                    text.setTextColor(Color.BLACK);
                    return view;
                }
            };


            ed.setAdapter(adp);


        }
    }
}
