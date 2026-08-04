package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_stations")
data class FavoriteStation(
    @PrimaryKey val url: String,
    val name: String,
    val country: String,
    val genre: String,
    val favicon: String,
    val timestamp: Long = System.currentTimeMillis()
)
