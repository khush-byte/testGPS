package com.example.testgps.main

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
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

    if(sharedPreference.getInt("state", 0)!=0) {
        widgetText = context.getString(R.string.appwidget_text_on)
        views.setTextColor(R.id.appwidget_text, Color.WHITE)
    }else{
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
}