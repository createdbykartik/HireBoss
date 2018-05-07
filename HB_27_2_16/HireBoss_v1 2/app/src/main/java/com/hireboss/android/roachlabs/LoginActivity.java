package com.hireboss.android.roachlabs;
/*
 *Author:Gurdev Singh
 *date  :8 jan 2016
 *version:1.0
 */
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.hireboss.android.roachlabs.view.kbv.KenBurnsView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, ConnectionCallbacks, OnConnectionFailedListener  {
    private KenBurnsView mKenBurns;
    //google start
    private static final int RC_SIGN_IN = 0;

    // Google client to communicate with Google
    private GoogleApiClient mGoogleApiClient;

    private boolean mIntentInProgress;
    private boolean signedInUser;
    private ConnectionResult mConnectionResult;
    private SignInButton signinButton;
    private ImageView image;
    private TextView username, emailLabel;
    private LinearLayout profileFrame, signinFrame;
    //google end

    String fb_id, fb_name, fb_emll, fb_gen, fb_loc, fb_dob, fb_profile_pic, fb_token,fb_token_expires;


    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    AccessToken accessToken;


    //Facebook login button
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Profile profile = Profile.getCurrentProfile();
          //  Toast.makeText(getApplicationContext(), "1st on success was called", Toast.LENGTH_SHORT).show();
            nextActivity(profile);
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onError(FacebookException e) {
         //   Toast.makeText(getApplicationContext(), "onError was called", Toast.LENGTH_SHORT).show();
        }
    };


    Button fb;


    String fname, lname, app_id, emal, token;
    String gpersonName,gpersonPhotoUrl,gemail,gid;


    Context mcontext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
       getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mKenBurns = (KenBurnsView) findViewById(R.id.loginkenburns);
        setAnimation();

        com.google.android.gms.common.SignInButton gbutton=(com.google.android.gms.common.SignInButton)findViewById(R.id.signin);



        mcontext = this;


        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                nextActivity(newProfile);
            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();


        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);


        //older fb code with implemented new code

        callback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                fb_token = accessToken.getToken();
                fb_token_expires=accessToken.getExpires().toString();

                Profile profile = Profile.getCurrentProfile();
                nextActivity(profile);

                Toast.makeText(getApplicationContext(), "Logging in..", Toast.LENGTH_SHORT).show();


                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code
//                                Log.v("LoginActivity", response.toString());


                                String fbdata = getFacebookData(object);
//                                Log.v("LoginActivity", fbdata);

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
          //      Toast.makeText(getApplicationContext(), "executeasync was called", Toast.LENGTH_SHORT).show();
                request.executeAsync();


            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException e) {
            }
        };

        loginButton.setReadPermissions(Arrays.asList("public_profile, email, birthday, user_friends"));
        loginButton.setReadPermissions("user_friends");
        loginButton.setReadPermissions("public_profile");
        loginButton.setReadPermissions("email");
        loginButton.setReadPermissions("user_birthday");
        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday"));

        loginButton.registerCallback(callbackManager, callback);

        //google start
        signinButton = (SignInButton) findViewById(R.id.signin);
        signinButton.setOnClickListener(this);

        image = (ImageView) findViewById(R.id.image);
        username = (TextView) findViewById(R.id.username);
        emailLabel = (TextView) findViewById(R.id.email);

        profileFrame = (LinearLayout) findViewById(R.id.profileFrame);
        signinFrame = (LinearLayout) findViewById(R.id.signinFrame);

        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(Plus.API, Plus.PlusOptions.builder().build()).addScope(Plus.SCOPE_PLUS_LOGIN).build();
        //google ends
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Facebook login
        Profile profile = Profile.getCurrentProfile();
        nextActivity(profile);
    //    Toast.makeText(getApplicationContext(), "onResume was called", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onPause() {

        super.onPause();
    //    Toast.makeText(getApplicationContext(), "onPause was called", Toast.LENGTH_SHORT).show();
    }
    //google start
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
  //      Toast.makeText(getApplicationContext(), "onStart", Toast.LENGTH_LONG).show();
    }
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
     //   Toast.makeText(getApplicationContext(),"resolveSignInerror",Toast.LENGTH_LONG).show();
    }
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }

        if (!mIntentInProgress) {
            // store mConnectionResult
            mConnectionResult = result;

            if (signedInUser) {
                resolveSignInError();
            }
        }
   //     Toast.makeText(getApplicationContext(),"Connection failed",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnected(Bundle arg0) {
        signedInUser = false;
      //  Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
        getProfileInformation();
      //  Toast.makeText(getApplicationContext(),"onConnected",Toast.LENGTH_LONG).show();


    }

    private void updateProfile(boolean isSignedIn) {
        if (isSignedIn) {
            signinFrame.setVisibility(View.GONE);
            profileFrame.setVisibility(View.GONE);
            startActivity(new Intent(LoginActivity.this, MainActivitydrawer.class));
            finish();


        } else {
            signinFrame.setVisibility(View.VISIBLE);
            profileFrame.setVisibility(View.GONE);
        }
     //   Toast.makeText(getApplicationContext(),"updateProfile",Toast.LENGTH_LONG).show();
    }

    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String gpersonName = currentPerson.getDisplayName();
                String gpersonPhotoUrl = currentPerson.getImage().getUrl();
                String gemail = Plus.AccountApi.getAccountName(mGoogleApiClient);
                String gid=currentPerson.getId();

                // String gdateofbirth=currentPerson.getBirthday();
                // String ggender=currentPerson.getGender();
           //     Toast.makeText(LoginActivity.this, gid+"", Toast.LENGTH_SHORT).show();
