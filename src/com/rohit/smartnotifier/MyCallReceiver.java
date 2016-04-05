package com.rohit.smartnotifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Created by Rohit on 5/7/2015.
 */
public class MyCallReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String incomingNumber = null;
        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            // This code will execute when the phone has an incoming call

            // get the phone number
            incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Toast.makeText(context, "Call from:" + incomingNumber, Toast.LENGTH_LONG).show();
            Toast.makeText(context, "Call from: number" + incomingNumber, Toast.LENGTH_LONG).show();

        } else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
                TelephonyManager.EXTRA_STATE_IDLE)
                || intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
                TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            // This code will execute when the call is disconnected
//         SmsManager smsManager = SmsManager.getDefault();
//         smsManager.sendTextMessage( this.incomingNumber, null, "write the message to send", null, null);
//         if(this.incomingNumber!=null)
//      Toast.makeText(context,"this.incomingNumber"+this.incomingNumber,Toast.LENGTH_LONG).show();
//      else
//       Toast.makeText(context,"this.incomingNumber",Toast.LENGTH_LONG).show();
            Toast.makeText(context, "Call from: number" + incomingNumber, Toast.LENGTH_LONG).show();
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage( incomingNumber, null, "Test Message", null, null);
                Toast.makeText(context, "SMS Sent!",
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
//      Toast.makeText(context,
//       "SMS faild, please try again later!",
//       Toast.LENGTH_LONG).show();
                Toast.makeText(context,
                        "Exception-->"+e.toString(),Toast.LENGTH_LONG).show();
//      if(this.incomingNumber!=null)
//      Toast.makeText(context,"this.incomingNumber"+this.incomingNumber,Toast.LENGTH_LONG).show();
//      else
//       Toast.makeText(context,"this.incomingNumber",Toast.LENGTH_LONG).show();
                e.printStackTrace();

            }

            Toast.makeText(context, "Detected call hangup event", Toast.LENGTH_LONG).show();

        }
    }
}