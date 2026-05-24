package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.navigation.NavController
import com.example.data.WeatherJournal
import com.example.ui.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditJournalScreen(
    navController: NavController,
    viewModel: WeatherViewModel,
    journalId: Int?
) {
    val journals by viewModel.journalEntries.collectAsState()
    val isEditMode = journalId != null && journalId > 0

    // Form states
    var area by remember { mutableStateOf("") }
    var temperature by remember { mutableStateOf(28.0) }
    var condition by remember { mutableStateOf("Partly Cloudy") }
    var notes by remember { mutableStateOf("") }
    var showErrorMsg by remember { mutableStateOf("") }

    // Landmarks options
    val landmarkChips = listOf("GEC Circle", "Agrabad", "Patenga Beach", "Halishahar", "Chawkbazar", "Muradpur", "Foy's Lake")
    val weatherConditions = listOf("Sunny", "Partly Cloudy", "Cloudy", "Monsoon Rain", "Thunderstorm", "Foggy")

    // Populate data if editing
    LaunchedEffect(journalId, journals) {
        if (isEditMode) {
            val entry = journals.find { it.id == journalId }
            if (entry != null) {
                area = entry.area
                temperature = entry.temperature
                condition = entry.condition
                notes = entry.notes
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Weather Log" else "Add Local Weather Log") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.testTag("back_button")
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
                text = "Record observations in different parts of Chattogram to build your offline weather log.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            // Form Fields Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Location Input
                    Text(
                        text = "Location / Area",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedTextField(
                        value = area,
                        onValueChange = { area = it },
                        placeholder = { Text("e.g. Agrabad, GEC Circle") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("area_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Quick Area Tag Selection
                    Text(
                        text = "Or choose major landmarks:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        landmarkChips.forEach { landmark ->
                            val isSelected = area == landmark
                            FilterChip(
                                selected = isSelected,
                                onClick = { area = landmark },
                                label = { Text(landmark) },
                                modifier = Modifier.testTag("landmark_chip_$landmark")
                            )
                        }
                    }
                }
            }

            // Temperature Selector Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Estimated Temperature",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${String.format("%.1f", temperature)}°C",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Slider(
                        value = temperature.toFloat(),
                        onValueChange = { temperature = it.toDouble() },
                        valueRange = 10f..45f,
                        steps = 70,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("temp_slider")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("10°C (Cold)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                        Text("45°C (Extreme Heat)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    }
                }
            }

            // Condition Selector Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Weather Condition",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        weatherConditions.forEach { cond ->
                            val isSelected = condition == cond
                            FilterChip(
                                selected = isSelected,
                                onClick = { condition = cond },
                                label = { Text(cond) },
                                modifier = Modifier.testTag("condition_chip_$cond")
                            )
                        }
                    }
                }
            }

            // Observations Notes Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Notes & Observations",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = { Text("Log specific behaviors, breeze, rain level, sea tide observations, traffic delay etc.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("notes_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            if (showErrorMsg.isNotEmpty()) {
                Text(
                    text = showErrorMsg,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 4.dp).testTag("error_msg")
                )
            }

            Button(
                onClick = {
                    if (area.trim().isEmpty()) {
                        showErrorMsg = "Please enter an area/location name first"
                    } else if (notes.trim().isEmpty()) {
                        showErrorMsg = "Please share a quick note of your observations"
                    } else {
                        showErrorMsg = ""
                        if (isEditMode) {
                            viewModel.updateJournalEntry(
                                WeatherJournal(
                                    id = journalId ?: 0,
                                    area = area.trim(),
                                    temperature = temperature,
                                    condition = condition,
                                    notes = notes.trim(),
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                        } else {
                            viewModel.addJournalEntry(
                                area = area.trim(),
                                temp = temperature,
                                condition = condition,
                                notes = notes.trim()
                            )
                        }
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_journal_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(imageVector = Icons.Filled.Save, contentDescription = "Save Icon")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (isEditMode) "Update Log Entry" else "Save Observations", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
