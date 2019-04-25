package com.makerloom.golearn.utils

import android.graphics.Bitmap
import com.google.gson.Gson
import com.makerloom.golearn.models.WiFiDetails
import net.glxn.qrgen.android.QRCode


class QRUtils {
    companion object {
        fun getWiFiDetailsJSON (wifiDetails: WiFiDetails): String {
            val gson = Gson()
            return gson.toJson(wifiDetails, WiFiDetails::class.java)
        }

        fun getWiFiDetailsObject (wifiDetailsJson: String): WiFiDetails {
            val gson = Gson()
            return gson.fromJson(wifiDetailsJson, WiFiDetails::class.java)
        }

        fun getQRCodeBitmap (wifiDetails: WiFiDetails): Bitmap {
            val myBitmap = QRCode.from(getWiFiDetailsJSON(wifiDetails)).withSize(250, 250).bitmap()
            return myBitmap
        }

        fun getQRCodeBitmap (wifiDetailsJson: String): Bitmap {
            val myBitmap = QRCode.from(wifiDetailsJson).bitmap()
            return myBitmap
        }
    }
}