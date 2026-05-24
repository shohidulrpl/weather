package com.example

import android.app.Application
import com.example.data.WeatherDatabase
import com.example.data.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class WeatherApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { WeatherDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { WeatherRepository(database.weatherDao()) }
}
