package com.example.simpletelegramcode

import android.app.Application
import android.content.Context
import kotlin.coroutines.coroutineContext

class ApplicationLoader: Application() {

    init {
        instance = this
    }
    companion object {
        fun getContext(): Context {
            return instance.applicationContext
        }
        lateinit var instance: ApplicationLoader

    }
}