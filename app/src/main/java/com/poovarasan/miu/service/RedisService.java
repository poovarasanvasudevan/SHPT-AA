package com.poovarasan.miu.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.poovarasan.miu.R;
import com.poovarasan.miu.application.App;
import com.poovarasan.miu.listeners.RedisListener;

import java.util.List;

import br.com.goncalves.pugnotification.notification.PugNotification;
import br.com.goncalves.pugnotification.notification.Simple;

/**
 * Created by poovarasanv on 18/10/16.
 */

public class RedisService extends Service {


    int USERCOUNT = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("Connection", "Hit");
        new SubScribeTask().execute();
        return START_STICKY;
    }


    class SubScribeTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (App.isOnline(getApplicationContext()) && ParseUser.getCurrentUser() != null) {
                App.getRedis().subscribe(new RedisListener(getApplicationContext()), ParseUser.getCurrentUser().getUsername());

                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.countInBackground(new CountCallback() {
                    public void done(int count, ParseException e) {
                        if (e == null) {
                            USERCOUNT = count;
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("VERSION");
                            query.fromLocalDatastore();
                            query.orderByDescending("version");
                            query.getFirstInBackground(new GetCallback<ParseObject>() {
                                public void done(ParseObject object, ParseException e) {
                                    if (object != null && object.getInt("version") != USERCOUNT) {
                                        ParseObject parseObject = new ParseObject("VERSION");
                                        parseObject.put("version", USERCOUNT);
                                        parseObject.pinInBackground();
                                    }
                                }
                            });

                        }
                    }
                });


            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new GetMessage().execute();
            super.onPostExecute(aVoid);
        }
    }

    class GetMessage extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            if (ParseUser.getCurrentUser() != null) {

                if(App.isOnline(getApplicationContext())) {
                    Log.i("Hit", "Hit");
                    Simple simple = null;
                    List<String> messageQueue = App.getRedis().lrange(ParseUser.getCurrentUser().getUsername() + ".messagequeue", 0, -1);
                    for (String message : messageQueue) {
                        App.getRedis().rpop(ParseUser.getCurrentUser().getUsername() + ".messagequeue");

                        String fullMessage = "\n" + message;

                        simple = PugNotification.with(getApplicationContext())
                                .load()
                                .title("New Notification")
                                .message(fullMessage)
                                .smallIcon(R.drawable.notification)
                                .largeIcon(R.drawable.notification_big)
                                .flags(Notification.DEFAULT_ALL)
                                .vibrate(new long[]{1000L, 200L, 300L})
                                .simple();
                    }
                    if (simple != null)
                        simple.build();
                }
            }
            return null;
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
