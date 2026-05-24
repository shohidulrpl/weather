package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather_config WHERE id = 1 LIMIT 1")
    fun getWeatherConfig(): Flow<WeatherConfig?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherConfig(config: WeatherConfig)

    @Query("SELECT * FROM weather_journal ORDER BY timestamp DESC")
    fun getAllJournals(): Flow<List<WeatherJournal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournal(journal: WeatherJournal)

    @Update
    suspend fun updateJournal(journal: WeatherJournal)

    @Delete
    suspend fun deleteJournal(journal: WeatherJournal)

    @Query("DELETE FROM weather_journal WHERE id = :id")
    suspend fun deleteJournalById(id: Int)
}
