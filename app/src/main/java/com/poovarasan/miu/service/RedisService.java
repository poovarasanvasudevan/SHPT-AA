package com.poovarasan.miu.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.poovarasan.miu.application.App;
import com.poovarasan.miu.listeners.RedisListener;

/**
 * Created by poovarasanv on 18/10/16.
 */

public class RedisService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("Connection","Hit");
        new SubScribeTask().execute();
        return START_STICKY;
    }

    class SubScribeTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (isOnline(getApplicationContext())) {
                App.getRedis().subscribe(new RedisListener(getApplicationContext()), "mychannel");
            }
            return null;
        }
    }

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
