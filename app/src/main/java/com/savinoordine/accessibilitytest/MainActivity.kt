package com.savinoordine.accessibilitytest

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.content.Intent
import android.graphics.Path
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.savinoordine.accessibilitytest.service.MyAccessibilityService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var accessibilityService: MyAccessibilityService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val btn = findViewById<Button>(R.id.accessibilityBtn)
        btn.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }

        val click = findViewById<Button>(R.id.displayClick)
        click.setOnClickListener {
            accessibilityService.isAccessibilityServiceEnabled(this)
            val dispatch = dispatch(100F, 100F)
            Log.d(TAG, "Dispatch: $dispatch")
        }
    }

    private fun buildClick(x: Float, y: Float): GestureDescription {
        val builder = GestureDescription.Builder()
        val p = Path()

        val displayMetrics = resources.displayMetrics
        val height = displayMetrics.heightPixels;
        val top = height * .25
        val mid = height * .5
        val bottom = height * .75
        val midX = displayMetrics.widthPixels / 2

        p.moveTo(midX.toFloat(), bottom.toFloat())
        p.lineTo(midX.toFloat(), top.toFloat())

//        p.moveTo(x, y)
//        p.lineTo(x + 10, y + 10)
        builder.addStroke(StrokeDescription(p, 10L, 200L, false))
        return builder.build()
//
//
//        val clickPath = Path()
//        clickPath.moveTo(x, y)
//        val clickStroke = GestureDescription.StrokeDescription(clickPath, 0, 1)
//        val clickBuilder = GestureDescription.Builder()
//        clickBuilder.addStroke(clickStroke)
//        return clickBuilder.build()
    }

    private fun dispatch(x: Float, y: Float): Boolean {
        Log.d(TAG, "A: $accessibilityService")
        return accessibilityService.dispatchGesture(
            buildClick(x, y),
            callback,
            null
        )
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

    companion object {
        const val TAG = ">>>"
    }
}