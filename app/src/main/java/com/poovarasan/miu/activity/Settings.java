package com.poovarasan.miu.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.parse.ParseUser;
import com.poovarasan.miu.R;
import com.poovarasan.miu.adapter.SettingMenuAdapter;
import com.poovarasan.miu.databinding.ActivitySettingsBinding;

import java.util.ArrayList;
import java.util.List;

public class Settings extends AppCompatActivity {

    ActivitySettingsBinding activitySettingsBinding;
    Toolbar toolbar;

    String[] title;
    Drawable[] icons;
    Class[] classes;

    FastItemAdapter<SettingMenuAdapter> fastAdapter;

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
        String dName = parseUser.getString("dname");

        if (status.length() > 25)
            status = status.substring(0, 25) + "...";


        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        activitySettingsBinding.settingMenuList.setLayoutManager(llm);

        activitySettingsBinding.displayStatus.setText(status);
        activitySettingsBinding.displayName.setText(dName);
        byte[] dImage = ParseUser.getCurrentUser().getBytes("image");

        if (dImage == null) {
            activitySettingsBinding.profileImage.setImageResource(R.drawable.default_image);
        } else {
            Glide.with(getApplicationContext())
                    .load(dImage)
                    .into(activitySettingsBinding.profileImage);
        }

        activitySettingsBinding.displayStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //  ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(Settings.this, view, DetailActivity.EXTRA_IMAGE);
                ActivityCompat.startActivity(Settings.this, new Intent(Settings.this, Status.class),
                        null);
            }
        });

        activitySettingsBinding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ActivityCompat.startActivity(Settings.this, new Intent(Settings.this, Profile.class),
                        null);
            }
        });

        title = new String[]{
                "Accounts",
                "Chats",
                "Notification",
                "Data Usage",
                "Contacts",
                "Storage",
                "About and Help",
        };


        icons = new Drawable[]{
                getResources().getDrawable(R.drawable.ic_account_circle),
                getResources().getDrawable(R.drawable.ic_chat),
                getResources().getDrawable(R.drawable.ic_notifications),
                getResources().getDrawable(R.drawable.ic_data_usage),
                getResources().getDrawable(R.drawable.ic_contacts),
                getResources().getDrawable(R.drawable.ic_database),
                getResources().getDrawable(R.drawable.ic_help)
        };

        classes = new Class[]{
                DataUsage.class,
                DataUsage.class,
                DataUsage.class,
                DataUsage.class,
                DataUsage.class,
                DataUsage.class,
                DataUsage.class
        };
        List<SettingMenuAdapter> settingMenuAdapters = new ArrayList<>();
        for (int i = 0; i < title.length; i++) {
            settingMenuAdapters.add(new SettingMenuAdapter(icons[i], title[i], this, classes[i]));
        }

        fastAdapter = new FastItemAdapter<>();
        activitySettingsBinding.settingMenuList.setAdapter(fastAdapter);
        fastAdapter.add(settingMenuAdapters);

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

    @Override
    protected void onPostResume() {
        super.onPostResume();
        ParseUser parseUser = ParseUser.getCurrentUser();
        String status = parseUser.getString("status");
        String dName = parseUser.getString("dname");

        if (status.length() > 25)
            status = status.substring(0, 25) + "...";

        activitySettingsBinding.displayStatus.setText(status);
        activitySettingsBinding.displayName.setText(dName);

        byte[] dImage = ParseUser.getCurrentUser().getBytes("image");

        if (dImage == null) {
            activitySettingsBinding.profileImage.setImageResource(R.drawable.default_image);
        } else {
            Glide
                    .with(getApplicationContext())
                    .load(dImage)
                    .into(activitySettingsBinding.profileImage);
        }
    }
}
