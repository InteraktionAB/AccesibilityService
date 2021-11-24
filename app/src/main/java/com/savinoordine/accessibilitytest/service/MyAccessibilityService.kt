package com.savinoordine.accessibilitytest.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.graphics.Path
import android.graphics.PixelFormat
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.Button
import android.widget.FrameLayout
import com.savinoordine.accessibilitytest.MainActivity.Companion.TAG
import com.savinoordine.accessibilitytest.R
import javax.inject.Inject


class MyAccessibilityService
@Inject
constructor() : AccessibilityService() {

    var mLayout: FrameLayout? = null
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val eventText: String = when (event?.eventType) {
            AccessibilityEvent.TYPE_VIEW_CLICKED -> "Clicked"
            AccessibilityEvent.TYPE_VIEW_FOCUSED -> "Focused"
            else -> ""
        }
        Log.d(TAG, "Event: $eventText")
    }

    override fun onInterrupt() {
        Log.d(TAG, "Interrupt")
    }

    override fun onServiceConnected() {
        Log.d(">>>", "connected")
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        mLayout = FrameLayout(this)
        val lp = WindowManager.LayoutParams()
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        lp.format = PixelFormat.TRANSLUCENT
        lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.TOP
        val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.action_bar, mLayout)
        wm.addView(mLayout, lp)

        configurePowerButton()
        configureSwipeButton()
        configureClick()
    }

    private fun configurePowerButton() {
        val powerButton: Button = mLayout!!.findViewById<View>(R.id.power) as Button
        powerButton.setOnClickListener { performGlobalAction(GLOBAL_ACTION_POWER_DIALOG) }
    }

    private fun configureSwipeButton() {
        val swipeButton = mLayout!!.findViewById<View>(R.id.swipe) as Button
        swipeButton.setOnClickListener {
            val swipePath = Path()
            swipePath.moveTo(1000F, 1000F)
            swipePath.lineTo(100F, 1000F)
            val gestureBuilder = GestureDescription.Builder()
            gestureBuilder.addStroke(StrokeDescription(swipePath, 0, 500))
            dispatchGesture(gestureBuilder.build(), null, null)
        }
    }

    private fun configureClick() {
        val clickButton = mLayout!!.findViewById<View>(R.id.click) as Button
        clickButton.setOnClickListener {
            val p = Path()
            val displayMetrics = resources.displayMetrics
            val height = displayMetrics.heightPixels;
            val top = height * .25
            val bottom = height * .75
            val midX = displayMetrics.widthPixels / 2

            p.moveTo(midX.toFloat(), bottom.toFloat())
            p.lineTo(midX.toFloat(), top.toFloat())

            val gestureBuilder = GestureDescription.Builder()
            gestureBuilder.addStroke(StrokeDescription(p, 10L, 200L, false))
            dispatchGesture(gestureBuilder.build(), callback, null)
        }
    }

    private val callback = object : AccessibilityService.GestureResultCallback() {
        override fun onCompleted(gestureDescription: GestureDescription?) {
            Log.d(TAG, "gesture completed")
            super.onCompleted(gestureDescription)
        }

        override fun onCancelled(gestureDescription: GestureDescription?) {
            Log.d(TAG, "gesture cancelled")
            super.onCancelled(gestureDescription)
        }
    }

}