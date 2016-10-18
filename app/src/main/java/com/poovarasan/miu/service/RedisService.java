package com.poovarasan.miu.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.poovarasan.miu.application.App;
import com.poovarasan.miu.listeners.RedisListener;

/**
 * Created by poovarasanv on 18/10/16.
 */

public class RedisService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new SubScribeTask().execute();
        return START_STICKY;
    }

    class SubScribeTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            App.getRedis().subscribe(new RedisListener(), "mychannel");
            return null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
