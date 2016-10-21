package com.poovarasan.miu.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by poovarasanv on 20/10/16.
 */

public class SyncReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Sync sync = new Sync(context);
        sync.makeSync();
    }
}
