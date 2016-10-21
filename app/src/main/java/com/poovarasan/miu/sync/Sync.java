package com.poovarasan.miu.sync;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
                        Log.i("Contacts", contactNumber.replaceAll("[\\s()-]", "").trim());
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

                ParseQuery query = ParseQuery.getQuery("MyUsers");
                query.fromLocalDatastore();
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        ParseObject.unpinAllInBackground(objects);
                    }
                });


                Log.i("Sync Taked Place", "Sync");
                for (ParseUser parseUser : objects) {

                    Log.i("Sync Taked Place", parseUser.getUsername());
                    ParseObject users = new ParseObject("MyUsers");
                    users.put("NUMBER", parseUser.getUsername());
                    users.put("STATUS", parseUser.get("status"));
                    users.pinInBackground();
                }
            }
        });
    }
}
