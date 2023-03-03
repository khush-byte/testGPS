package com.example.testgps.database

import androidx.room.*

@Dao
interface BookDao {
    @Insert
    suspend fun insertLocation(book: Book)

    @Query("SELECT * FROM location_table")
    fun getAllLocations(): List<Book>

    @Query("SELECT * FROM location_table WHERE state = 1")
    fun getNewLocations(): List<Book>

    @Query("SELECT * FROM location_table WHERE state = 1 LIMIT 1")
    fun getLocation(): Book

    @Update
    suspend fun updateBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)
}