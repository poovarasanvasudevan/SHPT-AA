package com.poovarasan.miu.application;

import android.app.Application;
import android.content.Intent;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.poovarasan.miu.service.RedisService;
import com.poovarasan.miu.sync.SyncReceiver;

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

        Intent intent1 = new Intent(this, SyncReceiver.class);
        startService(intent);
    }


    public static Jedis getRedis() {
        return new Jedis("10.0.2.2");
    }

}
