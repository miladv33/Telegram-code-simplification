package com.example.simpletelegramcode.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun Activity.hasMemoryPermission(): Boolean {
    return !(ContextCompat.checkSelfPermission(
        this,
        "android.permission.READ_EXTERNAL_STORAGE"
    ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                "android.permission.WRITE_EXTERNAL_STORAGE"
            ) != PackageManager.PERMISSION_GRANTED)
}

fun Activity.getMemoryPermission() {
    if (ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, arrayOf("android.permission.READ_EXTERNAL_STORAGE"), 1)
    }

    if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, arrayOf("android.permission.WRITE_EXTERNAL_STORAGE"), 1)
    }
}