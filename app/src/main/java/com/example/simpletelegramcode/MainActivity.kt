package com.example.simpletelegramcode

import android.media.MediaPlayer
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.simpletelegramcode.utils.FileLog
import com.example.simpletelegramcode.utils.SafeAndroidUtilities
import com.example.simpletelegramcode.utils.getMemoryPermission
import com.example.simpletelegramcode.utils.hasMemoryPermission
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var frameLayout: FrameLayout
    lateinit var fileLoggerTimer: Timer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        frameLayout = FrameLayout(this)
        setContentView(
            frameLayout,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        if (hasMemoryPermission()) {
            testFileLogger()
        } else {
            getMemoryPermission()
        }
    }

    private fun testFileLogger() {
        addVideoView()
        FileLog.ensureInitied()
        var ramTestNumber = 0
        fileLoggerTimer = Timer()
        fileLoggerTimer.schedule(object : TimerTask() {
            override fun run() {
                ramTestNumber++
                FileLog.e("testing funny Video $ramTestNumber")
            }
        }, 1000, 1)
    }

    private fun addVideoView() {
        val videoPlayer = VideoView(this)
        val mediaPlayer = MediaPlayer.create(this, R.raw.funny_videos)
        val surfaceHolder = videoPlayer.holder
        surfaceHolder.addCallback(object : SurfaceHolder.Callback {

            override fun surfaceCreated(holder: SurfaceHolder) {
                videoPlayer.background =
                    ContextCompat.getDrawable(applicationContext, android.R.color.transparent)
                mediaPlayer.setDisplay(holder)
                mediaPlayer.start()
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {

            }

        })
        mediaPlayer.setOnCompletionListener {
            fileLoggerTimer.cancel()
            frameLayout.removeView(videoPlayer)
            val simpleButton = Button(this)
            simpleButton.text = "my father my fathermy father "
            simpleButton.text =
                SafeAndroidUtilities.ellipsizeCenterEnd(
                    simpleButton,
                    "my father", 100, 10
                )
            frameLayout.addView(simpleButton)
        }

        frameLayout.addView(videoPlayer)
    }
}