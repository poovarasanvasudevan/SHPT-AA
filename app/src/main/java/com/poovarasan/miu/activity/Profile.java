package com.poovarasan.miu.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.poovarasan.miu.R;
import com.poovarasan.miu.databinding.ActivityProfileBinding;

public class Profile extends AppCompatActivity {

    Toolbar toolbar;
    ActivityProfileBinding activityProfileBinding;
    boolean edit = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        String dName = ParseUser.getCurrentUser().getString("dname") == null ? "" : ParseUser.getCurrentUser().getString("dname");
        activityProfileBinding.profileName.setText(dName);
        activityProfileBinding.profileName.setSelection(dName.length());

        activityProfileBinding.profileNameEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edit == true) {
                    activityProfileBinding.profileName.setEnabled(true);
                    activityProfileBinding.profileNameEditButton.setImageResource(R.drawable.ic_check_white_24dp);
                    edit = false;
                } else {

                    ParseUser parseUser = ParseUser.getCurrentUser();
                    parseUser.put("dname", activityProfileBinding.profileName.getText().toString().trim());
                    parseUser.saveEventually(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            activityProfileBinding.profileName.setEnabled(false);
                            activityProfileBinding.profileNameEditButton.setImageResource(R.drawable.ic_edit);
                            edit = true;
                            Toast.makeText(getApplicationContext(), "Name Updated Successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
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
