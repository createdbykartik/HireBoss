package com.hireboss.android.roachlabs;

/*
 *Author:Gurdev Singh
 *date  :9 jan 2016
 *version:1.0
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";

    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();


                smsMessageStr = smsBody;
            }
            // Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();
            if(smsMessageStr.startsWith("Hire")) {
                //this will update the UI with message
                SmsActivity inst = SmsActivity.instance();
                inst.updateList(smsMessageStr);
            }
            else{}
        }
    }
}
