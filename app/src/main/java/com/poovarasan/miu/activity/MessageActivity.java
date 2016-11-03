package com.poovarasan.miu.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.poovarasan.miu.R;
import com.poovarasan.miu.adapter.ContactAdapter;
import com.poovarasan.miu.databinding.ActivityMessageBinding;

import java.io.File;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

public class MessageActivity extends AppCompatActivity {

    ActivityMessageBinding activityMessageBinding;
    ContactAdapter contactAdapter;
    int sendType = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMessageBinding = DataBindingUtil.setContentView(this, R.layout.activity_message);

        setSupportActionBar(activityMessageBinding.toolbar);

        final Intent intent = getIntent();
        contactAdapter = intent.getParcelableExtra("contactDetails");

        Glide.with(this)
                .load(new File(contactAdapter.getImage()))
                .into(activityMessageBinding.toolbarProfileImage);

        activityMessageBinding.toolBarTitle.setText(contactAdapter.getName());
        activityMessageBinding.toolBarSubtitle.setText("Online Now");


        activityMessageBinding.toolBarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        mikeModification(0);


        EmojIconActions emojIcon = new EmojIconActions(this, activityMessageBinding.messageText, activityMessageBinding.messageText, activityMessageBinding.emojiButton);
        emojIcon.ShowEmojIcon();

        activityMessageBinding.messageText.setTextSize(18);
        activityMessageBinding.messageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mikeModification(charSequence.length());
            }

            @Override
            public void afterTextChanged(Editable editable) {

                mikeModification(editable.length());
            }
        });

        activityMessageBinding.cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.CAMERA);
                int permissionCheckFile = ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED && permissionCheckFile == PackageManager.PERMISSION_GRANTED) {
                    EasyImage.openCamera(MessageActivity.this, 0);
                } else {
                    Nammu.askForPermission(MessageActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, new PermissionCallback() {
                        @Override
                        public void permissionGranted() {
                            EasyImage.openCamera(MessageActivity.this, 0);
                        }

                        @Override
                        public void permissionRefused() {
                            Toast.makeText(getApplicationContext(), R.string.permission_refeused, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        activityMessageBinding.profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MessageActivity.this, UserProfile.class);
                intent1.putExtra("contactDetail", contactAdapter);
                ActivityCompat.startActivity(MessageActivity.this, intent1, null);
            }
        });

        activityMessageBinding.rippleSendBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return false;
            }
        });


    }


    public static int dp(float value) {
        return (int) Math.ceil(1 * value);
    }

    void mikeModification(int count) {
        if (count > 0) {
            activityMessageBinding.sendBtn.setImageResource(R.drawable.ic_action_send_now);
            sendType = 1;
        } else {
            activityMessageBinding.sendBtn.setImageResource(R.drawable.ic_mic);
        }
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

                if (source == EasyImage.ImageSource.GALLERY) {

                } else if (source == EasyImage.ImageSource.CAMERA) {

                } else if (source == EasyImage.ImageSource.DOCUMENTS) {

                }
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(MessageActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.message_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.muteUser: {

                new MaterialDialog.Builder(this)
                        .title(R.string.mute_user_title)
                        .items(R.array.muteArray)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                return true;
                            }
                        })
                        .positiveText(R.string.mute_button_text)
                        .show();
                break;
            }

            case R.id.clearChat: {

                new MaterialDialog
                        .Builder(this)
                        .title(R.string.clear_chat_heading)
                        .content(getString(R.string.clear_char_content) + contactAdapter.getName() + " in Miu")
                        .positiveText(R.string.yes_button)
                        .negativeText(R.string.no_button)
                        .checkBoxPromptRes(R.string.clear_string, false, null)
                        .show();

                break;
            }

            case R.id.emailChat: {

                new MaterialDialog.Builder(this)
                        .title(R.string.email_attachememt_heading)
                        .content(R.string.email_attachement_text)
                        .positiveText(R.string.email_attachememt_dont_attach)
                        .negativeText(R.string.email_attachememt_attach)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            }
                        })
                        .show();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
