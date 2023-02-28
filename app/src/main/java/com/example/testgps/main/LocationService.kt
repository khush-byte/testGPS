package com.example.testgps.main

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.room.Room
import com.example.testgps.R
import com.example.testgps.database.Book
import com.example.testgps.database.BookDatabase
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocationService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    private lateinit var locationManager: LocationManager
    private var newlocation: Location? = null
    var checkLoc: String = ""
    private lateinit var mainHandler: Handler

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location...")
            .setContentText("Location: null")
            .setSmallIcon(R.drawable.ic_baseline_gps_fixed)
            .setOngoing(true)
        startTracking()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {
                Log.d("myTag", "mainHandler")
                val lat = newlocation?.latitude.toString().take(7)
                val long = newlocation?.longitude.toString().take(7)
                val newLocation = "$lat, $long"
                val updateNotification = notification.setContentText(
                    "Location: ($newLocation)"
                )
                insertNewLocation(newLocation)
                notificationManager.notify(1, updateNotification.build())
                mainHandler.postDelayed(this, 600000)
            }
        })

        startForeground(1, notification.build())
        setStatus(true)
    }

    @SuppressLint("MissingPermission")
    private fun startTracking() {
        locationManager = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            250000,
            0f,
            locationListener
        )
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val loc = "${location.latitude},\n${location.longitude}"
            Log.d("myTag", loc)
            newlocation = location
        }

        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        }

        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private fun stop() {
        locationManager.removeUpdates(locationListener);
        mainHandler.removeCallbacksAndMessages(null);
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        setStatus(false)
    }

    @SuppressLint("ResourceType")
    private fun setStatus(status: Boolean) {
        val context: Context = this
        var widgetText = "SYSTEM ERROR"
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val remoteViews = RemoteViews(context.packageName, R.layout.test_g_p_s)
        val thisWidget = ComponentName(context, TestGPS::class.java)

        if (status) {
            widgetText = context.getString(R.string.appwidget_text_on)
            remoteViews.setTextColor(R.id.appwidget_text, Color.WHITE)
        } else {
            widgetText = context.getString(R.string.appwidget_text_off)
            remoteViews.setTextColor(R.id.appwidget_text, Color.RED)
        }

        remoteViews.setTextViewText(R.id.appwidget_text, widgetText)
        appWidgetManager.updateAppWidget(thisWidget, remoteViews)
    }

    private fun insertNewLocation(location: String) {
        serviceScope.launch(Dispatchers.IO) {
            val db = Room.databaseBuilder(
                applicationContext,
                BookDatabase::class.java, "location_database"
            ).build()
            bookDao = db.bookDao()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val current: String = LocalDateTime.now().format(formatter)
            if (location != "null, null" && location != checkLoc) {
                bookDao.insertLocation(Book(0, location, current, 1))
                checkLoc = location
            }
        }
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}