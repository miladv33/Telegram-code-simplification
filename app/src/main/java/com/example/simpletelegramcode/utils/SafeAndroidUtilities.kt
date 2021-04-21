package com.example.simpletelegramcode.utils

import android.graphics.Point
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.StaticLayout
import android.util.DisplayMetrics
import android.widget.TextView
import java.util.*

class SafeAndroidUtilities {
    private val waitingForSms = false
    private val waitingForCall = false
    private val smsLock = Any()
    private val callLock = Any()
    var density = 1f
    var displaySize = Point()
    var photoSize: Int? = null
    var displayMetrics = DisplayMetrics()

    companion object {

        fun containsUnsupportedCharacters(text: String): Boolean {
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


        fun ellipsizeCenterEnd(
            textView: TextView,
            query: String?,
            availableWidth: Int,
            maxSymbols: Int
        ): CharSequence? {
            var finalText = textView.text
            try {
                val startHighlightedIndex = getStartHighlightedIndex(finalText,query, maxSymbols)
                finalText = getMaximumString(finalText,startHighlightedIndex,maxSymbols)
                val staticLayout = StaticLayout(
                    finalText, textView.paint, Int.MAX_VALUE,
                    Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false
                )
                val endOfTextX = staticLayout.getLineWidth(0)
                if (endOfTextX + textView.paint.measureText("...") < availableWidth) {
                    return finalText
                }
                var i = startHighlightedIndex + 1
                while (i < finalText.length - 1 && !Character.isWhitespace(finalText[i])) {
                    i++
                }
                val endHighlightedIndex = i
                var endOfHighlight = staticLayout.getPrimaryHorizontal(endHighlightedIndex)
                if (staticLayout.isRtlCharAt(endHighlightedIndex)) {
                    endOfHighlight = endOfTextX - endOfHighlight
                }
                if (endOfHighlight < availableWidth) {
                    return finalText
                }
                var x =
                    endOfHighlight - availableWidth + textView.paint.measureText("...") * 2 + availableWidth * 0.1f
                if (finalText.length - endHighlightedIndex > 20) {
                    x += availableWidth * 0.1f
                }
                if (x > 0) {
                    var charOf = staticLayout.getOffsetForHorizontal(0, x)
                    var k = 0
                    if (charOf > finalText.length - 1) {
                        charOf = finalText.length - 1
                    }
                    while (!Character.isWhitespace(finalText[charOf]) && k < 10) {
                        k++
                        charOf++
                        if (charOf > finalText.length - 1) {
                            charOf = staticLayout.getOffsetForHorizontal(0, x)
                            break
                        }
                    }
                    val sub: CharSequence
                    if (k >= 10) {
                        x =
                            staticLayout.getPrimaryHorizontal(startHighlightedIndex + 1) - availableWidth * 0.3f
                        sub = finalText.subSequence(
                            staticLayout.getOffsetForHorizontal(0, x),
                            finalText.length
                        )
                    } else {
                        if (charOf > 0 && charOf < finalText.length - 2 && Character.isWhitespace(
                                finalText[charOf]
                            )
                        ) {
                            charOf++
                        }
                        sub = finalText.subSequence(charOf, finalText.length)
                    }
                    return SpannableStringBuilder.valueOf("...").append(sub)
                }
            } catch (e: Exception) {
                //FileLog.e(e);
            }
            return finalText
        }
        private fun getMaximumString(text: CharSequence, startHighlightedIndex: Int, maxSymbols: Int): CharSequence {
            var lastIndex = text.length
            return if (lastIndex > maxSymbols) {
                text.subSequence(
                    Math.max(0, startHighlightedIndex - maxSymbols / 2),
                    Math.min(lastIndex, startHighlightedIndex + maxSymbols / 2)
                )
            }else{
                text
            }
        }

        private fun getStartHighlightedIndex(finalText: CharSequence,
                                             query: String?, maxSymbols: Int):Int{
            val lastIndex = finalText.length
            var startHighlightedIndex = finalText.toString().toLowerCase(Locale.getDefault()).indexOf(query!!)
            if (lastIndex > maxSymbols) {
                startHighlightedIndex -= Math.max(0, startHighlightedIndex - maxSymbols / 2)
            }
            return startHighlightedIndex
        }
    }
}