package com.poovarasan.miu.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.poovarasan.miu.R;
import com.poovarasan.miu.adapter.PagerAdapter;
import com.poovarasan.miu.databinding.ActivityHomeBinding;


public class Home extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    ActivityHomeBinding activityHomeBinding;
    int currentTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        setSupportActionBar(activityHomeBinding.toolbar);


        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), 3);
        activityHomeBinding.pager.setAdapter(adapter);

        activityHomeBinding.tabLayout.setupWithViewPager(activityHomeBinding.pager);
        activityHomeBinding.tabLayout.setOnTabSelectedListener(this);
        //
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        activityHomeBinding.pager.setCurrentItem(tab.getPosition());
        currentTab = tab.getPosition();

        invalidateOptionsMenu();
    }

    private void changeMenuItem(int position) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        menu.clear();
        if (currentTab == 0) {
            inflater.inflate(R.menu.call_menu, menu);  //  menu for photospec.
        } else if (currentTab == 1) {
            inflater.inflate(R.menu.message_menu, menu);  // menu for songspec
        } else {
            inflater.inflate(R.menu.contact_menu, menu);  // menu for songspec
        }

        return super.onPrepareOptionsMenu(menu);
    }
}
