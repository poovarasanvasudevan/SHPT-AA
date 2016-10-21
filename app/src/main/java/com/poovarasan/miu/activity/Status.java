package com.poovarasan.miu.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.poovarasan.miu.R;
import com.poovarasan.miu.databinding.ActivityStatusBinding;

import java.util.Date;

public class Status extends AppCompatActivity {

    ActivityStatusBinding activityStatusBinding;
    Toolbar toolbar;

    String status = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityStatusBinding = DataBindingUtil.setContentView(this, R.layout.activity_status);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        String status = ParseUser.getCurrentUser().getString("status");
        activityStatusBinding.myStatus.setText(status);
        activityStatusBinding.myStatus.setSelectAllOnFocus(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.status_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent i = new Intent(Status.this, Home.class);
            startActivity(i);
            finish();
        }
        if (item.getItemId() == R.id.statusUpdate) {
            status = activityStatusBinding.myStatus.getText().toString();
            if (status.length() > 0) {
                ParseUser user = ParseUser.getCurrentUser();
                user.put("status", status);
                user.saveEventually(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(), "Status Updated Succesfully", Toast.LENGTH_LONG).show();

                            ParseObject parseObject = ParseObject.create("Status");
                            parseObject.put("status", status);
                            parseObject.put("updated", new Date());
                            parseObject.put("active", true);
                            parseObject.pinInBackground();
                        }
                    }
                });


            } else {
                Toast.makeText(getApplicationContext(), "No Status to Update", Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
