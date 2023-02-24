package com.example.testgps.main

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.testgps.database.Book
import com.example.testgps.database.BookDao
import com.example.testgps.database.BookDatabase
import com.example.testgps.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

lateinit var bookDao: BookDao
class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
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

        binding.btnStart.setOnClickListener {
            runService()
        }

        binding.btnStop.setOnClickListener {
            Intent(applicationContext, LocationService::class.java).apply{
                action = LocationService.ACTION_STOP
                startService(this)
            }
        }
        //Log.i("MyTAG", doesDatabaseExist(this, "location_database").toString())

        //Create DB
//        if(!doesDatabaseExist(this, "location_database")) {
//            val db = Room.databaseBuilder(
//                applicationContext,
//                BookDatabase::class.java, "location_database"
//            ).build()
//            bookDao = db.bookDao()
//            lifecycleScope.launch(Dispatchers.IO) {
//                bookDao.insertLocation(Book(0, "test", "test", 0))
//            }
//        }
        //testDB()
    }

    private fun runService() {
        val manager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }else{
            Intent(applicationContext, LocationService::class.java).apply{
                action = LocationService.ACTION_START
                startService(this)
            }
        }
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
}