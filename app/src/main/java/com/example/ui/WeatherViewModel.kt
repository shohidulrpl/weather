package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.WeatherConfig
import com.example.data.WeatherJournal
import com.example.data.WeatherRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    // Observe local weather configuration, falling back to a default if null
    val weatherConfig: StateFlow<WeatherConfig> = repository.weatherConfig
        .map { it ?: WeatherConfig() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WeatherConfig()
        )

    // Observe local weather journal log list
    val journalEntries: StateFlow<List<WeatherJournal>> = repository.allJournals
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateConfig(config: WeatherConfig) {
        viewModelScope.launch {
            repository.updateWeatherConfig(config)
        }
    }

    fun addJournalEntry(area: String, temp: Double, condition: String, notes: String) {
        viewModelScope.launch {
            repository.insertJournal(
                WeatherJournal(
                    area = area,
                    temperature = temp,
                    condition = condition,
                    notes = notes,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun updateJournalEntry(journal: WeatherJournal) {
        viewModelScope.launch {
            repository.updateJournal(journal)
        }
    }

    fun deleteJournalEntry(id: Int) {
        viewModelScope.launch {
            repository.deleteJournalById(id)
        }
    }

    // Helper to generate next 7 days forecast deterministically from config
    fun generate7DayForecast(config: WeatherConfig): List<ForecastDay> {
        val days = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        val calendar = Calendar.getInstance()
        val currentDayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0-indexed index

        val conditionList = when (config.condition) {
            "Heavy Thunderstorm", "Monsoon Rain", "Tropical Storm" -> listOf(
                "Heavy Rain" to "Torrential downpours, high risk of waterlogging in Agrabad areas.",
                "Thunderstorm" to "Lightning strikes likely. Wind gusts up to 45 km/h.",
                "Heavy Rain" to "Continuous rain, Patenga storm surge alert standard.",
                "Drizzle" to "Intermittent showers, cooling temperature trends.",
                "Cloudy" to "Gloomy overcast skies, high humidity.",
                "Partly Cloudy" to "Sun breaks in the afternoon.",
                "Sunny / Hot" to "Humid clearance, warm sea breeze."
            )
            "Foggy", "Overcast", "Cloudy" -> listOf(
                "Cloudy" to "Thick overcast, low visibility over Karnaphuli channel.",
                "Drizzle" to "Light misty drizzle, cool damp day.",
                "Partly Cloudy" to "Sun peaking through layers.",
                "Partly Cloudy" to "Mild temperatures, comfortable.",
                "Sunny" to "Clear sunny afternoon.",
                "Cloudy" to "Evening sea fog rolling in.",
                "Partly Cloudy" to "Light cloud cover, cool breeze."
            )
            "Winter / Cool" -> listOf(
                "Sunny" to "Crisp winter morning, dry and pleasant weather.",
                "Sunny" to "Optimal weather for Foy's Lake sightseeing.",
                "Sunny" to "Warm afternoon with dry air.",
                "Partly Cloudy" to "High light wispy clouds, pleasant evening.",
                "Sunny" to "Clean air with light northerly winds.",
                "Sunny" to "Perfect beach weather at Patenga.",
                "Partly Cloudy" to "Slightly cooler evening, comfortable breeze."
            )
            else -> listOf( // "Sunny / Hot" or "Partly Cloudy"
                "Sunny" to "Intensely warm over Chattogram city, stay hydrated.",
                "Partly Cloudy" to "Scattered light clouds, dynamic solar heating.",
                "Partly Cloudy" to "High humidity, hot coastal winds.",
                "Sunny" to "Clear blue skies over Karnaphuli port.",
                "Thunderstorm" to "Afternoon Kalboishakhi (Nor'wester) gusty spell.",
                "Partly Cloudy" to "Gradual clearing, fresh ocean breeze.",
                "Sunny" to "Bright sunshine, perfect for travel."
            )
        }

        return List(7) { index ->
            val forecastDayName = days[(currentDayIndex + index + 1) % 7]
            
            // Derive variations on temperatures based on index
            val tempVariationMax = when (index) {
                0 -> 0.0
                1 -> 1.2
                2 -> -1.5
                3 -> -0.4
                4 -> 2.1
                5 -> -2.0
                else -> 0.8
            }
            val tempVariationMin = when (index) {
                0 -> 0.0
                1 -> -0.8
                2 -> 1.0
                3 -> -1.2
                4 -> 1.5
                5 -> -0.5
                else -> -1.0
            }

            val derivedMax = (config.tempMax + tempVariationMax).coerceIn(15.0, 43.0)
            val derivedMin = (config.tempMin + tempVariationMin).coerceIn(10.0, 30.0)
            
            val pair = conditionList[index % conditionList.size]
            
            ForecastDay(
                dayName = forecastDayName,
                tempMax = derivedMax,
                tempMin = derivedMin,
                condition = pair.first,
                relativeHumidity = (config.humidity + (index * 2 - 4)).coerceIn(40, 100),
                windSpeed = (config.windSpeed + (index * 1.1 - 2)).coerceAtLeast(3.0),
                description = pair.second
            )
        }
    }
}

data class ForecastDay(
    val dayName: String,
    val tempMax: Double,
    val tempMin: Double,
    val condition: String,
    val relativeHumidity: Int,
    val windSpeed: Double,
    val description: String
)

class WeatherViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
