package com.hireboss.android.roachlabs;
/*
 *Author:Gurdev Singh
 *date  :8 jan 2016
 *version:1.0
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class shopping extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final EditText what=(EditText)findViewById(R.id.whattoshop);
        final EditText from=(EditText)findViewById(R.id.fromwhere);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String whattext=what.getText().toString();

                String fromtext=from.getText().toString();
                Toast.makeText(shopping.this,"what:"+whattext+"\nfrom:"+fromtext, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), com.hireboss.android.roachlabs.MapsActivity.class));


            }
        });
    }

}
