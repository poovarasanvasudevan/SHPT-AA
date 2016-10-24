package com.poovarasan.miu.application;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.poovarasan.miu.service.RedisService;

import redis.clients.jedis.Jedis;

/**
 * Created by poovarasanv on 14/10/16.
 */

public class App extends Application {

    static Jedis jedis;

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
        Parse.enableLocalDatastore(this);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .enableLocalDataStore()
                .applicationId("herokuApp")
                .clientKey(null)
                .server("https://agile-cliffs-51843.herokuapp.com/parse")
                .build()
        );
        ParseInstallation
                .getCurrentInstallation()
                .saveInBackground();

        Intent intent = new Intent(this, RedisService.class);
        startService(intent);

    }

    public static void setOnline() {
        if (ParseUser.getCurrentUser() != null) {
            ParseUser parseUser = ParseUser.getCurrentUser();
            parseUser.put("available", true);

            Log.i("Online","yes");
            parseUser.saveEventually();
        }
    }

    public static void setOffline() {
        if (ParseUser.getCurrentUser() != null) {
            ParseUser parseUser = ParseUser.getCurrentUser();
            parseUser.put("available", false);
            Log.i("Online","no");
            parseUser.saveEventually();
        }
    }


    public static Jedis getRedis() {
        return new Jedis("10.0.2.2");
    }

}
