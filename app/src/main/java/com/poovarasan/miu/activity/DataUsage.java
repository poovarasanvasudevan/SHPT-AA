package com.poovarasan.miu.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.TrafficStats;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mikepenz.materialize.MaterializeBuilder;
import com.poovarasan.miu.R;
import com.poovarasan.miu.databinding.ActivityDataUsageBinding;

import java.util.List;

public class DataUsage extends AppCompatActivity {

    ActivityDataUsageBinding activityDataUsageBinding;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDataUsageBinding = DataBindingUtil.setContentView(this, R.layout.activity_data_usage);

        new MaterializeBuilder().withActivity(this).build();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getUsage();
    }

    public void getUsage() {
        final PackageManager pm = getPackageManager();

        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        //final List<ActivityManager.RunningTaskInfo> recentTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
        for (int i = 0; i < appProcesses.size(); i++) {
            Log.d("Executed app", "Application executed : " + appProcesses.get(i).processName + "\t\t ID: " + appProcesses.get(i).pid + "");
            //  String packageName = activityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
            //String packageName = appProcesses.get(i)..getPackageName();
            ApplicationInfo app = null;
            try {
                app = pm.getApplicationInfo(appProcesses.get(i).processName, 0);
                if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1) {
                    //it's a system app, not interested
                } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                    //Discard this one
                    //in this case, it should be a user-installed app
                } else {
                    long tx = TrafficStats.getUidTxBytes(app.uid);
                    long rx = TrafficStats.getUidRxBytes(app.uid);

                    activityDataUsageBinding.totalUsage.setText(String.valueOf(tx) + " / " + String.valueOf(rx));
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
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
