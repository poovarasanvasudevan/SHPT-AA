package com.poovarasan.miu.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.poovarasan.miu.R;
import com.poovarasan.miu.databinding.ActivityLoginBinding;
import com.poovarasan.miu.service.RedisService;
import com.poovarasan.miu.sync.Sync;

public class Login extends AppCompatActivity {

    Toolbar toolbar;
    MaterialDialog materialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityLoginBinding activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        activityLoginBinding.gosignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, Signup.class);
                startActivity(i);
            }
        });

        activityLoginBinding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ParseUser.logInInBackground(activityLoginBinding.username.getText().toString(), activityLoginBinding.password.getText().toString(), new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {

                            materialDialog = new MaterialDialog.Builder(Login.this)
                                    .title("Login Success")
                                    .content("Sync Contacts...")
                                    .progress(true, 0)
                                    .show();

                            new SyncLoader().execute();

                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid Username or Password", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    class SyncLoader extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            Sync sync = new Sync(getApplicationContext());
            sync.makeSync();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            materialDialog.dismiss();
            Intent intent = new Intent(Login.this, RedisService.class);
            startService(intent);

            Intent i = new Intent(Login.this, Home.class);
            startActivity(i);
            finish();

            super.onPostExecute(aVoid);
        }
    }
}
