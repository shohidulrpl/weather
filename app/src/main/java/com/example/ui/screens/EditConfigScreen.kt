package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.navigation.NavController
import com.example.data.WeatherConfig
import com.example.ui.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditConfigScreen(
    navController: NavController,
    viewModel: WeatherViewModel
) {
    val activeConfig by viewModel.weatherConfig.collectAsState()

    var currentTemp by remember { mutableStateOf(28.0) }
    var condition by remember { mutableStateOf("Partly Cloudy") }
    var humidity by remember { mutableStateOf(80) }
    var windSpeed by remember { mutableStateOf(14.0) }
    var pressure by remember { mutableStateOf(1008) }
    var airQuality by remember { mutableStateOf("Good") }
    var patengaWaterLevel by remember { mutableStateOf("Normal (1.2m)") }
    var sunrise by remember { mutableStateOf("05:12 AM") }
    var sunset by remember { mutableStateOf("06:34 PM") }
    var tempMax by remember { mutableStateOf(33.0) }
    var tempMin by remember { mutableStateOf(25.0) }

    val conditionOptions = listOf(
        "Sunny",
        "Partly Cloudy",
        "Cloudy",
        "Monsoon Rain",
        "Heavy Thunderstorm",
        "Winter / Cool",
        "Foggy"
    )

    val aqiOptions = listOf("Good", "Moderate", "Unhealthy", "Hazardous")
    val tideOptions = listOf("Low (0.4m)", "Normal (1.2m)", "High (2.1m)", "Surge warning (3.5m)")

    // Initialize state from existing database values
    LaunchedEffect(activeConfig) {
        currentTemp = activeConfig.currentTemp
        condition = activeConfig.condition
        humidity = activeConfig.humidity
        windSpeed = activeConfig.windSpeed
        pressure = activeConfig.pressure
        airQuality = activeConfig.airQuality
        patengaWaterLevel = activeConfig.patengaWaterLevel
        sunrise = activeConfig.sunrise
        sunset = activeConfig.sunset
        tempMax = activeConfig.tempMax
        tempMin = activeConfig.tempMin
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Simulate / Edit Climate") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.testTag("config_back")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Adjust the primary weather variables of Chattogram to simulate different atmospheric, seasonal, or storm conditions offline.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            // Temperature Controls
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Baseline Temperature Settings", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Current Temperature: ${String.format("%.1f", currentTemp)}°C")
                    }
                    Slider(
                        value = currentTemp.toFloat(),
                        onValueChange = {
                            currentTemp = it.toDouble()
                            // Keep max/min realistic relative to current
                            if (currentTemp > tempMax) tempMax = currentTemp + 2.0
                            if (currentTemp < tempMin) tempMin = currentTemp - 2.5
                        },
                        valueRange = 10f..45f,
                        modifier = Modifier.testTag("config_current_temp_slider")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Min (Night)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = String.format("%.1f", tempMin),
                                onValueChange = { tempMin = it.toDoubleOrNull() ?: tempMin },
                                singleLine = true,
                                modifier = Modifier.testTag("config_temp_min_input")
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Max (Noon)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = String.format("%.1f", tempMax),
                                onValueChange = { tempMax = it.toDoubleOrNull() ?: tempMax },
                                singleLine = true,
                                modifier = Modifier.testTag("config_temp_max_input")
                            )
                        }
                    }
                }
            }

            // Weather Condition
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Atmospheric Condition", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        conditionOptions.forEach { option ->
                            val isSelected = condition == option
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    condition = option
                                    // Set automatic defaults based on condition choices to aid beginners
                                    when (option) {
                                        "Heavy Thunderstorm" -> {
                                            humidity = 95
                                            windSpeed = 38.0
                                            pressure = 998
                                            patengaWaterLevel = "High (2.1m)"
                                        }
                                        "Monsoon Rain" -> {
                                            humidity = 90
                                            windSpeed = 22.0
                                            pressure = 1002
                                            patengaWaterLevel = "Normal (1.2m)"
                                        }
                                        "Sunny" -> {
                                            humidity = 62
                                            windSpeed = 8.5
                                            pressure = 1012
                                            patengaWaterLevel = "Low (0.4m)"
                                        }
                                        "Winter / Cool" -> {
                                            humidity = 48
                                            windSpeed = 11.0
                                            pressure = 1018
                                            patengaWaterLevel = "Normal (1.2m)"
                                        }
                                    }
                                },
                                label = { Text(option) },
                                modifier = Modifier.testTag("config_condition_$option")
                            )
                        }
                    }
                }
            }

            // Atmospheric Dials (Humidity / Wind / Pressure)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Meteorological Dials", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                    // Humidity
                    Column {
                        Text("Humidity: $humidity%", fontSize = 13.sp)
                        Slider(
                            value = humidity.toFloat(),
                            onValueChange = { humidity = it.toInt() },
                            valueRange = 30f..100f
                        )
                    }

                    // Wind Speed
                    Column {
                        Text("Sea Wind: ${String.format("%.1f", windSpeed)} km/h", fontSize = 13.sp)
                        Slider(
                            value = windSpeed.toFloat(),
                            onValueChange = { windSpeed = it.toDouble() },
                            valueRange = 2f..75f
                        )
                    }

                    // Pressure
                    Column {
                        Text("Barometric Pressure: $pressure hPa", fontSize = 13.sp)
                        Slider(
                            value = pressure.toFloat(),
                            onValueChange = { pressure = it.toInt() },
                            valueRange = 980f..1024f
                        )
                    }
                }
            }

            // Marine and Air Quality Settings
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Ocean Tides & Air Quality", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                    Text("Patenga Tidal Stage", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        tideOptions.forEach { tide ->
                            val isSelected = patengaWaterLevel == tide
                            FilterChip(
                                selected = isSelected,
                                onClick = { patengaWaterLevel = tide },
                                label = { Text(tide, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text("Air Quality Index (AQI)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        aqiOptions.forEach { aqi ->
                            val isSelected = airQuality == aqi
                            FilterChip(
                                selected = isSelected,
                                onClick = { airQuality = aqi },
                                label = { Text(aqi, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }

            // Daylight hours
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Daylight Schedule", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = sunrise,
                            onValueChange = { sunrise = it },
                            label = { Text("Sunrise Time") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = sunset,
                            onValueChange = { sunset = it },
                            label = { Text("Sunset Time") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            }

            // Save Buttons
            Button(
                onClick = {
                    viewModel.updateConfig(
                        WeatherConfig(
                            currentTemp = currentTemp,
                            condition = condition,
                            humidity = humidity,
                            windSpeed = windSpeed,
                            pressure = pressure,
                            airQuality = airQuality,
                            patengaWaterLevel = patengaWaterLevel,
                            sunrise = sunrise,
                            sunset = sunset,
                            tempMax = tempMax,
                            tempMin = tempMin,
                            lastUpdated = System.currentTimeMillis()
                        )
                    )
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_config_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(imageVector = Icons.Filled.Update, contentDescription = "Update config")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Apply Weather Settings", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
