package com.makerloom.golearn.screens

import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.github.angads25.filepicker.controller.DialogSelectionListener
import com.github.angads25.filepicker.model.DialogConfigs
import com.github.angads25.filepicker.model.DialogProperties
import com.github.angads25.filepicker.view.FilePickerDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.makerloom.common.activity.MyPlainToolbarActivity
import com.makerloom.golearn.R
import com.makerloom.golearn.models.Course
import com.makerloom.golearn.models.UniversityInfo
import com.makerloom.golearn.models.UserInfo
import com.makerloom.golearn.screens.fragments.*
import com.makerloom.golearn.utils.UserUtils
import com.ncapdevi.fragnav.FragNavController
import com.tml.sharethem.receiver.ReceiverActivity
import com.tml.sharethem.sender.SHAREthemActivity
import com.tml.sharethem.sender.SHAREthemService
import com.tml.sharethem.utils.HotspotControl
import com.tml.sharethem.utils.Utils
import com.tml.sharethem.utils.Utils.DEFAULT_PORT_OREO
import java.io.File


class HomeActivity : MyPlainToolbarActivity(),
        MediaFragment.OnFragmentInteractionListener,
        CoursesFragment.OnFragmentInteractionListener,
        DocumentsFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        TimeTableFragment.OnFragmentInteractionListener,

        FragNavController.RootFragmentListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val navigationView = findViewById<BottomNavigationView>(R.id.navigation)
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        checkUserSignedIn(savedInstanceState)
    }

    fun checkUserAndUniInfo (user: FirebaseUser, savedInstanceState: Bundle?) {
        val pDialog = buildCheckSignInProgressDialog()

        // Try to retrieve user and university information
        var userInfo: UserInfo? = null
        var userInfoDone = false

        var uniInfo: UniversityInfo? = null
        var uniInfoDone = false

        val then = Runnable {
            if (isRetrieveDoneAndSuccessful(userInfo, userInfoDone, uniInfo, uniInfoDone)) {
                try { pDialog.dismiss() }
                catch (ex: Exception) { ex.printStackTrace() }

                setupFragments(savedInstanceState)
            }
            else  {
                pDialog.hide()
                val aDialog = buildBadConnectionDialog(object: DialogCallback {
                    // Try Again
                    override fun onPositiveButtonClick() {
                        checkUserAndUniInfo(user, savedInstanceState)
                        return
                    }

                    // Continue
                    override fun onNeutralButtonClick() {
                        super.onNeutralButtonClick()
                        setupFragments(savedInstanceState)
                    }
                })
                aDialog.show()
            }
        }

        UserUtils.getUserInfo(user, object: UserUtils.UserInfoCallback {
            override fun onFailure(ex: Exception) {
                userInfoDone = true
                then.run()
            }

            override fun onSuccess(_userInfo: UserInfo?) {
                if (_userInfo != null) {
                    userInfo = _userInfo!!
                    userInfoDone = true
                    then.run()
                }
                else {
                    // Take user to fill in information
                    goToAddUserInfo()
                }
            }
        })

        UserUtils.getUniversityInfo(user, object: UserUtils.UniversityInfoCallback {
            override fun onFailure(ex: Exception) {
                uniInfoDone = false
                then.run()
            }

            override fun onSuccess(universityInfo: UniversityInfo?) {
                if (universityInfo != null) {
                    uniInfo = universityInfo!!
                    uniInfoDone = false
                    then.run()
                }
                else {
                    // Take user to fill in university information
                    goToAddUniInfo()
                }
            }
        })
    }

    fun goToAddUserInfo () {
        startActivity(Intent(this@HomeActivity, AddUserInfoActivity::class.java))
    }

    fun goToAddUniInfo () {
        startActivity(Intent(this@HomeActivity, AddUniversityInfoActivity::class.java))
    }

    fun checkUserSignedIn (savedInstanceState: Bundle?) {
        val user = FirebaseAuth.getInstance().currentUser

        // Make user to sign in if user object is null
        if (null == user) {
            goToWelcome()
            return
        }
        else {
            checkUserAndUniInfo(user, savedInstanceState)
        }
    }

    fun isRetrieveDoneAndSuccessful (userInfo: UserInfo?, userInfoDone: Boolean,
                                     uniInfo: UniversityInfo?, uniInfoDone: Boolean): Boolean {

        val userInfoGood = userInfo != null && userInfoDone
        val uniInfoGood = uniInfo != null && uniInfoDone

        return userInfoGood && uniInfoGood
    }

    fun buildCheckSignInProgressDialog (): ProgressDialog {
        val pDialog = ProgressDialog(this@HomeActivity)
        pDialog.apply {
            isIndeterminate = true
            setProgressStyle(ProgressDialog.STYLE_SPINNER)
            setCancelable(false)
            setMessage("Loading...")
            show()
        }

        return pDialog
    }

    fun buildBadConnectionDialog (dialogCallback: DialogCallback): AlertDialog {
        val aDialog = AlertDialog.Builder(this@HomeActivity)
                .setTitle("Internet Connection Error")
                .setMessage("We couldn't connect to the internet to retrieve important information. " +
                        "You can try again or continue.")
                .setPositiveButton("Try Again", DialogInterface.OnClickListener { dialog, which ->
                    dialogCallback.onPositiveButtonClick()
                    try { dialog.dismiss() }
                    catch (ex: Exception) { ex.printStackTrace() }
                })
                .setNeutralButton("Continue", DialogInterface.OnClickListener { dialog, which ->
                    dialogCallback.onNeutralButtonClick()
                    try { dialog.dismiss() }
                    catch (ex: Exception) { ex.printStackTrace() }
                })
                .setCancelable(false)
                .create()

        return aDialog
    }

    interface DialogCallback {
        fun onPositiveButtonClick () {}

        fun onNegativeButtonClick () {}

        fun onNeutralButtonClick () {}
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_courses -> {
                openCoursesFragment()
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_timetable -> {
                openTimeTableFragment()
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_settings -> {
                openSettingsFragment()
                return@OnNavigationItemSelectedListener true
            }
        }

        return@OnNavigationItemSelectedListener false
    }



    private var fragNavController = FragNavController(supportFragmentManager, R.id.fragment_root)

    override val numberOfRootFragments = 3

    fun setupFragments (savedInstanceState: Bundle?) {
        val fragments = listOf(
                CoursesFragment.newInstance(),
                TimeTableFragment.newInstance(),
                SettingsFragment.newInstance()
        )

        fragNavController.rootFragments = fragments
        // fragNavController.rootFragmentListener = this

        fragNavController.initialize(INDEX_COURSES, savedInstanceState)
    }

    override fun onBackPressed() {
        if (fragNavController.isRootFragment) {
            super.onBackPressed()
        }
        else {
            fragNavController.popFragment()
        }
    }

    fun setTitle (title: String?, subtitle: String?) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        if (title != null) {
            toolbar?.title = title

            if (!TextUtils.isEmpty(subtitle)) {
                toolbar?.subtitle = subtitle
            }
            else {
                toolbar?.subtitle = ""
            }
        }
    }

    fun setTitle (title: String?) {

        setTitle(title, null)
    }

    fun resetTitle () {
        setTitle(getString(R.string.app_name), null)
    }

    override fun getRootFragment(index: Int): Fragment {
        when (index) {
            INDEX_COURSES -> {
                setTitle(getString(R.string.title_courses))
                return CoursesFragment.newInstance()
            }

            INDEX_TIMETABLE -> {
                setTitle(getString(R.string.title_timetable))
                return TimeTableFragment.newInstance()
            }

            INDEX_SETTINGS -> {
                setTitle(getString(R.string.title_settings))
                return SettingsFragment.newInstance()
            }
        }
        throw IllegalStateException("Need to send an index that we know")
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        fragNavController.onSaveInstanceState(outState!!)
    }

    fun openDocumentsFragment (course: Course) {
        fragNavController.pushFragment(DocumentsFragment.newInstance(course.courseCode, course.name))
    }

    fun openMediaFragment () {
        fragNavController.pushFragment(MediaFragment.newInstance())
    }

    fun getMimeType(uri: Uri): String? {
        var mimeType: String? = null

        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val cr = this.getContentResolver()
            mimeType = cr.getType(uri)
        }
        else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString())
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase())
        }

        return mimeType
    }


    fun openDocument (filePath: String) {
        val uri = FileProvider.getUriForFile(this,
                getString(R.string.file_provider_authority), File(filePath))
        val mime = getMimeType(uri)

        // Toast.makeText(this, "$filePath\n\n$uri\n\n$mime", Toast.LENGTH_LONG).show()
        val openFile = Intent(Intent.ACTION_VIEW)

        openFile.setDataAndTypeAndNormalize(uri, mime)

        openFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        openFile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        try {
            startActivity(openFile)
        }
        catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Toast.makeText(this, "You currently don't have an app that can open this file. " +
                    "Please download one from the Google Play Store", Toast.LENGTH_LONG).show()
        }
    }

    fun openCoursesFragment () {
        fragNavController.switchTab(INDEX_COURSES)
    }

    fun openTimeTableFragment () {
        fragNavController.switchTab(INDEX_TIMETABLE)
    }

    fun openSettingsFragment () {
        fragNavController.switchTab(INDEX_SETTINGS)
    }

    override fun onFragmentInteraction(uri: Uri) {}

    companion object {
        val TAG = HomeActivity::class.java.simpleName

        val INDEX_COURSES = 0
        val INDEX_TIMETABLE = 1
        val INDEX_SETTINGS = 2
    }

    private var dialog: FilePickerDialog? = null

    fun sendFiles(view: View) {
        if (Utils.isShareServiceRunning(applicationContext)) {
            startActivity(Intent(applicationContext, SHAREthemActivity::class.java))
            return
        }

        val properties = DialogProperties()
        properties.selection_mode = DialogConfigs.MULTI_MODE
        properties.selection_type = DialogConfigs.FILE_SELECT
        properties.root = File(DialogConfigs.DEFAULT_DIR)
        properties.error_dir = File(DialogConfigs.DEFAULT_DIR)
        properties.extensions = null

        dialog = FilePickerDialog(this, properties)
        dialog?.apply {
            setTitle("Select files to share")

            setDialogSelectionListener(object : DialogSelectionListener {
                override fun onSelectedFilePaths(files: Array<String>?) {
                    if (null == files || files.size == 0) {
                        Toast.makeText(this@HomeActivity, "Select at least one file to start Share Mode", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val intent = Intent(applicationContext, SHAREthemActivity::class.java)
                    intent.putExtra(SHAREthemService.EXTRA_FILE_PATHS, files)

                    // PORT value is hardcoded for Oreo & above since it's not possible to set SSID with which port info can be extracted on Receiver side.
                    intent.putExtra(SHAREthemService.EXTRA_PORT, DEFAULT_PORT_OREO)

                    // Sender name can't be relayed to receiver for Oreo & above
                    intent.putExtra(SHAREthemService.EXTRA_SENDER_NAME, "GoLearn")
                    startActivity(intent)
                }
            })

            show()
        }
    }

    fun receiveFiles(view: View) {
        val hotspotControl = HotspotControl.getInstance(applicationContext)

        if (null != hotspotControl && hotspotControl.isEnabled) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Sender(Hotspot) mode is active. Please disable it to proceed with Receiver mode")
            builder.setNeutralButton("OK", DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.cancel() })
            builder.show()
            return
        }

        startActivity(Intent(applicationContext, ReceiverActivity::class.java))
    }

    //Add this method to show Dialog when the required permission has been granted to the app.
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (dialog != null) {
                        // Show dialog if the read permission has been granted.
                        dialog!!.show()
                    }
                }
                else {
                    // Permission has not been granted. Notify the user.
                    Toast.makeText(this, "Permission is Required for getting list of files", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
