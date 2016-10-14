package com.poovarasan.miu.application;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by poovarasanv on 14/10/16.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("SHPTAPP")
                .server("http://10.0.2.2:1337/parse")
                .build()
        );
        ParseInstallation
                .getCurrentInstallation()
                .saveInBackground();

    }
}
