package com.poovarasan.miu.sync;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.poovarasan.miu.application.App;
import com.poovarasan.miu.model.UserModel;
import com.poovarasan.miu.parsemodel.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by poovarasanv on 20/10/16.
 */

public class Sync {

    Context context;

    public Sync(Context context) {
        this.context = context;
    }

    public void makeSync() {

        //Toast.makeText(context,"Job",Toast.LENGTH_SHORT).show();

        if (App.isOnline(context)) {
            ArrayList<String> alContacts = new ArrayList<String>();
            ContentResolver cr = context.getContentResolver(); //Activity/Application android.content.Context
            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                    if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            alContacts.add(contactNumber.replaceAll("[\\s()-]", "").trim());
                            break;
                        }
                        pCur.close();
                    }

                } while (cursor.moveToNext());
            }
            ParseQuery query = ParseUser.getQuery();
            query.whereContainedIn("username", alContacts);
            query.findInBackground(new FindCallback<ParseUser>() {


                @Override
                public void done(List<ParseUser> objects, ParseException e) {


                    ParseQuery query = ParseQuery.getQuery(User.CLASS);
                    query.fromLocalDatastore();
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            ParseObject.unpinAllInBackground(objects);
                        }
                    });

                    if (objects != null && objects.size() > 0) {
                        List<UserModel> allUser = new ArrayList<UserModel>();
                        for (ParseUser parseUser : objects) {
                            if (parseUser.getUsername() != ParseUser.getCurrentUser().getUsername()) {

                                ParseObject users = new ParseObject(User.CLASS);
                                users.put(User.NUMBER, parseUser.getUsername());
                                users.put(User.STATUS, parseUser.get("status"));


                                String contact[] = getContact(context, parseUser.getUsername()).split(";");
                                App.getStorage(context).getFile("Miu/Images/ProfilePic", parseUser.getUsername() + ".png").deleteOnExit();

                                if (parseUser.getBytes("image") == null) {
                                    App
                                            .getStorage(context)
                                            .createFile("Miu/Images/ProfilePic", parseUser.getUsername() + ".png", App.byteToBitmap(App.getDefaultImage(context)));

                                    File profilePic = App.getStorage(context).getFile("Miu/Images/ProfilePic", parseUser.getUsername() + ".png");

                                    users.put(User.IMAGE, profilePic.getAbsolutePath());
                                } else {

                                    Log.i("ImageChage", parseUser.getUsername());
                                    App
                                            .getStorage(context)
                                            .createFile("Miu/Images/ProfilePic", parseUser.getUsername() + ".png", App.byteToBitmap(parseUser.getBytes("image")));

                                    File profilePic = App.getStorage(context).getFile("Miu/Images/ProfilePic", parseUser.getUsername() + ".png");
                                    users.put(User.IMAGE, profilePic.getAbsolutePath());
                                }

                                users.put(User.NAME, getContactName(context, parseUser.getUsername()));
                                users.put(User.USERID,parseUser.getObjectId());
                                users.pinInBackground();
                            }
                        }
                    }
                }
            });
        }
    }

    public void syncUser(final String number) {
        if (App.isOnline(context)) {

            ParseQuery query = ParseUser.getQuery();
            query.whereEqualTo("username", number);
            query.getFirstInBackground(new GetCallback() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        ParseObject parseObject = new ParseObject(User.CLASS);
                        parseObject.put(User.STATUS, object.getString("STATUS"));
                        App.getStorage(context).getFile("Miu/Images/ProfilePic", object.getString("username") + ".png").deleteOnExit();

                        if (object.getBytes("image") == null) {
                            App
                                    .getStorage(context)
                                    .createFile("Miu/Images/ProfilePic", object.getString("username") + ".png", App.byteToBitmap(App.getDefaultImage(context)));

                            File profilePic = App.getStorage(context).getFile("Miu/Images/ProfilePic", object.getString("username") + ".png");

                            parseObject.put(User.IMAGE, profilePic.getAbsolutePath());
                        } else {

                            Log.i("ImageChage", object.getString("username"));
                            App
                                    .getStorage(context)
                                    .createFile("Miu/Images/ProfilePic", object.getString("username") + ".png", App.byteToBitmap(object.getBytes("image")));

                            File profilePic = App.getStorage(context).getFile("Miu/Images/ProfilePic", object.getString("username") + ".png");
                            parseObject.put(User.IMAGE, profilePic.getAbsolutePath());
                        }

                        parseObject.pinInBackground();
                    }
                }

                @Override
                public void done(Object o, Throwable throwable) {

                }
            });

        }
    }

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = "";
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    public static String getContact(Context context, String phoneNumber) {
        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};

        Cursor cursor =
                contentResolver.query(
                        uri,
                        projection,
                        null,
                        null,
                        null);

        String contactName = "";
        String contactId = "";
        if (cursor != null) {
            while (cursor.moveToNext()) {
                contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
                contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            cursor.close();
        }

        return contactName + ";" + contactId;
    }
}
