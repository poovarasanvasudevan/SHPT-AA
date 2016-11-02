package com.poovarasan.miu.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.poovarasan.miu.R;
import com.poovarasan.miu.databinding.ActivityStatusBinding;

import java.util.Date;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;

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


        EmojIconActions emojIcon = new EmojIconActions(this, activityStatusBinding.myStatus, activityStatusBinding.myStatus, activityStatusBinding.emojiBtn);
        emojIcon.ShowEmojIcon();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.status_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.statusUpdate) {
            status = activityStatusBinding.myStatus.getText().toString();
            if (status.length() > 0) {
                ParseUser user = ParseUser.getCurrentUser();
                user.put("status", status);
                user.saveEventually();

                ParseObject parseObject = ParseObject.create("Status");
                parseObject.put("status", status);
                parseObject.put("updated", new Date());
                parseObject.put("active", true);
                parseObject.pinInBackground();
                Toast.makeText(getApplicationContext(), "Status Updated Succesfully", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(getApplicationContext(), "No Status to Update", Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
