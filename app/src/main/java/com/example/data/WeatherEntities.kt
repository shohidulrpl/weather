package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_config")
data class WeatherConfig(
    @PrimaryKey val id: Int = 1,
    val currentTemp: Double = 28.5,
    val condition: String = "Partly Cloudy",
    val humidity: Int = 82,
    val windSpeed: Double = 14.5,
    val pressure: Int = 1008,
    val sunrise: String = "05:12 AM",
    val sunset: String = "06:34 PM",
    val tempMax: Double = 33.0,
    val tempMin: Double = 25.0,
    val airQuality: String = "Good",
    val patengaWaterLevel: String = "Normal (1.2m)",
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "weather_journal")
data class WeatherJournal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val area: String, // e.g. "Patenga", "Agrabad", "GEC Circle", "Halishahar", "Chawkbazar", "Muradpur"
    val temperature: Double,
    val condition: String, // e.g. "Sunny", "Heavy Rain", "Thunderstorm", "Cloudy", "Drizzle"
    val notes: String,
    val timestamp: Long = System.currentTimeMillis()
)
