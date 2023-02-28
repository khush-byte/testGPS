package com.example.testgps.main

import android.appwidget.AppWidgetManager
import android.content.*
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.RemoteViews
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.testgps.R
import com.example.testgps.database.BookDao
import com.example.testgps.databinding.ActivityMainBinding

lateinit var bookDao: BookDao

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var sharedPreference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            0
        )
        setContentView(binding.root)
        sharedPreference = getSharedPreferences("LocalMemory", Context.MODE_PRIVATE)
        initAppSate()

        binding.btnStart.setOnClickListener {
            runService()
        }

        binding.btnStop.setOnClickListener {
            stopService()
        }
    }

    private fun runService() {
        val manager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        } else {
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_START
                startService(this)
            }
        }

        val editor = sharedPreference.edit()
        editor.putInt("state", 1)
        editor.apply()
        initAppSate()
    }

    private fun stopService(){
        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
            startService(this)
        }

        val editor = sharedPreference.edit()
        editor.putInt("state", 0)
        editor.apply()
        initAppSate()
    }

    private fun initAppSate(){
        val context: Context = this
        var widgetText = "SYSTEM ERROR"
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val remoteViews = RemoteViews(context.packageName, R.layout.test_g_p_s)
        val thisWidget = ComponentName(context, TestGPS::class.java)

        if(sharedPreference.getInt("state", 0)!=0) {
            binding.mainStatusField.text = context.getString(R.string.appwidget_text_on)
            binding.mainStatusField.setTextColor(Color.WHITE)
            binding.btnStart.isEnabled = false
            binding.btnStop.isEnabled = true

            widgetText = context.getString(R.string.appwidget_text_on)
            remoteViews.setTextColor(R.id.appwidget_text, Color.WHITE)

        }else{
            binding.mainStatusField.text = context.getString(R.string.appwidget_text_off)
            binding.mainStatusField.setTextColor(Color.RED)
            binding.btnStart.isEnabled = true
            binding.btnStop.isEnabled = false

            widgetText = context.getString(R.string.appwidget_text_off)
            remoteViews.setTextColor(R.id.appwidget_text, Color.RED)
        }

        remoteViews.setTextViewText(R.id.appwidget_text, widgetText)
        appWidgetManager.updateAppWidget(thisWidget, remoteViews)
    }

    private fun buildAlertMessageNoGps() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) })
            .setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert: AlertDialog = builder.create()
        alert.show()
    }

//    private fun testDB() {
//        lifecycleScope.launch(Dispatchers.IO) {
//            //Insert
//            Log.i("MyTAG", "*****     Inserting 3 Books     **********")
//            bookDao.insertLocation(Book(0, "Java", "Alex", 1))
//            bookDao.insertLocation(Book(0, "PHP", "Mike", 1))
//            bookDao.insertLocation(Book(0, "Kotlin", "Amelia", 1))
//            Log.i("MyTAG", "*****     Inserted 3 Books       **********")
//
//            //Query
//            val books = bookDao.getAllLocations()
//            Log.i("MyTAG", "*****   ${books.size} books there *****")
//            for (book in books) {
//                Log.i("MyTAG", "id: ${book.id} name: ${book.location} author: ${book.datetime} state: ${book.state}")
//            }
//
//            //Update
//            Log.i("MyTAG", "*****      Updating a book      **********")
//            bookDao.updateBook(Book(1, "PHPUpdated", "Mike", 0))
//            //Query
//            val books2 = bookDao.getAllLocations()
//            Log.i("MyTAG", "*****   ${books2.size} books there *****")
//            for (book in books2) {
//                Log.i("MyTAG", "id: ${book.id} name: ${book.location} author: ${book.datetime} state: ${book.state}")
//            }
//
//            //delete
//            Log.i("MyTAG", "*****       Deleting a book      **********")
//            bookDao.deleteBook(Book(2, "Kotlin", "Amelia", 1))
//            //Query
//            val books3 = bookDao.getNewLocations()
//            Log.i("MyTAG", "*****   ${books3.size} books there *****")
//            for (book in books3) {
//                Log.i("MyTAG", "id: ${book.id} name: ${book.location} author: ${book.datetime} state: ${book.state}")
//            }
//        }
//    }
//
//    private fun doesDatabaseExist(context: Context, dbName: String): Boolean {
//        val dbFile: File = context.getDatabasePath(dbName)
//        return dbFile.exists()
//    }

    companion object {
        const val PIN = "4762989107706"
    }
}