package com.poovarasan.miu.util;

import android.content.Context;
import android.media.MediaRecorder;

import com.poovarasan.miu.application.App;

import java.io.IOException;
import java.util.Date;

/**
 * Created by poovarasanv on 3/11/16.
 */

public class RecordButtonUtil {

    MediaRecorder mRecorder;
    private String mAudioPath;
    private boolean mIsRecording;
    private boolean mIsPlaying;

    // initialize the recorder
    private void initRecorder(Context context) {
        long time = new Date().getTime();

        App.getStorage(context)
                .createFile("Miu/Messages/Media/Audio", time + "_audio.3gp", "hello");

        String path = App.getStorage(context)
                .getFile("Miu/Messages/Media/Audio", time + "_audio.3gp").getAbsolutePath();
        App.getStorage(context)
                .getFile("Miu/Messages/Media/Audio", time + "_audio.3gp").delete();

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(path);
        mIsRecording = true;
    }

    /**
     * start recording and save the file
     */
    public void recordAudio(Context context) {
        initRecorder(context);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder.start();
    }


    /**
     * stop recording
     */
    public void stopRecord() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            mIsRecording = false;
        }
    }


}