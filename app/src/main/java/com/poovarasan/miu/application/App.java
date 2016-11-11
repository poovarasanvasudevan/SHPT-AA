package com.poovarasan.miu.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.poovarasan.miu.R;
import com.poovarasan.miu.model.RecentMessagesEntityManager;
import com.poovarasan.miu.parsemodel.Recent;
import com.poovarasan.miu.service.RedisService;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.util.Random;

import fr.xebia.android.freezer.Freezer;
import pl.tajchert.nammu.Nammu;
import redis.clients.jedis.Jedis;

/**
 * Created by poovarasanv on 14/10/16.
 */

public class App extends Application {

    static Jedis jedis;
    static Resources resources;
    static Storage storage = null;
    static RecentMessagesEntityManager recentMessagesEntityManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
        Parse.enableLocalDatastore(this);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .enableLocalDataStore()
                .applicationId("herokuApp")
                .clientKey(null)
                .server("https://agile-cliffs-51843.herokuapp.com/parse")
                .build()
        );

        recentMessagesEntityManager = new RecentMessagesEntityManager();
        ParseInstallation
                .getCurrentInstallation()
                .saveInBackground();

        resources = getResources();

        Nammu.init(this);
        Freezer.onCreate(this);
//        EasyImage.configuration(this)
//                .setImagesFolderName("MiuImages")
//                .saveInAppExternalFilesDir()
//                .setCopyExistingPicturesToPublicLocation(true);

        //App is online

        Intent intent = new Intent(this, RedisService.class);
        startService(intent);

    }

    public static String saveImage(String number, byte[] image, Context context) {
        if (App.isOnline(context)) {
            App
                    .getStorage(context)
                    .createFile("Miu/Images/ProfilePic", number + ".png", App.byteToBitmap(image));

            File profilePic = App.getStorage(context).getFile("Miu/Images/ProfilePic", number + ".png");
            return profilePic.getAbsolutePath();
        } else {
            return App.getDefaultImagePath(context);
        }
    }

    public static void addToRecent(final String number, final String message) {
        final ParseQuery parseQuery = new ParseQuery(Recent.CLASS);
        parseQuery.whereEqualTo(Recent.NUMBER, number);
        parseQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (e == null) {
                    if (count == 0) {
                        ParseObject parseObject = new ParseObject(Recent.CLASS);
                        parseObject.put(Recent.NUMBER, number);
                        parseObject.put(Recent.MESSAGE, message);
                        parseObject.put(Recent.TIME, new Date());
                        parseObject.pinInBackground();
                    } else {
                        ParseQuery parseQuery1 = new ParseQuery(Recent.CLASS);
                        parseQuery1.whereEqualTo(Recent.NUMBER, number);
                        parseQuery1.getFirstInBackground(new GetCallback() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                object.put(Recent.TIME, new Date());
                                object.put(Recent.MESSAGE, message);
                                object.pinInBackground();
                            }

                            @Override
                            public void done(Object o, Throwable throwable) {

                            }
                        });

                    }
                }
            }
        });
    }

    public static void setOnline() {
        if (ParseUser.getCurrentUser() != null) {
            ParseUser parseUser = ParseUser.getCurrentUser();
            parseUser.put("available", true);

            Log.i("Online", "yes");
            parseUser.saveEventually();
        }
    }

    public static void setOffline() {
        if (ParseUser.getCurrentUser() != null) {
            ParseUser parseUser = ParseUser.getCurrentUser();
            parseUser.put("available", false);
            Log.i("Online", "no");
            parseUser.saveEventually();
        }
    }

    public static boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static int generateRandomColor() {
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);


        red = (red + 255) / 2;
        green = (green + 255) / 2;
        blue = (blue + 255) / 2;

        return Color.rgb(red, green, blue);

    }


    public static byte[] getDefaultImage(Context context) {

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_image);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();

        return bitmapdata;
    }

    public static String getDefaultImagePath(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_image);


        App.getStorage(context).getFile("Miu/Images/ProfilePic", "default.png").deleteOnExit();
        App.getStorage(context)
                .createFile("Miu/Images/ProfilePic", "default.png", bitmap);

        File profilePic = App.getStorage(context).getFile("Miu/Images/ProfilePic", "default.png");
        return profilePic.getAbsolutePath();
    }

    public static Storage getStorage(Context context) {

        if (storage == null) {
            if (SimpleStorage.isExternalStorageWritable()) {
                storage = SimpleStorage.getExternalStorage();

                storage.createDirectory("Miu");
                storage.createDirectory("Miu/Images/ProfilePic");
                storage.createDirectory("Miu/Images/Wallpaper");
                storage.createDirectory("Miu/Messages/Media");
                storage.createDirectory("Miu/Messages/Media/Audio");
                storage.createDirectory("Miu/Messages/Media/Image");
                storage.createDirectory("Miu/Backup");


            } else {
                storage = SimpleStorage.getInternalStorage(context);

                storage.createDirectory("Miu");
                storage.createDirectory("Miu/Images/ProfilePic");
                storage.createDirectory("Miu/Images/Wallpaper");
                storage.createDirectory("Miu/Messages/Media");
                storage.createDirectory("Miu/Messages/Media/Audio");
                storage.createDirectory("Miu/Messages/Media/Image");
                storage.createDirectory("Miu/Backup");

            }
        }

        return storage;
    }

    public static Bitmap byteToBitmap(byte[] bytes) {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(bytes);
        Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);
        return bitmap;
    }


    public Date getCurrentDateTime() {
        return new Date();
    }

    public static Jedis getRedis() {
        return new Jedis("10.0.2.2");
    }

}
