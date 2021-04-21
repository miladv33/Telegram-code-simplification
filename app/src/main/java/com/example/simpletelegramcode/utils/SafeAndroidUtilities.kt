package com.example.simpletelegramcode.utils

import android.graphics.Point
import android.util.DisplayMetrics

class SafeAndroidUtilities {
    private val waitingForSms = false
    private val waitingForCall = false
    private val smsLock = Any()
    private val callLock = Any()
    var density = 1f
    var displaySize = Point()
    var photoSize: Int? = null
    var displayMetrics = DisplayMetrics()


    private fun containsUnsupportedCharacters(text: String): Boolean {
        return when {
            text.contains("\u202C") -> {
                true
            }
            text.contains("\u202D") -> {
                true
            }
            else -> text.contains("\u202E")
        }
    }
}