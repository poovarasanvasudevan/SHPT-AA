package com.poovarasan.miu.task;

import android.os.AsyncTask;

import com.poovarasan.miu.application.App;

import redis.clients.jedis.Jedis;

/**
 * Created by poovarasanv on 20/10/16.
 */

public class SendMessageTask extends AsyncTask<String, Void, Boolean> {
    @Override
    protected Boolean doInBackground(String... strings) {
        Jedis jedis = App.getRedis();
        long receiver = jedis.publish(strings[0], strings[1]);
        if (receiver > 0) {
            return true;
        } else {
            jedis.rpush(strings[0] + ".messagequeue", strings[1]);
            return true;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        //message sent succesfuully
    }
}
