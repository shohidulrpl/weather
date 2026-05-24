package com.example.data

import kotlinx.coroutines.flow.Flow

class WeatherRepository(private val weatherDao: WeatherDao) {
    val weatherConfig: Flow<WeatherConfig?> = weatherDao.getWeatherConfig()
    val allJournals: Flow<List<WeatherJournal>> = weatherDao.getAllJournals()

    suspend fun updateWeatherConfig(config: WeatherConfig) {
        weatherDao.insertWeatherConfig(config)
    }

    suspend fun insertJournal(journal: WeatherJournal) {
        weatherDao.insertJournal(journal)
    }

    suspend fun updateJournal(journal: WeatherJournal) {
        weatherDao.updateJournal(journal)
    }

    suspend fun deleteJournal(journal: WeatherJournal) {
        weatherDao.deleteJournal(journal)
    }

    suspend fun deleteJournalById(id: Int) {
        weatherDao.deleteJournalById(id)
    }
}
