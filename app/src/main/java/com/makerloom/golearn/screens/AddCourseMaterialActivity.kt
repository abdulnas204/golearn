package com.makerloom.golearn.screens

import android.os.Bundle
import android.view.View
import com.makerloom.common.activity.MyPlainToolbarActivity
import com.makerloom.golearn.R

class AddCourseMaterialActivity : MyPlainToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course_material)

        (findViewById<View>(R.id.submit_btn)).setOnClickListener {  }
        (findViewById<View>(R.id.add_material_btn)).setOnClickListener {  }
    }

}
