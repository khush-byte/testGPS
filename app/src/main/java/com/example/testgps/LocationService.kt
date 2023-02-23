package com.example.testgps

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LocationService: Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    private lateinit var locationManager: LocationManager
    private var newlocation: Location? = null

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
        when(intent?.action){
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(){
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location...")
            .setContentText("Location: null")
            .setSmallIcon(R.drawable.ic_baseline_gps_fixed)
            .setOngoing(true)
        startTracking()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        locationClient
//            .getLocationUpdates(10000L)
//            .catch { e -> e.printStackTrace() }
//            .onEach { location ->
//                val lat = newlocation?.latitude.toString().take(8)
//                val long = newlocation?.longitude.toString().take(8)
//                val updateNotification = notification.setContentText(
//                    "Location: ($lat, $long)"
//                )
//                notificationManager.notify(1, updateNotification.build())
//            }
//            .launchIn(serviceScope)
        val mainHandler = Handler(Looper.getMainLooper())

        mainHandler.post(object : Runnable {
            override fun run() {
                val lat = newlocation?.latitude.toString().take(7)
                val long = newlocation?.longitude.toString().take(7)
                val updateNotification = notification.setContentText(
                    "Location: ($lat, $long)"
                )
                notificationManager.notify(1, updateNotification.build())
                mainHandler.postDelayed(this, 10000)
            }
        })

        startForeground(1, notification.build())
    }

    @SuppressLint("MissingPermission")
    private fun startTracking(){
        locationManager = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            10000L,
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

    private fun stop(){
        locationManager.removeUpdates(locationListener);
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object{
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}