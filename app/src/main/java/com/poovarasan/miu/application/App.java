package com.poovarasan.miu.application;

import android.app.Application;
import android.content.Intent;

import com.parse.Parse;
import com.parse.ParseInstallation;
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

        Parse.enableLocalDatastore(this);
        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
        Parse.initialize(new Parse.Configuration.Builder(this)
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


    public static Jedis getRedis() {
        return new Jedis("10.0.2.2");
    }

}
