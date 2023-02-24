package com.example.testgps.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_table")
data class Book(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var location: String,

    @ColumnInfo(name = "date")
    var datetime: String,

    @ColumnInfo(name = "state")
    var state: Int
)
