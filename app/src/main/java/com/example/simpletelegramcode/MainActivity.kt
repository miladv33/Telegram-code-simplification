package com.example.simpletelegramcode

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.simpletelegramcode.utils.SafeAndroidUtilities

class MainActivity : AppCompatActivity() {

    lateinit var frameLayout:FrameLayout

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
        val simpleButton = Button(this)
        simpleButton.text = "my father my fathermy father "
        simpleButton.text =
            SafeAndroidUtilities.ellipsizeCenterEnd(simpleButton,
                "my father",100,10)
        frameLayout.addView(simpleButton)
    }
}