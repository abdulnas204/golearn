package com.makerloom.golearn.models

import android.text.TextUtils

data class WiFiDetails (val SSID: String, val password: String) {
    fun isValid (): Boolean {
        return !TextUtils.isEmpty(SSID) && !TextUtils.isEmpty(password)
    }
}