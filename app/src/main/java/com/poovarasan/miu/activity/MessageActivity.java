package com.poovarasan.miu.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.poovarasan.miu.R;
import com.poovarasan.miu.databinding.ActivityMessageBinding;

public class MessageActivity extends AppCompatActivity {

    ActivityMessageBinding activityMessageBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMessageBinding = DataBindingUtil.setContentView(this, R.layout.activity_message);

        setSupportActionBar(activityMessageBinding.toolbar);

        activityMessageBinding.toolBarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
