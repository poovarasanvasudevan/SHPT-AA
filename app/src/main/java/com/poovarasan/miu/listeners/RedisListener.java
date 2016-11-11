package com.poovarasan.miu.listeners;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.v4.os.AsyncTaskCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.parse.ParseUser;
import com.poovarasan.miu.R;
import com.poovarasan.miu.activity.MessageActivity;
import com.poovarasan.miu.adapter.ContactAdapter;
import com.poovarasan.miu.application.App;
import com.poovarasan.miu.event.TextMessageEvent;
import com.poovarasan.miu.model.MessageModel;
import com.poovarasan.miu.model.MessageModelEntityManager;
import com.poovarasan.miu.model.RecentMessagesEntityManager;
import com.poovarasan.miu.model.UserModel;
import com.poovarasan.miu.model.UserModelEntityManager;
import com.sromku.simple.storage.Storage;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Random;

import br.com.goncalves.pugnotification.notification.PugNotification;
import br.com.goncalves.pugnotification.notification.Simple;
import redis.clients.jedis.Client;
import redis.clients.jedis.JedisPubSub;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by poovarasanv on 18/10/16.
 */

public class RedisListener extends JedisPubSub {
    Context context;
    WindowManager mWindowManager;
    View mView;


    public RedisListener(Context context) {
        this.context = context;
    }

    @Override
    public void onMessage(String channel, String message) {
        Log.i(channel, message);
        notifyMe(message, context);
        parseMessage(message);
        // showDialog(message);
    }

