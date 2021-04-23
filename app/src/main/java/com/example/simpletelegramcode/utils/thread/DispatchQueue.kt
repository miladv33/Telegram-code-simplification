/*
 * This is the source code of Telegram for Android v. 7.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2020.
 */
package com.example.simpletelegramcode.utils.thread

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import com.example.simpletelegramcode.utils.FileLog
import java.util.concurrent.CountDownLatch

class DispatchQueue @JvmOverloads constructor(threadName: String?, start: Boolean = true) :
    Thread() {
    @Volatile
    private var handler: Handler? = null
    private val syncLatch = CountDownLatch(1)
    var lastTaskTime: Long = 0
        private set
    init {
        name = threadName
        if (start) {
            start()
        }
    }
    override fun run() {
        Looper.prepare()
        handler = Handler()
        syncLatch.countDown()
        Looper.loop()
    }

    fun postRunnable(runnable: Runnable?): Boolean {
        lastTaskTime = SystemClock.elapsedRealtime()
        return postRunnable(runnable, 0)
    }

    fun postRunnable(runnable: Runnable?, delay: Long): Boolean {
        try {
            syncLatch.await()
        } catch (e: Exception) {
            FileLog.e(e)
        }
        return if (delay <= 0) {
            handler!!.post(runnable!!)
        } else {
            handler!!.postDelayed(runnable!!, delay)
        }
    }

    fun sendMessage(msg: Message?, delay: Int) {
        try {
            syncLatch.await()
            if (delay <= 0) {
                handler!!.sendMessage(msg!!)
            } else {
                handler!!.sendMessageDelayed(msg!!, delay.toLong())
            }
        } catch (ignore: Exception) {
        }
    }

    fun cancelRunnable(runnable: Runnable?) {
        try {
            syncLatch.await()
            handler!!.removeCallbacks(runnable!!)
        } catch (e: Exception) {
            FileLog.e(e)
        }
    }

    fun cancelRunnables(runnables: Array<Runnable?>) {
        try {
            syncLatch.await()
            for (i in runnables.indices) {
                handler!!.removeCallbacks(runnables[i]!!)
            }
        } catch (e: Exception) {
            FileLog.e(e)
        }
    }

    fun cleanupQueue() {
        try {
            syncLatch.await()
            handler!!.removeCallbacksAndMessages(null)
        } catch (e: Exception) {
            FileLog.e(e)
        }
    }

    fun handleMessage(inputMessage: Message?) {}

    fun recycle() {
        handler!!.looper.quit()
    }




}