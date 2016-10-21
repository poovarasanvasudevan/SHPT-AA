package com.poovarasan.miu.sync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by poovarasanv on 20/10/16.
 */

public class SyncService extends Service {

    private PendingIntent pendingIntent;
    private AlarmManager manager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent alarmIntent = new Intent(this, SyncReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        startSync();
        return super.onStartCommand(intent, flags, startId);
    }

    public void startSync() {
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 60000 * 60 * 24;
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
