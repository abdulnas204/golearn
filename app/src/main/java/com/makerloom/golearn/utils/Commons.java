package com.makerloom.golearn.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.makerloom.common.MyApp;
import com.makerloom.golearn.screens.WelcomeActivity;

import androidx.appcompat.app.AppCompatActivity;

public class Commons {
    public static final int VALIDITY_MONTHS = 2;

    public static final String VALID_TILL_KEY = "validTill";
    public static final String USED_KEY = "used";
    public static final String PIN_KEY = "pin";
    public static final String UID_KEY = "uid";

    public static final String LAST_QUESTION_UPDATE_KEY = "LAST_QUESTION_UPDATE";

    public static final String IS_QUESTIONS_UPDATE_KEY = "isQuestionsUpdate";
    public static final String UPDATES_TOPIC_NAME = "questionsUpdate";

    public static final String QUESTIONS_PATH = "raw";
    public static final String QUESTIONS_PREFIX = "questions_";
    public static final String DEPARTMENTS_FILENAME = "departments";
    public static final String DATA_FILE_EXT = "json";

    public static final String PASSAGE_KEY = "passage";

    public static final String EXPIRED_PIN_KEY = "EXPIRED_PIN";

    public static final int MAX_INSTRUCTION_LENGTH = 200;

    public static final String USER_INFO_COLLECTION_NAME = "userInfo";
    public static final String UNIVERSITY_INFO_COLLECTION_NAME = "universityInfo";

    public static void enableFirestoreOffline (FirebaseFirestore db) {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }

    public static Boolean hasConnection (Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);

        if (null == cm) {
            return false;
        }

        NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi != null && wifi.isConnected()) {
            return true;
        }

        NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobile != null && mobile.isConnected()) {
            return true;
        }

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();

    }

    public static void goToWelcomeIfNotSignedIn (Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (null == user && !MyApp.useWithoutSignIn) {
            Intent welcome = new Intent(context, WelcomeActivity.class);
            welcome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(welcome);
            if (context instanceof AppCompatActivity) {
                ((AppCompatActivity) context).finish();
            }
        }
    }
}
