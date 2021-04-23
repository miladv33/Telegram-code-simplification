/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */
package com.example.simpletelegramcode.utils

import android.util.Log
import com.example.simpletelegramcode.ApplicationLoader.Companion.getContext
import com.example.simpletelegramcode.utils.thread.DispatchQueue
import com.example.simpletelegramcode.utils.time.FastDateFormat
import kotlin.jvm.Volatile
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.lang.Exception
import java.util.*

class FileLog {
    private var streamWriter: OutputStreamWriter? = null
    private var dateFormat: FastDateFormat? = null
    private var logQueue: DispatchQueue? = null
    private var currentFile: File? = null
    private var networkFile: File? = null
    private var tonlibFile: File? = null
    private var initied = false
    fun init() {
        if (initied) {
            return
        }
        dateFormat = FastDateFormat.getInstance("dd_MM_yyyy_HH_mm_ss", Locale.US)
        try {
            val sdCard = getContext().getExternalFilesDir(null) ?: return
            val dir = File(sdCard.absolutePath + "/logs")
            dir.mkdirs()
            currentFile = File(dir, dateFormat?.format(System.currentTimeMillis()) + ".txt")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            logQueue = DispatchQueue("logQueue")
            currentFile!!.createNewFile()
            val stream = FileOutputStream(currentFile)
            streamWriter = OutputStreamWriter(stream)
            streamWriter!!.write("-----start log ${dateFormat?.format(System.currentTimeMillis())}-----".trimIndent())
            streamWriter!!.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        initied = true
    }

    companion object {
        private const val tag = "tmessages"

        @Volatile
        var instance: FileLog? = null
            get() {
                var localInstance = field
                if (localInstance == null) {
                    synchronized(FileLog::class.java) {
                        localInstance = field
                        if (localInstance == null) {
                            localInstance = FileLog()
                            field = localInstance
                        }
                    }
                }
                return localInstance
            }
            private set

        fun ensureInitied() {
            instance!!.init()
        }

        val networkLogPath: String
            get() {
                try {
                    val sdCard = getContext().getExternalFilesDir(null) ?: return ""
                    val dir = File(sdCard.absolutePath + "/logs")
                    dir.mkdirs()
                    instance!!.networkFile = File(
                        dir,
                        instance!!.dateFormat!!.format(System.currentTimeMillis()) + "_net.txt"
                    )
                    return instance!!.networkFile!!.absolutePath
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
                return ""
            }
        val tonlibLogPath: String
            get() {
                try {
                    val sdCard = getContext().getExternalFilesDir(null) ?: return ""
                    val dir = File(sdCard.absolutePath + "/logs")
                    dir.mkdirs()
                    instance!!.tonlibFile = File(
                        dir,
                        instance!!.dateFormat!!.format(System.currentTimeMillis()) + "_tonlib.txt"
                    )
                    return instance!!.tonlibFile!!.absolutePath
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
                return ""
            }

          fun  e(message: String, exception: Throwable) {
            ensureInitied()
            Log.e(tag, message, exception)
            if (instance!!.streamWriter != null) {
                instance!!.logQueue!!.postRunnable {
                    try {
                        instance!!.streamWriter!!.write(
                            "${instance!!.dateFormat!!.format(System.currentTimeMillis())} E/tmessages: $message"
                        )
                        instance!!.streamWriter!!.write(exception.toString())
                        instance!!.streamWriter!!.flush()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        fun e(message: String) {
            ensureInitied()
            Log.e(tag, message)
            if (instance!!.streamWriter != null) {
                instance!!.logQueue!!.postRunnable {
                    try {
                        instance!!.streamWriter!!.write(
                            "${instance!!.dateFormat!!.format(System.currentTimeMillis())} E/tmessages: $message"
                        )
                        instance!!.streamWriter!!.flush()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        fun e(e: Throwable) {
            ensureInitied()
            e.printStackTrace()
            if (instance!!.streamWriter != null) {
                instance!!.logQueue!!.postRunnable {
                    try {
                        instance!!.streamWriter!!.write(
                            "${instance!!.dateFormat!!.format(System.currentTimeMillis())} E/tmessages: $e"
                        )
                        val stack = e.stackTrace
                        for (a in stack.indices) {
                            instance!!.streamWriter!!.write(
                                "${instance!!.dateFormat!!.format(System.currentTimeMillis())} E/tmessages: ${stack[a]}"
                            )
                        }
                        instance!!.streamWriter!!.flush()
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }
            } else {
                e.printStackTrace()
            }
        }

        fun d(message: String) {
            ensureInitied()
            Log.d(tag, message)
            if (instance!!.streamWriter != null) {
                instance!!.logQueue!!.postRunnable {
                    try {
                        instance!!.streamWriter!!.write(
                            "${instance!!.dateFormat!!.format(System.currentTimeMillis())} D/tmessages: $message"
                        )
                        instance!!.streamWriter!!.flush()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        fun w(message: String) {
            ensureInitied()
            Log.w(tag, message)
            if (instance!!.streamWriter != null) {
                instance!!.logQueue!!.postRunnable {
                    try {
                        instance!!.streamWriter!!.write(
                            "${instance!!.dateFormat!!.format(System.currentTimeMillis())} W/tmessages: $message"
                        )
                        instance!!.streamWriter!!.flush()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        fun cleanupLogs() {
            ensureInitied()
            val sdCard = getContext().getExternalFilesDir(null) ?: return
            val dir = File(sdCard.absolutePath + "/logs")
            val files = dir.listFiles()
            if (files != null) {
                for (a in files.indices) {
                    val file = files[a]
                    if (instance!!.currentFile != null && file.absolutePath == instance!!.currentFile!!.absolutePath) {
                        continue
                    }
                    if (instance!!.networkFile != null && file.absolutePath == instance!!.networkFile!!.absolutePath) {
                        continue
                    }
                    if (instance!!.tonlibFile != null && file.absolutePath == instance!!.tonlibFile!!.absolutePath) {
                        continue
                    }
                    file.delete()
                }
            }
        }
    }

    init {
        init()
    }
}