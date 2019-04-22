package com.makerloom.golearn.models

import java.io.File

data class Document(val title: String, val fileSize: Long, val filePath: String) {
    fun getExt (): String {
        return File(filePath).extension
    }
}