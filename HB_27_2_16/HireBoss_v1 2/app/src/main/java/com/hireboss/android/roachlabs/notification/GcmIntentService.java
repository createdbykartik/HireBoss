/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hireboss.android.roachlabs.notification;
/*
 *modified by Gurdev Singh
 *date  :10 jan 2016
 *version:1.0
 */
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hireboss.android.roachlabs.FinishTask;
import com.hireboss.android.roachlabs.MainActivitydrawer;
import com.hireboss.android.roachlabs.MapsActivity;
import com.hireboss.android.roachlabs.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/*

{
   titre="",
   message="",
   image="",
   id=""
}

*/

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    String  task_amount,
            task_title,
            task_description;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    public static final String TAG = "GCM";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        String titre = extras.getString("titre");
        task_title = titre;
        String message = extras.getString("message");

        String image = extras.getString("image");
        String id = extras.getString("id");

        //json response extraction


        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle

            Log.d("GCM", "RECEIVE CONTENT : " + extras.toString());

            /*
             * Filter messages based on message type. Since it is likely that GCM will be
			 * extended in the future with new message types, just ignore any message types you're
			 * not interested in, or that you don't recognize.
			 */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //sendNotification("Send error: " + extras.toString(),"");
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                //sendNotification("Deleted messages on server: " + extras.toString(),"");
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.

//                Intent viewIntent = new Intent(this,FinishTask.class);
//                viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(viewIntent);

                if (task_title.equals("TASK FINISH REQUEST")) {
                    try {
                        JSONObject jobj = new JSONObject(message);
                        task_amount = jobj.getString("item_amount");
                        task_description = jobj.getString("item_description");

                        SharedPreferences sp = getSharedPreferences("FINISHTASK", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("task_title", task_title);
                        editor.putString("task_description", task_description);
                        editor.putString("task_amount", task_amount);
                        editor.commit();

                        Log.d("json", message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d("GCM", "RECEIVE");

                }//json response extraction
                else
                {
                    try {
                        JSONObject jobj = new JSONObject(message);
                        task_amount = jobj.getString("item_amount");
                        task_description = jobj.getString("item_description");

                        SharedPreferences sp = getSharedPreferences("FINISHTASK", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("task_title", task_title);
                        editor.putString("task_description", task_description);
                        editor.putString("task_amount", task_amount);
                        editor.commit();

                        Log.d("json", message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Intent viewIntent = new Intent(this, MapsActivity.class);
                viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(viewIntent);


                sendNotification(titre, message, image, id);
            }

            // Release the wake lock provided by the WakefulBroadcastReceiver.
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        }
    }


    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String titre, String message, String image, String id) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d("messge gcm : ",""+message);
        Log.d("GCM", "NOTIFICATION");

        //afficher la notif

        try {
            Vibrator v = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(new long[]{0,100,300,200},-1);
        } catch (Exception e) {
        }


        PackageManager pm = getPackageManager();


        Bitmap bitmap = getBitmapFromURL(image);

        Notification notif;
        {
            Intent lancement = new Intent(this.getApplicationContext(), MapsActivity.class);
            lancement.putExtra("LAUNCH_NOTIFICATION", true);
            Log.d("notify","tasktitle"+task_title+":taskamt"+task_amount+":taskdesc"+task_description);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, lancement, 0);


            Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notif =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.hbsss)
                            .setLargeIcon(bitmap)
                            .setColor(getResources().getColor(R.color.orange))
                            .setContentTitle(titre)
                            .setContentText("Tap for Details")
                            .setVibrate(new long[]{300,100,0,300,100,0,300,100,0,300,100,0,300,100,0,300,100,0,300,100,0,300,100,0,300,100,0,300,100,0,300,100,0,300,100,0}).setSound(uri)
                            .setContentIntent(contentIntent)
                            .build();

            notif.ledARGB = getResources().getColor(R.color.orange);
            notif.flags = Notification.FLAG_SHOW_LIGHTS;
            notif.ledOnMS = 750;
            notif.ledOffMS = 2000;

        }

        //TODO mettre le nombre et un id différent
        notif.flags |= Notification.FLAG_AUTO_CANCEL;

        if (id == null)
            id = "";

        mNotificationManager.notify(id, id.hashCode(), notif);


    }

}