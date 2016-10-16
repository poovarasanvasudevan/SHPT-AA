package com.poovarasan.miu.receiver;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.parse.ParsePushBroadcastReceiver;
import com.poovarasan.miu.R;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.goncalves.pugnotification.notification.PugNotification;

/**
 * Created by iampo on 10/16/2016.
 */

public class GCMReceiver extends ParsePushBroadcastReceiver {


    @Override
    protected void onPushReceive(Context context, Intent intent) {

        try {
            JSONObject message = new JSONObject(intent.getExtras().getString("com.parse.Data"));


            PugNotification.with(context)
                    .load()
                    .title("New Notification")
                    .message(message.optString("alert"))
                    .smallIcon(R.drawable.notification)
                    .largeIcon(R.drawable.notification_big)
                    .flags(Notification.DEFAULT_ALL)
                    .simple()
                    .build();


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        super.onPushDismiss(context, intent);
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
    }

}
