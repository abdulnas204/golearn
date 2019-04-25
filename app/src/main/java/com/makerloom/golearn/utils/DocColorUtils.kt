package com.makerloom.golearn.utils

import android.content.Context
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.makerloom.golearn.R

class DocColorUtils {
    companion object {
        fun getColorForExt (context: Context, ext: String): Int {
            if (TextUtils.isEmpty(ext)) {
                return ContextCompat.getColor(context, R.color.gray)
            }
            else if (matches(ext, "pdf")) {
                return ContextCompat.getColor(context, R.color.md_red_500)
            }
            else if (matches(ext, "doc", "docx", "odt", "rtf")) {
                return ContextCompat.getColor(context, R.color.md_blue_400)
            }
            else if (matches(ext, "ppt", "pptx", "ods")) {
                return ContextCompat.getColor(context, R.color.md_orange_300)
            }
            else if (matches(ext, "xls", "xlsx", "ots")) {
                return ContextCompat.getColor(context, R.color.md_green_400)
            }
            else {
                return ContextCompat.getColor(context, R.color.gray)
            }
        }

        fun matches (inExt: String, vararg exts: String): Boolean {
            for (ext in exts) {
                if (inExt.contains(ext)) {
                    return true
                }
            }
            return false
        }
    }
}