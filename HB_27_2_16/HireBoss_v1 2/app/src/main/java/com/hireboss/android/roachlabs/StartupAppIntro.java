package com.hireboss.android.roachlabs;
/*
 *Author:Gurdev Singh
 *date  :8 jan 2016
 *version:1.0
 */
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.hireboss.android.roachlabs.notification.GcmInitializer;


public class StartupAppIntro extends AppIntro2 implements GcmInitializer.GcmInitialiserCallBack {
    String android_id,device_id, gcm_token;
    @Override
    public void init(Bundle savedInstanceState) {
        SharedPreferences sp=getSharedPreferences("FBDATA",MODE_PRIVATE);
        if(!sp.getString("fname","").equals(""))
        {
            startActivity(new Intent(this,MainActivitydrawer.class));

        }


        addSlide(AppIntroFragment.newInstance("HIRE BOSS", "Now Hire a Boss", R.drawable.hbsss, Color.parseColor("#000000")));
        addSlide(AppIntroFragment.newInstance("INSTANT!", "Get Work Done At The Click Of the Button", R.drawable.intr1, Color.parseColor("#6638b4")));
        addSlide(AppIntroFragment.newInstance("YOUR WISH!", "Want Something from a Specific Store, \n" +
                "Tell Us! We'll Do It!", R.drawable.intr2, Color.parseColor("#ef2a5f")));
        addSlide(AppIntroFragment.newInstance("BE COLUMBUS!","Navigate the way through!", R.drawable.intr3, Color.parseColor("#4185f4")));
        setZoomAnimation();
        new GcmInitializer(this, this).enregistrerCloudMessaginClient();



    }

    private void loadMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("IMEI", device_id);
        intent.putExtra("and_id", android_id);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
    @Override
    public void onGcmInitialised(String token) {
        gcm_token = token;
        SharedPreferences sp=getSharedPreferences("GCMDATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("gcm_token", gcm_token.toString());

        editor.commit();

        //       Toast.makeText(getApplicationContext(),"GCM TOKEN : "+gcm_token, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNextPressed() {
    }


    public void onSkipPressed() {
        loadMainActivity();
        //Toast.makeText(getApplicationContext(),
          //      getString(R.string.skip_button), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDonePressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSlideChanged() {
    }

    public void getStarted(View v){
        loadMainActivity();
    }
}



