package com.poovarasan.miu.sync;

import com.parse.ParseUser;
import com.poovarasan.miu.wrapper.location.DeviceLocation;
import com.poovarasan.miu.wrapper.location.LocationInfo;

import org.json.JSONException;
import org.json.JSONObject;

import me.tatarka.support.job.JobParameters;
import me.tatarka.support.job.JobService;

/**
 * Created by poovarasanv on 2/11/16.
 */

public class LocationSyncService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {

        LocationInfo locationInfo = new LocationInfo(this);
        DeviceLocation location = locationInfo.getLocation();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lat", location.getLatitude());
            jsonObject.put("lng", location.getLongitude());
            jsonObject.put("address", location.getAddressLine1());
            jsonObject.put("city", location.getCity());
            jsonObject.put("state", location.getState());
            jsonObject.put("pin", location.getPostalCode());
            jsonObject.put("countrycode", location.getCountryCode());

            ParseUser parseUser = ParseUser.getCurrentUser();
            parseUser.put("location", jsonObject);
            parseUser.saveEventually();

        } catch (JSONException e) {
            e.printStackTrace();
        }


        jobFinished(params, false);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