//                username.setText(gpersonName);
//                emailLabel.setText(gemail);

                new LoadProfileImage(gpersonPhotoUrl,image).execute();
          //      Toast.makeText(getApplicationContext(),"getProfileInfo"+":gender="+currentPerson.getGender()+":birthday="+currentPerson.getBirthday(),Toast.LENGTH_LONG).show();
                // update profile frame with new info about Google Account
                // profile

                //store ito database
                SharedPreferences sp=getSharedPreferences("FBDATA",MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("fname", gpersonName);

                editor.putString("email",gemail);
                editor.putString("fbid", gid);
                editor.putString("profilepic", gpersonPhotoUrl);



                editor.commit();
                SharedPreferences log=getSharedPreferences("MyData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editors=log.edit();
                editors.putString("hbwallet",0+"");
                String acc_id=log.getString("account_id","no acc_id found");
                WebRequest wb = new WebRequest();
                String url="http://hireboss.co/webservices/update_profile.php?account_id="+acc_id+"&full_name="+gpersonName.replace(" ", "%20")+"&email="+gemail+"&social_unique_id="+gid+"&user_type=users&social_type=google&profile_pic="+gpersonPhotoUrl;
                wb.execute(url);

                //db storing ends

                updateProfile(true);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
   //     Toast.makeText(getApplicationContext(),"getProfileInfo",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
        updateProfile(false);
  //      Toast.makeText(getApplicationContext(),"onConnSuspended",Toast.LENGTH_LONG).show();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signin:
                googlePlusLogin();
                break;
        }
      //  Toast.makeText(getApplicationContext(),"onClick",Toast.LENGTH_LONG).show();
    }


    public void signIn(View v) {
       googlePlusLogin();
        //Toast.makeText(getApplicationContext(),"SignIn",Toast.LENGTH_LONG).show();
    }

    public void logout(View v) {
       googlePlusLogout();
   // Toast.makeText(getApplicationContext(),"logout",Toast.LENGTH_LONG).show();
    }

    private void googlePlusLogin() {
        if (!mGoogleApiClient.isConnecting()) {
            signedInUser = true;
            resolveSignInError();
        }
   //     Toast.makeText(getApplicationContext(),"googlepluslogin",Toast.LENGTH_LONG).show();
    }

    private void googlePlusLogout() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            updateProfile(false);
        }
  //      Toast.makeText(getApplicationContext(),"googlepluslogout",Toast.LENGTH_LONG).show();
    }


    public class LoadProfileImage extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public LoadProfileImage(String url, ImageView imageView) {
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


    //google ends

    protected void onStop() {
        super.onStop();
        //Facebook login
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
      //  Toast.makeText(getApplicationContext(), "onStop was called", Toast.LENGTH_SHORT).show();

        //google start
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        //google ends
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        //Facebook login
        switch (requestCode) {
            case RC_SIGN_IN:
                if (responseCode == RESULT_OK) {
                    signedInUser = false;

                }
                mIntentInProgress = false;
                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
                break;

            default:
                callbackManager.onActivityResult(requestCode, responseCode, intent);
                break;


        }
     //   Toast.makeText(getApplicationContext(), "onActivityResult was called", Toast.LENGTH_SHORT).show();
    }
    private void nextActivity(Profile profile) {
        if (profile != null) {

            fname = profile.getFirstName();
            lname = profile.getLastName();
            app_id = profile.getId();

        //    Toast.makeText(this, " " + fname + " " + lname + " \nApp_id\n nextActivity was called" + app_id, Toast.LENGTH_LONG).show();

        }
    }

    private String getFacebookData(JSONObject object) {


        try {

            fb_id = object.getString("id");
            fb_name = object.getString("name");
            fb_emll = object.getString("email");
            fb_gen = object.getString("gender");
            fb_dob = object.getString("birthday");
            fb_loc = object.getJSONObject("location").getString("name");
        //    fb_profile_pic = "https://graph.facebook.com/" + fb_id + "/picture?width=200&height=200";



        //    Toast.makeText(this, "getFacebookData was called " + fb_emll, Toast.LENGTH_LONG).show();


        } catch (
                Exception e
                )

        {
            e.printStackTrace();
        }
      //  Toast.makeText(this, "Facebook " + fb_emll, Toast.LENGTH_LONG).show();


        SharedPreferences sp=getSharedPreferences("FBDATA",MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("fname", fb_name);
        editor.putString("email",fb_emll);
        editor.putString("fbid",fb_id);
        editor.putString("accessToken",fb_token);
        editor.putString("fbgen", fb_gen.toString());
        editor.putString("fbdob", fb_dob.toString());
        editor.putString("profilepic","https://graph.facebook.com/" + fb_id + "/picture?width=200&height=200");


        editor.commit();
        SharedPreferences log=getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editors=log.edit();
        editors.putString("hbwallet",0+"");
        String acc_id=log.getString("account_id","no acc_id found");
        WebRequest wb = new WebRequest();
        String url="http://hireboss.co/webservices/update_profile.php?account_id="+acc_id+"&full_name="+fb_name.replace(" ","%20")+"&date_of_birth="+fb_dob.toString()+"&email="+fb_emll+"&gender="+fb_gen.toString()+"&fb_token="+fb_token+"&social_unique_id="+fb_id+"&user_type=users&social_type=facebook&profile_pic="+fb_profile_pic;
        Log.d("gdev",fb_token_expires+"");
        Log.d("gdev",url);
        wb.execute(url);
        return fb_emll;


    }


    //web service

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

                {

                    //                   account_id = jobj.getString("account_id");

                    //                 toast="Your account id:" + account_id;
                    //    Toast.makeText(MainActivity.this, "Your account id:" + andid, Toast.LENGTH_SHORT).show();

//                    SharedPreferences sp=getSharedPreferences("MyData", Context.MODE_PRIVATE);
                    //                  SharedPreferences.Editor editor=sp.edit();
                    //                editor.putString("account_id", account_id.toString());

//                    editor.commit();
                    //                  Intent intent =new Intent(getApplicationContext(),SmsActivity.class);
                    //                startActivity(intent);



                    Intent in = new Intent(getApplicationContext(), MainActivitydrawer.class);

                    startActivity(in);
                    finish();
                }


                {
//                    toast="already registered";
                    //Toast.makeText(getApplicationContext(),"already registered",Toast.LENGTH_SHORT).show();}
                }

                return "";
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
//
//            Intent in = new Intent(getApplicationContext(), MainActivitydrawer.class);
//
//            startActivity(in);

       //     Toast.makeText(getApplicationContext(),toast, Toast.LENGTH_SHORT).show();



        }

    }

    public void onBackPressed() {
        //minimizes the activity
        moveTaskToBack(true);
    }

    private void setAnimation() {
        mKenBurns.setImageResource(R.drawable.everykenburns);
        animation2();
        animation3();

    }

    private void animation2() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.translate_top_to_center);
    }

    private void animation3() {
          }





}
//isset($_REQUEST['full_name']) && isset($_REQUEST['date_of_birth']) && isset($_REQUEST['email']) && isset($_REQUEST['gender']) && isset($_REQUEST['fb_token']) &&\
// isset($_REQUEST['social_unique_id']) && isset($_REQUEST['account_id']) && isset($_REQUEST['user_type']) && isset($_REQUEST['token_expiry'])
//http://hireboss.co/webservices/update_profile.php?account_id=20160114200783055&full_name=Gurdev%20Singh&date_of_birth=12/22/1994&email=bchm007@gmail.com&gender=male&
// fb_token=CAAcCdfemAtYBAKpHgUTfs3n30g75HjD6lEWWC0He9TvLJmJCLZAAoUVErXRv7nZARJZBOogMKlh0xPxoDZBfCs1KI0FZBvRPBrGLIoujZAvYC8ZB4Sia0wIA08UO49cc43XvZBKpiSuZCayMZC1PNOCNuE3ExQPzHSSqWNQ9kcer3TAxbli93tvJrQygXcsWlx8OcIYlZA2gehpzMgzEqOYUPsrWkB7efYTydkZD&
// social_unique_id=952860801430154&token_expiry=Fri