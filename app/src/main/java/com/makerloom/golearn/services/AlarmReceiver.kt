package com.makerloom.golearn.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.makerloom.golearn.utils.QuestionUpdateUtils

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        QuestionUpdateUtils.checkForNewQuestions(context)
    }
}
