package com.poovarasan.miu.listeners;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;
import android.util.Log;

import com.parse.ParseUser;
import com.poovarasan.miu.R;
import com.poovarasan.miu.activity.ChatHeadActivity;
import com.poovarasan.miu.application.App;

import java.util.List;

import br.com.goncalves.pugnotification.notification.PugNotification;
import redis.clients.jedis.Client;
import redis.clients.jedis.JedisPubSub;

/**
 * Created by poovarasanv on 18/10/16.
 */

public class RedisListener extends JedisPubSub {
    Context context;

    public RedisListener(Context context) {
        this.context = context;
    }

    @Override
    public void onMessage(String channel, String message) {
        Log.i(channel, message);
        notifyMe(message, context);

        startActivity();
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
        PugNotification.with(context)
                .load()
                .title("New Notification")
                .message(message)
                .smallIcon(R.drawable.notification)
                .largeIcon(R.drawable.notification_big)
                .flags(Notification.DEFAULT_ALL)
                .vibrate(new long[]{1000L, 200L, 300L})
                .simple()
                .build();
    }

    class GetMessage extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            Log.i("Hit", "Hit");
            List<String> messageQueue = App.getRedis().lrange(ParseUser.getCurrentUser().getUsername() + ".messagequeue", 0, -1);
            for (String message : messageQueue) {
                App.getRedis().rpop(ParseUser.getCurrentUser().getUsername() + ".messagequeue");

                String fullMessage = "\n" + message;

                PugNotification.with(context)
                        .load()
                        .title("New Notification")
                        .message(fullMessage)
                        .smallIcon(R.drawable.notification)
                        .largeIcon(R.drawable.notification_big)
                        .flags(Notification.DEFAULT_ALL)
                        .vibrate(new long[]{1000L, 200L, 300L})
                        .simple()
                        .build();
            }
            return null;
        }
    }

    public void startActivity() {
        Intent dialogIntent = new Intent(context, ChatHeadActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(dialogIntent);

    }
}
