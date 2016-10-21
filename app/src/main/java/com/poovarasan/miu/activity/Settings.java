package com.poovarasan.miu.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseUser;
import com.poovarasan.miu.R;
import com.poovarasan.miu.databinding.ActivitySettingsBinding;

public class Settings extends AppCompatActivity {

    ActivitySettingsBinding activitySettingsBinding;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySettingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        ParseUser parseUser = ParseUser.getCurrentUser();
        String status = parseUser.getString("status");

        if (status.length() > 20)
            status = status.substring(0, 20) + "...";


        activitySettingsBinding.displayStatus.setText(status);
        activitySettingsBinding.displayName.setText("Poovarasan Vasudevan");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