    private void parseMessage(String message) {
        try {
            MessageModelEntityManager messageModelEntityManager = new MessageModelEntityManager();
            RecentMessagesEntityManager recentMessagesEntityManager = new RecentMessagesEntityManager();
            JSONObject jsonObject = new JSONObject(message);
            String type = jsonObject.optString("type");
            switch (type) {

                case "MESSAGE": {
                    String from = jsonObject.optString("from");
                    long time = jsonObject.optLong("time");
                    String messageType = jsonObject.optString("MESSAGETYPE");
                    switch (messageType) {
                        case "TEXT": {

                            String tag = from + "_" + new Random(999999999) + "_" + time;
                            MessageModel messageModel = new MessageModel();
                            messageModel.setFromUser(from);
                            messageModel.setMessageTime(time);
                            messageModel.setSelf(false);
                            messageModel.setMessageType(messageType);
                            messageModel.setTag(tag);
                            messageModel.setMessage(jsonObject.optString("message"));
                            messageModelEntityManager.add(messageModel);

                            App.addToRecent(from,jsonObject.optString("message"));


                            EventBus.getDefault().post(new TextMessageEvent(
                                    jsonObject.optString("message"),
                                    from,
                                    time,
                                    tag
                            ));

                            break;
                        }
                        case "IMAGE": {
                            String imageBytes = jsonObject.optString("image");
                            byte[] encodedImage = Base64.decode(imageBytes, Base64.DEFAULT);
                            Bitmap bitmap = App.byteToBitmap(encodedImage);
                            Storage storage = App.getStorage(context);

                            storage.createDirectory("Miu/Messages/Media/" + from);

                            String PATH = "Miu/Messages/Media/" + from;
                            String NAME = "MEDIA" + new Date().getTime() + ".png";

                            storage.createFile(PATH, NAME, bitmap);
                            String filePath = App.getStorage(context).getFile(PATH, NAME).getAbsolutePath();

                            MessageModel messageModel = new MessageModel();
                            messageModel.setFromUser(from);
                            messageModel.setMessageTime(time);
                            messageModel.setSelf(false);
                            messageModel.setTag(from + "_" + new Random(999999999) + "_" + time);
                            messageModel.setMessageType(messageType);
                            messageModel.setImagePath(filePath);
                            messageModelEntityManager.add(messageModel);
                            break;
                        }
                        case "VIDEO": {

                        }
                        case "LOCATION": {

                        }
                    }
                    break;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        Log.i(channel, message);
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        Log.i(channel, subscribedChannels + "");

        AsyncTaskCompat.executeParallel(new GetMessage(), null);
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        Log.i(channel, subscribedChannels + "");
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        Log.i(pattern, subscribedChannels + "");
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        Log.i(pattern, subscribedChannels + "");
    }

    @Override
    public void onPong(String pattern) {
        Log.i("Pong", pattern + "");
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
    }

    @Override
    public void unsubscribe(String... channels) {
        super.unsubscribe(channels);
    }

    @Override
    public void subscribe(String... channels) {
        super.subscribe(channels);
    }

    @Override
    public void psubscribe(String... patterns) {
        super.psubscribe(patterns);
    }

    @Override
    public void punsubscribe() {
        super.punsubscribe();
    }

    @Override
    public void punsubscribe(String... patterns) {
        super.punsubscribe(patterns);
    }

    @Override
    public void ping() {
        super.ping();
    }

    @Override
    public boolean isSubscribed() {
        return super.isSubscribed();
    }

    @Override
    public void proceedWithPatterns(Client client, String... patterns) {
        super.proceedWithPatterns(client, patterns);
    }

    @Override
    public void proceed(Client client, String... channels) {
        super.proceed(client, channels);
    }

    @Override
    public int getSubscribedChannels() {
        return super.getSubscribedChannels();
    }

    public void notifyMe(String message, Context context) {

        UserModelEntityManager userModelEntityManager = new UserModelEntityManager();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(message);

            int userCount = userModelEntityManager.select().number().equalsTo(jsonObject.optString("from")).count();

            if (userCount > 0) {
                UserModel userModel = userModelEntityManager.select().number().equalsTo(jsonObject.optString("from")).first();


                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("contactDetails", new ContactAdapter(
                        userModel.getImage(),
                        userModel.getName(),
                        userModel.getStatus(),
                        userModel.getNumber(),
                        context
                ));
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                PugNotification.with(context)
                        .load()
                        .title(userModel.getNumber() + " sent you a message...")
                        .bigTextStyle(jsonObject.optString("message"))
                        .smallIcon(R.drawable.ic_whatsapp)
                        .largeIcon(R.drawable.miublue)
                        .flags(Notification.DEFAULT_ALL)
                        .vibrate(new long[]{1000L, 200L, 300L})
                        .button(R.drawable.ic_reply, "reply", pendingIntent)
                        .simple()
                        .build();
            } else {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("contactDetails", new ContactAdapter(
                        App.getDefaultImagePath(context),
                        jsonObject.optString("from"),
                        jsonObject.optString("from"),
                        jsonObject.optString("from"),
                        context
                ));

                PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                PugNotification.with(context)
                        .load()
                        .title(jsonObject.optString("from") + " sent you a message...")
                        .bigTextStyle(jsonObject.optString("message"))
                        .smallIcon(R.drawable.ic_whatsapp)
                        .largeIcon(R.drawable.miublue)
                        .flags(Notification.DEFAULT_ALL)
                        .vibrate(new long[]{1000L, 200L, 300L})
                        .button(R.drawable.ic_reply, "reply", pendingIntent)
                        .simple()
                        .build();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    class GetMessage extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            Log.i("Hit", "Hit");
            List<String> messageQueue = App.getRedis().lrange(ParseUser.getCurrentUser().getUsername() + ".messagequeue", 0, -1);
            Simple simple = null;
            boolean isMessageAvailable = false;
            String fullMessage = "";
            for (String message : messageQueue) {
                App.getRedis().rpop(ParseUser.getCurrentUser().getUsername() + ".messagequeue");

                isMessageAvailable = true;
                fullMessage += "\n" + message;
            }

            if (isMessageAvailable) {

                simple = PugNotification.with(context)
                        .load()
                        .title("New Messages")
                        .message(fullMessage)
                        .smallIcon(R.drawable.ic_whatsapp)
                        .largeIcon(R.drawable.miublue)
                        .flags(Notification.DEFAULT_ALL)
                        .bigTextStyle(fullMessage)
                        .vibrate(new long[]{1000L, 200L, 300L})
                        .simple();
                simple.build();

            }
            return null;
        }
    }


    private void showDialog(String aTitle) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock mWakeLock = pm.newWakeLock((PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "YourServie");
        mWakeLock.acquire();
        mWakeLock.release();

        mWindowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        mView = View.inflate(context, R.layout.activity_message, null);
        mView.setTag("MY");

        int top = context.getResources().getDisplayMetrics().heightPixels / 2;


        final WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 0,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                PixelFormat.RGBA_8888);

        mView.setVisibility(View.VISIBLE);
        mWindowManager.addView(mView, mLayoutParams);
        mWindowManager.updateViewLayout(mView, mLayoutParams);

    }

    private void hideDialog() {
        if (mView != null && mWindowManager != null) {
            mWindowManager.removeView(mView);
            mView = null;
        }
    }
}
