package com.poovarasan.miu.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.poovarasan.miu.R;
import com.poovarasan.miu.adapter.PagerAdapter;
import com.poovarasan.miu.application.App;
import com.poovarasan.miu.databinding.ActivityHomeBinding;
import com.poovarasan.miu.sync.Sync;

import java.util.List;


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
        activityHomeBinding.pager.setCurrentItem(1);
        //


    }

    class QueueMessage extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {


            Log.i("Messages", "xxxxx");
            long v = App.getRedis().rpush("mychannelq", "Hello");
            Log.i("queuemessage", v + "");
            return null;
        }
    }

    class QPopMessage extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Void... voids) {

            return null;
        }

        @Override
        protected void onPostExecute(List<String> strings) {

            super.onPostExecute(strings);
        }
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        activityHomeBinding.pager.setCurrentItem(tab.getPosition());
        currentTab = tab.getPosition();
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

        switch (item.getItemId()) {
            case R.id.status: {
                Intent intent = new Intent(Home.this, Status.class);
                startActivity(intent);
                finish();
                break;
            }

            case R.id.newBroadcast: {
                AsyncTaskCompat.executeParallel(new QPopMessage(), null);
                break;
            }

            case R.id.newGroup: {
                new QueueMessage().execute();
                break;
            }

            case R.id.refresh: {

                AsyncTaskCompat.executeParallel(new RefreshUser(), null);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    class RefreshUser extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            Sync sync = new Sync(getApplicationContext());
            sync.makeSync();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            ParseQuery query = ParseQuery.getQuery("MyUsers");
            query.fromLocalDatastore();
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    for (ParseObject parseObject : objects) {
                        Log.i("Parse Sync Poosan", parseObject.getString("NUMBER"));
                    }
                }
            });

            super.onPostExecute(aVoid);
        }
    }

}
