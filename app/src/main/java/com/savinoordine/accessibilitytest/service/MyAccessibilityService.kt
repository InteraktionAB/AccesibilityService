package com.savinoordine.accessibilitytest.service

import android.accessibilityservice.AccessibilityGestureEvent
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.savinoordine.accessibilitytest.MainActivity.Companion.TAG
import javax.inject.Inject

class MyAccessibilityService
@Inject
constructor() : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.d(TAG, "onAccessibilityEvent: $event")

        val eventText: String = when (event?.eventType) {
            AccessibilityEvent.TYPE_VIEW_CLICKED -> "Clicked"
            AccessibilityEvent.TYPE_VIEW_FOCUSED -> "Focused"
            else -> ""
        }
        Log.d(TAG, eventText)

    }

    override fun onInterrupt() {
        Log.d(">>>", "Interrupt")

    }

    override fun onGesture(gestureEvent: AccessibilityGestureEvent): Boolean {
        Log.d(">>>", "performing global action")
        performGlobalAction(GLOBAL_ACTION_BACK)
        return true
    }

    override fun onServiceConnected() {
        Log.d(">>>", "connected")
        val info = AccessibilityServiceInfo()
        info.apply {
            eventTypes =
                AccessibilityEvent.TYPE_VIEW_CLICKED or AccessibilityEvent.TYPE_VIEW_FOCUSED
//            packageNames = arrayOf("com.example.android.myFirstApp", "com.example.android.mySecondApp")
            feedbackType = AccessibilityEvent.TYPES_ALL_MASK
            notificationTimeout = 500
            performGlobalAction(1)
            serviceInfo = info
        }
    }

    fun isAccessibilityServiceEnabled(mContext: Context): Boolean {
        var accessibilityEnabled = 0
        val service: String =
            mContext.packageName + "/" + MyAccessibilityService::class.java.canonicalName
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                mContext.applicationContext.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
            Log.v(TAG, "accessibilityEnabled = $accessibilityEnabled")
        } catch (e: Settings.SettingNotFoundException) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.message)
        }
        val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            Log.v(TAG, "Accessibility Is Enabled")
            val settingValue: String = Settings.Secure.getString(
                mContext.applicationContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()
//                    Log.v(TAG, "AccessibilityService :: $this ")
                    if (accessibilityService.equals(service, ignoreCase = true)) {
                        Log.v(TAG, "accessibility is switched on!")
                        return true
                    }
                }
            }
        } else {
            Log.v(TAG, "accessibility is disabled")
        }
        return false
    }

}