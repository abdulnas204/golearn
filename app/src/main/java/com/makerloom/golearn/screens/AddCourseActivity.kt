package com.makerloom.golearn.screens

import android.os.Bundle
import android.view.View
import com.makerloom.common.activity.MyPlainToolbarActivity
import com.makerloom.golearn.R

class AddCourseActivity : MyPlainToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)

        (findViewById<View>(R.id.submit_btn)).setOnClickListener {  }
    }

}
