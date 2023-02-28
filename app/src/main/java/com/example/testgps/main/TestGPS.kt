package com.example.testgps.main

import android.app.ActivityManager
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testgps.R


/**
 * Implementation of App Widget functionality.
 */
class TestGPS : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    var widgetText = "SYSTEM ERROR"
    val sharedPreference = context.getSharedPreferences("LocalMemory", Context.MODE_PRIVATE)
    val views = RemoteViews(context.packageName, R.layout.test_g_p_s)

    if (sharedPreference.getInt("state", 0) != 0) {
        widgetText = context.getString(R.string.appwidget_text_on)
        views.setTextColor(R.id.appwidget_text, Color.WHITE)
    } else {
        widgetText = context.getString(R.string.appwidget_text_off)
        views.setTextColor(R.id.appwidget_text, Color.RED)
    }

    views.setTextViewText(R.id.appwidget_text, widgetText)

//    val intent = Intent(context, MainActivity::class.java)
//    val pendingIntent = PendingIntent.getActivity(
//        context,
//        0,
//        intent,
//        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT);
//    views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
    checkServiceStatus(context)
}

fun checkServiceStatus(context: Context) {
    val serviceState = isMyServiceRunning(LocationService::class.java, context)
    val sharedPreference = context.getSharedPreferences("LocalMemory", Context.MODE_PRIVATE)

    val mainHandler = Handler(Looper.getMainLooper())
    mainHandler.post(object : Runnable {
        override fun run() {
            //Log.d("myTag", "${serviceState}, ${sharedPreference.getInt("state", 0)}")
            if (!serviceState && sharedPreference.getInt("state", 0) == 1) {
                val editor = sharedPreference.edit()
                editor.putInt("serviceDropped", 1)
                editor.apply()
                Toast.makeText(
                    context,
                    "System error! Please activate the tracking service!",
                    Toast.LENGTH_LONG
                ).show()
                //Log.d("myTag", "${sharedPreference.getInt("serviceDropped", 0)}")
            }
            mainHandler.postDelayed(this, 1500000)
        }
    })
}

private fun isMyServiceRunning(serviceClass: Class<*>, context: Context): Boolean {
    val manager = context.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}
