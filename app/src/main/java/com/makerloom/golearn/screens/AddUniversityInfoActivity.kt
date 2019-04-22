package com.makerloom.golearn.screens

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import com.isapanah.awesomespinner.AwesomeSpinner
import com.makerloom.common.activity.MyPlainToolbarActivity
import com.makerloom.golearn.R
import com.makerloom.golearn.models.UniversityInfo
import com.makerloom.golearn.utils.Commons
import com.makerloom.golearn.utils.NetworkUtils
import com.makerloom.golearn.utils.UserUtils
import mehdi.sakout.fancybuttons.FancyButton

class AddUniversityInfoActivity : MyPlainToolbarActivity(), SubmitActivity {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_university_info)

        Commons.goToWelcomeIfNotSignedIn(this@AddUniversityInfoActivity)

        val uniSpinner: AwesomeSpinner = findViewById(R.id.uni_spinner)
        val deptSpinner: AwesomeSpinner = findViewById(R.id.dept_spinner)
        val levelSpinner: AwesomeSpinner = findViewById(R.id.level_spinner)

        val uniAdapter = ArrayAdapter.createFromResource(this, R.array.uni_array,
                android.R.layout.simple_spinner_item)
        uniAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        uniSpinner.apply {
            setAdapter(uniAdapter, 0)
            setOnSpinnerItemClickListener { position, itemAtPosition -> }
        }

        val deptAdapter = ArrayAdapter.createFromResource(this, R.array.dept_array,
                android.R.layout.simple_spinner_item)
        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        deptSpinner.apply {
            setAdapter(deptAdapter, 0)
            setOnSpinnerItemClickListener { position, itemAtPosition -> }
        }

        val levelAdapter = ArrayAdapter.createFromResource(this, R.array.level_array,
                android.R.layout.simple_spinner_item)
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        levelSpinner.apply {
            setAdapter(levelAdapter, 0)
            setOnSpinnerItemClickListener { position, itemAtPosition -> }
        }

        val user = FirebaseAuth.getInstance().currentUser
        val submitBtn: FancyButton = findViewById(R.id.submit_btn)
        enableBtn(submitBtn)

        submitBtn.setOnClickListener {
            disableBtn(submitBtn)

            if (checkFields(uniSpinner, deptSpinner, levelSpinner) && user != null) {
                val university = uniSpinner.selectedItem
                val department = deptSpinner.selectedItem
                val level = levelSpinner.selectedItem

                UserUtils.submitUniversityInfo(user, university, department, level, object: UserUtils.UniversityInfoCallback {
                    override fun onFailure(ex: Exception) {
                        enableBtn(submitBtn)
                        NetworkUtils.showConnectionErrorMessage(this@AddUniversityInfoActivity, "save your information")
                    }

                    override fun onSuccess(universityInfo: UniversityInfo?) {
                        // goToHome()
                        finish()
                        enableBtn(submitBtn)
                    }
                })
            }
        }
    }

    fun checkFields (vararg fields: View): Boolean {
        for (field in fields) {
            if (field is AwesomeSpinner) {
                if (TextUtils.isEmpty(field.selectedItem)) {
                    return false
                }
            }
        }
        return true
    }
}
