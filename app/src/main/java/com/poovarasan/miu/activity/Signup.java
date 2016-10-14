package com.poovarasan.miu.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.poovarasan.miu.R;
import com.poovarasan.miu.databinding.ActivitySignupBinding;

public class Signup extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivitySignupBinding activitySignupBinding = DataBindingUtil.setContentView(this, R.layout.activity_signup);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        activitySignupBinding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser user = new ParseUser();
                user.setUsername(activitySignupBinding.username.getText().toString());
                user.setPassword(activitySignupBinding.password.getText().toString());
                user.setEmail(activitySignupBinding.email.getText().toString());

                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(), "Signup Succes Login Now", Toast.LENGTH_LONG).show();

                            // Hooray! Let them use the app now.
                            Intent intent = new Intent(Signup.this, Login.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Sign up didn't succeed. Look at the ParseException
                            // to figure out what went wrong
                            Toast.makeText(getApplicationContext(), "Failed to Signup :" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}
