package com.example.myhike

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hike")
data class Hike(@PrimaryKey(autoGenerate = true) var id: Int,
                @ColumnInfo(name = "name") var name: String,
                @ColumnInfo(name = "area") var area: String,
                @ColumnInfo(name = "category") var category: String,
                @ColumnInfo(name = "length") var length: Int,
                @ColumnInfo(name = "nightstops") var nightstops: Int,
                @ColumnInfo(name = "favorite") var favorite: Boolean)