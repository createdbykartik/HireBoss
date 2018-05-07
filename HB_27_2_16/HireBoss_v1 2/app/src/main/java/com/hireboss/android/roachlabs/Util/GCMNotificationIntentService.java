package com.hireboss.android.roachlabs.Util;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hireboss.android.roachlabs.MainActivitydrawer;
import com.hireboss.android.roachlabs.R;

import org.json.JSONObject;


/*this service calls when notification comes*/
public class GCMNotificationIntentService extends IntentService {


    private static int NOTIFICATION_ID = 1;
    private static int notificationCount = 0;

    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GCMNotificationIntentService() {
        super("GcmIntentService");
    }

    public static final String TAG = "GCMNotificationIntentService";


    @Override
    protected void onHandleIntent(Intent intent) {

        try {

            Bundle extras = intent.getExtras();

            String message = extras.getString("price");

            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

            final int icon = R.drawable.hbsss;
            final String titleAppName = getString(R.string.app_name);

            String messageType = gcm.getMessageType(intent);

            if (!extras.isEmpty()) {
                if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                        .equals(messageType)) {
//				sendNotification("Send error: " + extras.toString());
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                        .equals(messageType)) {
//				sendNotification("Deleted messages on server: "
//						+ extras.toString());
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                        .equals(messageType)) {


                    JSONObject jsonObject;

                    try {

                        jsonObject = new JSONObject(message);

                        String status = jsonObject.getString("status");

                        if(status.equalsIgnoreCase("true")) {

                            String msg = jsonObject.getString("msg");
                            String header = jsonObject.getString("header");


                            final Intent notificationIntent = new Intent(this,MainActivitydrawer.class);

                            manageNotification(this, notificationIntent, titleAppName,header, msg, icon, null);

                        }


                    }catch(Exception e){


                        e.printStackTrace();


                    }

                }
            }

            GcmBroadcastReceiver.completeWakefulIntent(intent);


        }catch (Exception e){

            e.printStackTrace();

        }

    }


	/*This method shows the notificaion on device with title , description etc........*/

    public static void manageNotification(Context ctx, Intent notificationIntent,
                                          String titleAppName, String titleAdv, String descAdv, int icon, Bitmap largeIcon) {


        try {

            NOTIFICATION_ID = (int) System.currentTimeMillis();

            NotificationManager notificationManager;
            Notification myNotification;

            PendingIntent pendingIntent = PendingIntent.getActivity(ctx, NOTIFICATION_ID, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder builder = new Notification.Builder(ctx);
            builder.setContentTitle(titleAdv);
            builder.setContentText(descAdv);
            builder.setTicker(titleAppName);
            builder.setWhen(System.currentTimeMillis());
            builder.setContentIntent(pendingIntent);
            builder.setDefaults(Notification.DEFAULT_SOUND);
            builder.setAutoCancel(true);
            builder.setSmallIcon(icon);

            if (largeIcon != null) {

                builder.setLargeIcon(largeIcon);

            }

            myNotification = builder.build();


            myNotification.number = notificationCount++;

            notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(NOTIFICATION_ID, myNotification);


        } catch (Exception e) {

            e.printStackTrace();

        }

    }



}
