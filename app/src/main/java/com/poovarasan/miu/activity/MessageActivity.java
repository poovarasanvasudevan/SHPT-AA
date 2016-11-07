package com.poovarasan.miu.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
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
import com.cocosw.bottomsheet.BottomSheet;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.adapters.HeaderAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.poovarasan.miu.R;
import com.poovarasan.miu.adapter.ContactAdapter;
import com.poovarasan.miu.adapter.MessageOtherAdapter;
import com.poovarasan.miu.adapter.MessageSelfAdapter;
import com.poovarasan.miu.application.App;
import com.poovarasan.miu.databinding.ActivityMessageBinding;
import com.poovarasan.miu.event.TextMessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.List;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;
import redis.clients.jedis.Jedis;

public class MessageActivity extends AppCompatActivity {

    ActivityMessageBinding activityMessageBinding;
    ContactAdapter contactAdapter;
    int sendType = 0;
    MaterialDialog materialDialog;
    MaterialDialog.Builder builder;

    HeaderAdapter<MessageSelfAdapter> selfFastAdapter;
    FastItemAdapter<IItem> otherFastAdapter;


    Jedis jedis;

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


        Jedis jedis = App.getRedis();
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
        activityMessageBinding.messageText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                activityMessageBinding.userMessage.smoothScrollToPosition(otherFastAdapter.getItemCount());
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

                if (sendType == 1) {

                    JSONObject jsonObject = new JSONObject();

                    try {
                        jsonObject.put("type", "MESSAGE");
                        //{'type':'MESSAGE','from':'9789197654','MESSAGETYPE':'TEXT','message':'Hello Poosan'}
                        jsonObject.put("from", ParseUser.getCurrentUser().getUsername());
                        jsonObject.put("MESSAGETYPE", "TEXT");
                        jsonObject.put("time", new Date().getTime());
                        jsonObject.put("message", activityMessageBinding.messageText.getText().toString());

                        AsyncTaskCompat.executeParallel(new SendMessageTask(), jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {

                }
                return false;
            }
        });

        selfFastAdapter = new HeaderAdapter<>();
        otherFastAdapter = new FastItemAdapter<>();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        activityMessageBinding.userMessage.setLayoutManager(llm);

        activityMessageBinding.userMessage.setAdapter(selfFastAdapter.wrap(otherFastAdapter));

        ParseQuery query = ParseQuery.getQuery("MESSAGE");
        query.fromLocalDatastore();
        query.whereEqualTo("from", contactAdapter.getNumber());


        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (ParseObject parseObject : objects) {
                    if (parseObject.getBoolean("isself") == false) {
                        otherFastAdapter.add(new MessageOtherAdapter(
                                parseObject.getString("message"),
                                parseObject.getLong("time")
                        ));
                    } else {
                        otherFastAdapter.add(new MessageSelfAdapter(
                                parseObject.getString("message"),
                                parseObject.getLong("time")
                        ));
                    }
                }

                activityMessageBinding.userMessage.smoothScrollToPosition(otherFastAdapter.getItemCount());
            }
        });


    }

    class SendMessageTask extends AsyncTask<JSONObject, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(JSONObject... jsonObjects) {
            long result = App.getRedis().publish(contactAdapter.getNumber(), jsonObjects[0].toString());
            JSONObject jsonObject = jsonObjects[0];

            ParseObject parseObject = new ParseObject("MESSAGE");
            parseObject.put("from", contactAdapter.getNumber());
            parseObject.put("time", jsonObject.optLong("time"));
            parseObject.put("messagetype", "TEXT");
            parseObject.put("isself", true);
            parseObject.put("message", jsonObject.optString("message"));
            parseObject.pinInBackground();
            if (result < 1) {
                App.getRedis().lpush(contactAdapter.getNumber() + ".messagequque", jsonObjects[0].toString());
            }
            return jsonObjects[0];
        }

        @Override
        protected void onPostExecute(JSONObject aBoolean) {

            otherFastAdapter.add(new MessageSelfAdapter(
                    aBoolean.optString("message"),
                    aBoolean.optLong("time")
            ));

            activityMessageBinding.messageText.setText("");
            activityMessageBinding.userMessage.smoothScrollToPosition(otherFastAdapter.getItemCount());
            super.onPostExecute(aBoolean);
        }
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
            sendType = 0;
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
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ParseQuery query = ParseQuery.getQuery("MESSAGE");
                                query.fromLocalDatastore();
                                query.whereEqualTo("from", contactAdapter.getNumber());
                                query.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {
                                        ParseObject.unpinAllInBackground(objects);

                                        otherFastAdapter.clear();
                                    }
                                });
                            }
                        })
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


            case R.id.attach_file: {

                new BottomSheet.Builder(this)
                        .title("Choose Attachement Type")
                        .sheet(R.menu.attachement_sheet)
                        .grid()
                        .listener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {

                                }
                            }
                        }).show();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TextMessageEvent event) {

        if (event.getNumber().equals(contactAdapter.getNumber())) {
            otherFastAdapter.add(new MessageOtherAdapter(
                    event.getMessage(),
                    event.getTime()
            ));
            activityMessageBinding.userMessage.smoothScrollToPosition(otherFastAdapter.getItemCount());
        }
    }
}
