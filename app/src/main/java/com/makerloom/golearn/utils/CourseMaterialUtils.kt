package com.makerloom.golearn.utils

import com.makerloom.golearn.models.Course

class CourseMaterialUtils {
    companion object {
        val TAG = CourseMaterialUtils::class.java.simpleName

        fun getContentSummary (course: Course): String {
            return "5 Documents, 12 Videos and 3 Images"
        }

        fun getLastUpdate (course: Course): String {
            return "Updated Yesterday"
        }
    }
}