package com.poovarasan.miu.activity;

import android.Manifest;
import android.content.Context;
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
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.cocosw.bottomsheet.BottomSheet;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.adapters.HeaderAdapter;
import com.mikepenz.fastadapter_extensions.ActionModeHelper;
import com.mikepenz.fastadapter_extensions.UndoHelper;
import com.mikepenz.fastadapter_extensions.items.ProgressItem;
import com.mikepenz.materialize.MaterializeBuilder;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
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
import com.poovarasan.miu.model.MessageModel;
import com.poovarasan.miu.model.MessageModelEntityManager;
import com.poovarasan.miu.model.UserModelEntityManager;
import com.poovarasan.miu.parsemodel.Message;
import com.poovarasan.miu.parsemodel.User;
import com.poovarasan.miu.sync.Sync;
import com.sromku.simple.storage.Storage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import fr.xebia.android.freezer.async.Callback;
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
    UserModelEntityManager userModelEntityManager;
    MessageModelEntityManager messageModelEntityManager;


    Jedis jedis;
    private ActionModeHelper actionModeHelper;
    List<Integer> pos;
    private UndoHelper undoHelper;
    Storage storage;
    int startPosition = 0;
    int limit = 15;

    private HeaderAdapter<ProgressItem> footerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMessageBinding = DataBindingUtil.setContentView(this, R.layout.activity_message);

        userModelEntityManager = new UserModelEntityManager();
        messageModelEntityManager = new MessageModelEntityManager();
        new MaterializeBuilder().withActivity(this).build();
        // setTheme(R.style.GreenTheme);
        setSupportActionBar(activityMessageBinding.toolbar);
        pos = new ArrayList<>();
        storage = App.getStorage(getApplicationContext());

        final Intent intent = getIntent();
        contactAdapter = intent.getParcelableExtra("contactDetails");
        footerAdapter = new HeaderAdapter<>();

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

                    if (activityMessageBinding.messageText.getText().toString().trim().length() > 0) {
                        JSONObject jsonObject = new JSONObject();

                        try {
                            jsonObject.put("type", "MESSAGE");
                            jsonObject.put("from", ParseUser.getCurrentUser().getUsername());
                            jsonObject.put("MESSAGETYPE", "TEXT");
                            jsonObject.put("time", new Date().getTime());
                            jsonObject.put("message", activityMessageBinding.messageText.getText().toString().trim());

                            activityMessageBinding.messageText.setText("");
                            AsyncTaskCompat.executeParallel(new SendMessageTask(), jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                } else {

                }
                return false;
            }
        });

        selfFastAdapter = new HeaderAdapter<>();
        otherFastAdapter = new FastItemAdapter<>();
        otherFastAdapter.setHasStableIds(true);
        otherFastAdapter.withSelectable(true);
        otherFastAdapter.withMultiSelect(true);
        otherFastAdapter.withSelectOnLongClick(true);

        // activityMessageBinding.toolbar.startActionMode((android.view.ActionMode.Callback) actionBarCallBack);


        actionModeHelper = new ActionModeHelper(otherFastAdapter, R.menu.message_select, new ActionBarCallBack());
        otherFastAdapter.withOnPreClickListener(new FastAdapter.OnClickListener<IItem>() {
            @Override
            public boolean onClick(View v, IAdapter<IItem> adapter, IItem item, int position) {
                pos.add(position);
                Boolean res = actionModeHelper.onClick(item);
                return res != null ? res : false;
            }
        });

        otherFastAdapter.withOnPreLongClickListener(new FastAdapter.OnLongClickListener<IItem>() {
            @Override
            public boolean onLongClick(View v, IAdapter<IItem> adapter, IItem item, int position) {
                ActionMode actionMode = actionModeHelper.onLongClick(MessageActivity.this, position);
                if (actionMode != null) {
                    // Set CAB background color
                    pos.add(position);
                    findViewById(R.id.action_mode_bar).setBackgroundColor(getResources().getColor(R.color.colorPrimary));


                } else {
                    v.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
                return actionMode != null;

            }
        });

        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setStackFromEnd(true);
        activityMessageBinding.userMessage.setLayoutManager(llm);

        activityMessageBinding.userMessage.setAdapter(otherFastAdapter);


        loadNext(true);
        activityMessageBinding.userMessage.smoothScrollToPosition(otherFastAdapter.getItemCount());

        activityMessageBinding.messageText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                activityMessageBinding.userMessage.smoothScrollToPosition(otherFastAdapter.getItemCount());
            }
        });


        activityMessageBinding.userMessage.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                // Log.i(dx + "-->", dy + "-->");
                if (llm.findFirstCompletelyVisibleItemPosition() < 1) {
                    loadNext(false);
                }
                Log.i(llm.findFirstCompletelyVisibleItemPosition() + "-->", llm.findLastCompletelyVisibleItemPosition() + "-->");
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        if (App.isOnline(getApplicationContext())) {
            AsyncTaskCompat.executeParallel(new SyncSingleUser(), null);
        }
    }

    class SyncSingleUser extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Sync sync = new Sync(getApplicationContext());
            sync.syncUser(contactAdapter.getNumber());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            ParseQuery parseQuery = new ParseQuery(User.CLASS);
            parseQuery.whereEqualTo(User.NUMBER, contactAdapter.getNumber());
            parseQuery.getFirstInBackground(new GetCallback() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    activityMessageBinding.toolBarSubtitle.setText("Online");
                    Glide.with(getApplicationContext())
                            .load(new File(object.getString(User.IMAGE)))
                            .into(activityMessageBinding.toolbarProfileImage);
                }

                @Override
                public void done(Object o, Throwable throwable) {

                }
            });
        }
    }


    public void loadNext(final boolean startMessage) {


        final ParseQuery parseObject = new ParseQuery(Message.CLASS);
        parseObject.fromLocalDatastore();
        parseObject.whereEqualTo(Message.FROM, contactAdapter.getNumber());
        parseObject.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (count > startPosition) {
                    ParseQuery parseQuery = new ParseQuery(Message.CLASS);
                    parseQuery.fromLocalDatastore();
                    parseQuery.whereEqualTo(Message.FROM, contactAdapter.getNumber());
                    parseObject.addDescendingOrder(Message.TIME);
                    parseObject.setSkip(startPosition);
                    parseObject.setLimit(limit);
                    parseObject.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            for (ParseObject parseObject1 : objects) {
                                if (parseObject1.getBoolean(Message.ISSELF) == false) {

                                    if (startMessage == false) {

                                        otherFastAdapter.add(0, new MessageOtherAdapter(
                                                parseObject1.getString(Message.MESSAGE),
                                                parseObject1.getDate(Message.TIME).getTime()
                                        ).withTag(parseObject1.getString(Message.UNIQUEID)));
                                    } else {

                                        otherFastAdapter.add(0, new MessageOtherAdapter(
                                                parseObject1.getString(Message.MESSAGE),
                                                parseObject1.getDate(Message.TIME).getTime()
                                        ).withTag(parseObject1.getString(Message.UNIQUEID)));
                                    }
                                } else {

                                    if (startMessage == false) {

                                        otherFastAdapter.add(0, new MessageSelfAdapter(
                                                parseObject1.getString(Message.MESSAGE),
                                                parseObject1.getDate(Message.TIME).getTime()
                                        ).withTag(parseObject1.getString(Message.UNIQUEID)));
                                    } else {

                                        otherFastAdapter.add(0, new MessageSelfAdapter(
                                                parseObject1.getString(Message.MESSAGE),
                                                parseObject1.getDate(Message.TIME).getTime()
                                        ).withTag(parseObject1.getString(Message.UNIQUEID)));
                                    }
                                }
                            }
                            startPosition += limit;
                        }
                    });
                }
            }
        });
    }

    private void setClipboard(Context context, String text) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    private class ActionBarCallBack implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            if (item.getItemId() == R.id.msgDelete) {

                Set<Integer> selected = otherFastAdapter.getSelections();
                List<Integer> integers = new ArrayList<>();
                for (final Integer integer : otherFastAdapter.getSelections()) {
                    integers.add(integer);
                }

                Collections.sort(integers, new Comparator<Integer>() {
                    @Override
                    public int compare(Integer integer, Integer t1) {
                        return t1.compareTo(integer);
                    }
                });

                for (final Integer integer : integers) {
                    MessageModel messageModel = messageModelEntityManager.select().tag().equalsTo((String) otherFastAdapter.getItem(integer).getTag()).first();
                    if (messageModel != null) {
                        messageModelEntityManager.delete(messageModel);
                        otherFastAdapter.remove(integer);
                    }
                }


                Toast
                        .makeText(getApplicationContext(), otherFastAdapter.getSelections().size() + " Message Deleted", Toast.LENGTH_LONG)
                        .show();
            }

            if (item.getItemId() == R.id.msgCopy) {
                Set<IItem> items = otherFastAdapter.getSelectedItems();

                String clipboard = "";
                for (IItem iItem : items) {
                    if (iItem instanceof MessageSelfAdapter) {
                        MessageSelfAdapter messageSelfAdapter1 = (MessageSelfAdapter) iItem;
                        clipboard += "\n" + messageSelfAdapter1.getMessage();
                    } else if (iItem instanceof MessageOtherAdapter) {
                        MessageOtherAdapter messageSelfAdapter2 = (MessageOtherAdapter) iItem;
                        clipboard += "\n" + messageSelfAdapter2.getMessage();
                    }
                }

                setClipboard(getApplicationContext(), clipboard);

                Toast
                        .makeText(getApplicationContext(), "Message Copied Succesfully..", Toast.LENGTH_LONG)
                        .show();
            }
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }
    }

    class SendMessageTask extends AsyncTask<JSONObject, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(JSONObject... jsonObjects) {
            long result = App.getRedis().publish(contactAdapter.getNumber(), jsonObjects[0].toString());
            JSONObject jsonObject = jsonObjects[0];

            MessageModel messageModel = new MessageModel();
            messageModel.setFromUser(contactAdapter.getNumber());
            messageModel.setMessageTime(jsonObject.optLong("time"));
            messageModel.setMessageType("TEXT");
            messageModel.setSelf(true);
            messageModel.setTag(contactAdapter.getNumber() + "_" + new Random(999999999) + "_" + jsonObject.optLong("time"));
            messageModel.setMessage(jsonObject.optString("message"));
            messageModelEntityManager.add(messageModel);

            App.addToRecent(contactAdapter.getNumber(), jsonObject.optString("message"));

            Log.i("Time", jsonObject.optLong("time") + "");
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
    protected void onPostResume() {

        otherFastAdapter.clear();
        loadNext(true);

        super.onPostResume();
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


                                final MaterialDialog materialDialog1 = new MaterialDialog.Builder(MessageActivity.this)
                                        .title("Clear Chat")
                                        .content("Deleting All Messaged...")
                                        .progress(true, 0)
                                        .show();
                                messageModelEntityManager.select().fromUser().equalsTo(contactAdapter.getNumber()).async(new Callback<List<MessageModel>>() {
                                    @Override
                                    public void onSuccess(List<MessageModel> data) {

                                        messageModelEntityManager.delete(data);
                                        otherFastAdapter.clear();
                                        materialDialog1.dismiss();
                                    }

                                    @Override
                                    public void onError(List<MessageModel> data) {
                                        messageModelEntityManager.delete(data);
                                        otherFastAdapter.clear();
                                        materialDialog1.dismiss();
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

            case R.id.viewcontact: {

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
            ).withTag(event.getTag()));
            activityMessageBinding.userMessage.smoothScrollToPosition(otherFastAdapter.getItemCount());
        }
    }


}
