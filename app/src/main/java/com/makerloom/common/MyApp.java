package com.makerloom.common;

import android.support.multidex.MultiDexApplication;

import com.google.firebase.messaging.FirebaseMessaging;
import com.makerloom.golearn.utils.Commons;
import com.makerloom.golearn.utils.QuestionUpdateUtils;

import io.paperdb.Paper;

/**
 * Created by michael on 2/25/18.
 */

public class MyApp extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        Paper.init(getApplicationContext());
        // Check for new questions every time the app starts
        QuestionUpdateUtils.Companion.checkForNewQuestions(this);
        // Subscribe to messages about questions updates
        FirebaseMessaging.getInstance().subscribeToTopic(Commons.UPDATES_TOPIC_NAME);
    }
}
