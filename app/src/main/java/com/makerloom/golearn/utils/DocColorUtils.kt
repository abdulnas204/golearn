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
                return ContextCompat.getColor(context, R.color.md_red_600)
            }
            else if (matches(ext, "doc", "docx", "odf")) {
                return ContextCompat.getColor(context, R.color.md_blue_500)
            }
            else if (matches(ext, "ppt", "pptx", "opf")) {
                return ContextCompat.getColor(context, R.color.md_orange_400)
            }
            else if (matches(ext, "doc", "docx", "odf")) {
                return ContextCompat.getColor(context, R.color.md_blue_500)
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