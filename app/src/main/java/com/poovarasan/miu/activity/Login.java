package com.poovarasan.miu.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.materialize.MaterializeBuilder;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.poovarasan.miu.R;
import com.poovarasan.miu.databinding.ActivityLoginBinding;
import com.poovarasan.miu.service.RedisService;
import com.poovarasan.miu.sync.Sync;

import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

public class Login extends AppCompatActivity {

    Toolbar toolbar;
    MaterialDialog materialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityLoginBinding activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        new MaterializeBuilder().withActivity(this).build();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Nammu.init(this);

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

                            int permissionCheckFile = ContextCompat.checkSelfPermission(Login.this, Manifest.permission.READ_CONTACTS);
                            int permissionCheck = ContextCompat.checkSelfPermission(Login.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            if (permissionCheckFile == PackageManager.PERMISSION_GRANTED && permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                new SyncLoader().execute();
                            } else {
                                Nammu.askForPermission(Login.this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionCallback() {
                                    @Override
                                    public void permissionGranted() {
                                        new SyncLoader().execute();
                                    }

                                    @Override
                                    public void permissionRefused() {
                                        Toast.makeText(getApplicationContext(), R.string.permission_refeused, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }


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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
