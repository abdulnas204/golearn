package com.makerloom.golearn.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import im.delight.android.location.SimpleLocation
import java.util.concurrent.TimeUnit

class LocationUtils {
    companion object {
        val TAG = LocationUtils::class.java.simpleName

        fun checkLocationEnabled (context: Context, isCheckingLocation: Boolean, checkLocationEnabledCallback: CheckLocationEnabledCallback?): Boolean {
            val location = SimpleLocation(context)
            if (!isCheckingLocation) {
                if (!location.hasLocationEnabled()) {
                    val dialog = AlertDialog.Builder(context)
                            .setTitle("Enable Location Services")
                            .setMessage("Location Services are required to use the file sharing feature. Please enable location services through Settings.")
                            .setPositiveButton("Enable", DialogInterface.OnClickListener { dialog, which ->
                                dialog.dismiss()
                                SimpleLocation.openSettings(context)
                                if (context is AppCompatActivity) {
                                    context.finish()
                                    // restartActivity(context)
                                }
                                Handler().postDelayed(Runnable {
                                    if (context is AppCompatActivity) {
                                        context.runOnUiThread {
                                            Toast.makeText(context, "Tap 'Send/Receive Material' after enabling Location Services",
                                                    Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }, TimeUnit.SECONDS.toMillis(1))
                                checkLocationEnabledCallback?.onEnabled()
                            })
                            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                                dialog.dismiss()

                                if (context is AppCompatActivity) {
                                    context.finish()
                                }
                                Toast.makeText(context, "Location Services are required to use the file sharing feature",
                                        Toast.LENGTH_LONG).show()
                                checkLocationEnabledCallback?.onDisabled()
                            })
                            .create()
                    dialog.show()
                } else {
                    checkLocationEnabledCallback?.isAlreadyEnabled()
                }
            }

            return !location.hasLocationEnabled()
        }

        fun restartActivity (context: Context) {
            if (context is AppCompatActivity) {
                context.apply {
                    val intent = intent
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    finish()
                    startActivity(intent)
                }
            }
        }
    }

    interface CheckLocationEnabledCallback {
        fun onEnabled () {}

        fun onDisabled () {}

        fun isAlreadyEnabled ()
    }
}