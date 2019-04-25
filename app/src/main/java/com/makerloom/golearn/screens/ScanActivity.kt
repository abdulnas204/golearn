package com.makerloom.golearn.screens

import android.Manifest
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.budiyev.android.codescanner.*
import com.github.javiersantos.bottomdialogs.BottomDialog
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.makerloom.common.activity.MyPlainToolbarActivity
import com.makerloom.golearn.R
import com.makerloom.golearn.models.WiFiDetails
import com.makerloom.golearn.utils.QRUtils
import com.thanosfisherman.wifiutils.WifiUtils
import java.util.concurrent.TimeUnit

class ScanActivity : MyPlainToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        requestCameraPermission()
    }

    private fun requestCameraPermission () {
        Log.d(TAG, "requestCameraPermission")

        Dexter.withActivity(this@ScanActivity)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(object: PermissionListener {
                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        Log.d(TAG, "onPermissionDenied")

                        AlertDialog.Builder(this@ScanActivity)
                                .setTitle("Camera Permission Denied")
                                .setMessage("This app requires permission to use your camera. The camera is used to scan and validate the WiFi QR Codes.")
                                .setPositiveButton("OK", object: DialogInterface.OnClickListener {
                                    override fun onClick(dialog: DialogInterface?, which: Int) {
                                        dialog?.dismiss()
                                    }
                                })
                                .setCancelable(false)
                                .setOnDismissListener {
                                    finish()
                                }
                                .create()
                                .show()
                    }

                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        Log.d(TAG, "onPermissionGranted")

                        initActivity()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                            permission: PermissionRequest?,
                            token: PermissionToken?
                    ) {
                        Log.d(TAG, "onPermissionRationaleShouldBeShown")

                        token?.continuePermissionRequest()
                    }
                })
                .check()
    }


    private var codeScanner: CodeScanner? = null

    private var TAG = javaClass.simpleName

    private fun initActivity () {
        Log.d(TAG, "initActivity")

        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)

        codeScanner = CodeScanner(this, scannerView)

        codeScanner?.apply {
            camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
            formats = listOf(BarcodeFormat.QR_CODE) // list of type BarcodeFormat,
            // ex. CodeScanner.ALL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
            scanMode = ScanMode.CONTINUOUS // or SINGLE or PREVIEW
            isAutoFocusEnabled = true // Whether to enable auto focus or not
            isFlashEnabled = !true // Whether to enable flash or not

            decodeCallback = DecodeCallback {
                codeScanner?.stopPreview()
                runOnUiThread {
//                    Toast.makeText(this@ScanActivity,
//                            "Scan result: ${it.text}", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Scan result: ${it.text}")
                    val isValid = isValidQRCode(it)
                    showScanFeedback(isValid, QRUtils.getWiFiDetailsObject(it.text))
                }
            }

            errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
                runOnUiThread {
                    Toast.makeText(this@ScanActivity, "Camera initialization error: ${it.message}",
                            Toast.LENGTH_LONG).show()
                }
            }

            startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner?.startPreview()
    }

    override fun onPause() {
        codeScanner?.releaseResources()
        super.onPause()
    }

    private fun isValidQRCode (result: Result) : Boolean {
        var wifiDetails: WiFiDetails

        try {
            wifiDetails = QRUtils.getWiFiDetailsObject(result.text)
        }
        catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        return wifiDetails.isValid() && (result.barcodeFormat == BarcodeFormat.QR_CODE)
    }

    private val timeoutSeconds = 20L

    private fun showScanFeedback (isAccepted: Boolean, wifiDetails: WiFiDetails) {
        val builder = BottomDialog.Builder(this@ScanActivity)
                .setCancelable(false)
                .setPositiveText("OK")
                .setPositiveBackgroundColorResource(R.color.md_blue_500)
                .setPositiveTextColorResource(R.color.md_white_1000)
                .onPositive {
                    it.dismiss()
                    if (isAccepted) {
                        var progressDialog: ProgressDialog? = null
                        // Connect To Network
                        progressDialog = ProgressDialog(this@ScanActivity)
                        progressDialog.apply {
                            setProgressStyle(ProgressDialog.STYLE_SPINNER)
                            setMessage("Connecting to GoLearn Hotspot")
                            setCancelable(false)
                            isIndeterminate = true
                            show()
                        }

                        WifiUtils.enableLog(true)
                        WifiUtils.withContext(this@ScanActivity.applicationContext)
                                .connectWith(wifiDetails.SSID, wifiDetails.password)
                                .setTimeout(TimeUnit.SECONDS.toMillis(timeoutSeconds))
                                .onConnectionResult {
                                    try {
                                        progressDialog.dismiss()
                                    }
                                    catch (e: Exception) {
                                        e.printStackTrace()
                                    }

                                    if (it) {
                                        Log.d(TAG, "Successfully Connected")
                                        Toast.makeText(this@ScanActivity,
                                                "Successfully Connected To GoLearn Hotspot",
                                                Toast.LENGTH_LONG).show()
                                        finish()
                                    }
                                    else {
                                        Log.d(TAG, "Failed To Connect")
                                        Toast.makeText(this@ScanActivity,
                                                "Failed To Connect To GoLearn Hotspot",
                                                Toast.LENGTH_LONG).show()
                                    }
                                }
                                .start()
                    }
                    else {
                        codeScanner?.startPreview()
                    }
                }

        if (isAccepted) {
            builder.apply {
                setTitle("WiFi Network Details")
                setContent("• SSID: ${wifiDetails.SSID}\n" +
                           "• Password: ${wifiDetails.password}")
            }
        }
        else {
            builder.apply {
                setTitle("Scanning Error")
                setContent("The QR Code Wasn't Accepted. Please Try Again.")
                setPositiveText("Try Again")
                setNegativeText("Cancel")
                onNegative {
                    it.dismiss()
                }
            }
        }

        builder.build().show()
    }
}
