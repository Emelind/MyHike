package com.example.myhike

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HikeDao {

    @Insert
    fun insert(hike: Hike)

    @Delete
    fun delete(hike: Hike)

    @Query("SELECT * FROM hike")
    fun getAll() : MutableList<Hike>

    @Query("SELECT * FROM hike WHERE nightstops LIKE :numberOfNightStops")
    fun findByNightStops(numberOfNightStops: Int) : MutableList<Hike>

    @Query("SELECT * FROM hike WHERE category LIKE :category")
    fun findByCategory(category: String) : MutableList<Hike>

    @Query("SELECT * FROM hike WHERE length < :length")
    fun findByLength(length: Int) : MutableList<Hike>

    @Query("SELECT * FROM hike WHERE favorite LIKE :favorite")
    fun findByFavorite(favorite: Boolean) : MutableList<Hike>
}