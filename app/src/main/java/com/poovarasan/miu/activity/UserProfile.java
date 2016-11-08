package com.poovarasan.miu.activity;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.cocosw.bottomsheet.BottomSheet;
import com.poovarasan.miu.R;
import com.poovarasan.miu.adapter.ContactAdapter;
import com.poovarasan.miu.databinding.ActivityUserProfileBinding;
import com.poovarasan.miu.widget.CircularDrawable;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class UserProfile extends AppCompatActivity {

    ActivityUserProfileBinding activityUserProfileBinding;

    ContactAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUserProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile);



        setSupportActionBar(activityUserProfileBinding.MyToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        activityUserProfileBinding.collapseToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.md_white_1000));
        contactAdapter = getIntent().getParcelableExtra("contactDetail");
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

            case R.id.profileMessage: {

                AsyncTaskCompat.executeParallel(new UserSheetLoader(), null);

                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    class UserSheetLoader extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap bmp = null;
            try {
                bmp = Glide.with(getApplicationContext())
                        .load(contactAdapter.getImage())
                        .asBitmap()
                        .error(R.drawable.default_image)
                        .placeholder(R.drawable.default_image)
                        .into(100, 100)
                        .get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return bmp;

        }

        @Override
        protected void onPostExecute(Bitmap aVoid) {
            CircularDrawable circularDrawable = new CircularDrawable();
            circularDrawable.setBitmapOrTextOrIcon(aVoid);

            new BottomSheet.Builder(UserProfile.this)
                    .icon(circularDrawable)
                    .title("To " + contactAdapter.getName())
                    .sheet(R.menu.user_profile_sheet)
                    .listener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {

                            }
                        }
                    }).show();
            super.onPostExecute(aVoid);
        }
    }

}
