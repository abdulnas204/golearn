package com.makerloom.common;

import com.google.firebase.firestore.FirebaseFirestore;
import com.makerloom.golearn.utils.Commons;

import androidx.multidex.MultiDexApplication;
import io.paperdb.Paper;

/**
 * Created by michael on 2/25/18.
 */

public class MyApp extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        Paper.init(getApplicationContext());

        // Allow the database work offline
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Commons.enableFirestoreOffline(db);

        // Check for new questions every time the app starts
        // QuestionUpdateUtils.Companion.checkForNewQuestions(this);

        // Subscribe to messages about questions updates
        // FirebaseMessaging.getInstance().subscribeToTopic(Commons.UPDATES_TOPIC_NAME);
    }

    public static boolean useWithoutSignIn = false;

    public static void useWithoutSignIn () {
        useWithoutSignIn = true;
    }

    public static void dontUseWithoutSignIn () {
        useWithoutSignIn = !true;
    }
}
