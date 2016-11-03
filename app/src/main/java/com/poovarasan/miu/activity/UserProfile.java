package com.poovarasan.miu.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.poovarasan.miu.R;
import com.poovarasan.miu.adapter.ContactAdapter;
import com.poovarasan.miu.databinding.ActivityUserProfileBinding;

import java.io.File;

public class UserProfile extends AppCompatActivity {

    ActivityUserProfileBinding activityUserProfileBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUserProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile);


        setSupportActionBar(activityUserProfileBinding.MyToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        activityUserProfileBinding.collapseToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.md_white_1000));
        ContactAdapter contactAdapter = getIntent().getParcelableExtra("contactDetail");
        activityUserProfileBinding.collapseToolbar.setTitle(contactAdapter.getName());
        Glide.with(this)
                .load(new File(contactAdapter.getImage()))
                .into(activityUserProfileBinding.bgheader);

        activityUserProfileBinding.MyToolbar.setSubtitle("Online");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


}
