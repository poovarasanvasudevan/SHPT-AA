package com.poovarasan.miu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.parse.ParseUser;
import com.poovarasan.miu.activity.Home;
import com.poovarasan.miu.activity.Login;

/**
 * @author poovarasanv
 * @see android.app.Activity
 * @see android.content.Context
 *
 * @since 1.0
 *
 * this Activity is the Starting point of the App that decides is the user logged in or not and sends to
 * Corresponding Activity
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    // do stuff with the user
                    Intent intent = new Intent(MainActivity.this, Home.class);
                    startActivity(intent);
                    finish();
                } else {
                    // show the signup or login screen
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);

        /**
         *
         * @author Poovarasan Vasudevan
         * @reason This is Meant Splash Screen Moving One Screen to Other if Logged in
         *          then it goes to Home Screen else goes to Login Screen
         * @see http://miu.com/docs/login
         *
         * ***/


    }
}
