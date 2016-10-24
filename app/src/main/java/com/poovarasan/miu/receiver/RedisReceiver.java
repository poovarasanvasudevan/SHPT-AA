package com.poovarasan.miu.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.poovarasan.miu.service.RedisService;

/**
 * Created by poovarasanv on 18/10/16.
 */

public class RedisReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, RedisService.class);
        context.startService(i);
    }
}
