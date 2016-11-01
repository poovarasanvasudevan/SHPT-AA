package com.poovarasan.miu.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.poovarasan.miu.R;
import com.poovarasan.miu.adapter.ImageChooseAdapter;
import com.poovarasan.miu.databinding.ActivityProfileBinding;

import java.io.File;
import java.io.FileInputStream;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

public class Profile extends AppCompatActivity {

    Toolbar toolbar;
    ActivityProfileBinding activityProfileBinding;
    boolean edit = true;
    MaterialDialog.Builder builder;
    MaterialDialog materialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        String dName = ParseUser.getCurrentUser().getString("dname") == null ? "" : ParseUser.getCurrentUser().getString("dname");
        byte[] dImage = ParseUser.getCurrentUser().getBytes("image");
        activityProfileBinding.profileName.setText(dName);
        activityProfileBinding.profileName.setSelection(dName.length());
        if (dImage == null) {
            activityProfileBinding.myProfilePic.setImageResource(R.drawable.default_image);
        } else {
            Glide.with(getApplicationContext())
                    .load(dImage)
                    .centerCrop()
                    .into(activityProfileBinding.myProfilePic);
        }


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

        activityProfileBinding.changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builder = new MaterialDialog.Builder(Profile.this);
                int image[] = new int[]{
                        R.drawable.ic_photo,
                        R.drawable.ic_camera_alt,
                        R.drawable.ic_account_circle
                };
                ImageChooseAdapter imageChooseAdapter = new ImageChooseAdapter(getApplicationContext(), R.array.changeImageMenu, image);
                imageChooseAdapter.setCallback(new ImageChooseAdapter.Callback() {
                    @Override
                    public void onItemClicked(int index) {
                        switch (index) {
                            case 0: {

                                int permissionCheck = ContextCompat.checkSelfPermission(Profile.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                    EasyImage.openChooserWithGallery(Profile.this, "Pick an Image", 0);
                                } else {
                                    Nammu.askForPermission(Profile.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
                                        @Override
                                        public void permissionGranted() {
                                            EasyImage.openChooserWithGallery(Profile.this, "Pick an Image", 0);
                                        }

                                        @Override
                                        public void permissionRefused() {

                                            Toast.makeText(getApplicationContext(), "Permission Refsued unable to pick", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }

                                break;
                            }
                            case 1: {

                                int permissionCheck = ContextCompat.checkSelfPermission(Profile.this, Manifest.permission.CAMERA);
                                int permissionCheckFile = ContextCompat.checkSelfPermission(Profile.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                if (permissionCheck == PackageManager.PERMISSION_GRANTED && permissionCheckFile == PackageManager.PERMISSION_GRANTED) {
                                    EasyImage.openCamera(Profile.this, 0);
                                } else {
                                    Nammu.askForPermission(Profile.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, new PermissionCallback() {
                                        @Override
                                        public void permissionGranted() {
                                            EasyImage.openCamera(Profile.this, 0);
                                        }

                                        @Override
                                        public void permissionRefused() {
                                            Toast.makeText(getApplicationContext(), "Permission Refsued unable to pick", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                break;
                            }

                            case 2: {

                                break;
                            }
                        }
                    }

                    @Override
                    public void onButtonClicked(int index) {

                    }
                });
                materialDialog = builder
                        .title("Choose Action")
                        .adapter(imageChooseAdapter, null)
                        .autoDismiss(true)
                        .show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagePicked(final File imageFile, EasyImage.ImageSource source, int type) {

                materialDialog.dismiss();
                ParseUser parseUser = ParseUser.getCurrentUser();
                parseUser.put("image", getBytes(imageFile));
                parseUser.saveEventually(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        activityProfileBinding.myProfilePic.setImageBitmap(null);

                        Log.i("File Uploaded ", "Done");
                        Glide.with(getApplicationContext())
                                .load(imageFile)
                                .into(activityProfileBinding.myProfilePic);


                        Log.i("Updation", "Success");
                    }
                });
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(Profile.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }


    public byte[] getBytes(File file) {
        FileInputStream input = null;
        if (file.exists()) try {
            input = new FileInputStream(file);
            int len = (int) file.length();
            byte[] data = new byte[len];
            int count, total = 0;
            while ((count = input.read(data, total, len - total)) > 0) total += count;
            return data;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) try {
                input.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
