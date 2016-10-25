package com.poovarasan.miu.sync;

import me.tatarka.support.job.JobParameters;
import me.tatarka.support.job.JobService;

/**
 * Created by poovarasanv on 24/10/16.
 */

public class ContactSyncService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {

        Sync sync = new Sync(getApplicationContext());
        sync.makeSync();

        // Toast.makeText(getApplicationContext(), "Sync", Toast.LENGTH_LONG).show();
        jobFinished(params, false);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }


}
