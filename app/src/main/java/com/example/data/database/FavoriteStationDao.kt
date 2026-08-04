package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteStationDao {
    @Query("SELECT * FROM favorite_stations ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteStation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(station: FavoriteStation)

    @Delete
    suspend fun deleteFavorite(station: FavoriteStation)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_stations WHERE url = :url)")
    fun isFavorite(url: String): Flow<Boolean>
}
