package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.WeatherViewModel
import com.example.ui.WeatherViewModelFactory
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.AddEditJournalScreen
import com.example.ui.screens.EditConfigScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory((application as WeatherApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "splash"
                    ) {
                        composable("splash") {
                            SplashScreen(navController = navController)
                        }
                        composable("home") {
                            HomeScreen(
                                navController = navController,
                                viewModel = viewModel
                            )
                        }
                        composable("edit_config") {
                            EditConfigScreen(
                                navController = navController,
                                viewModel = viewModel
                            )
                        }
                        composable(
                            route = "add_edit_journal?id={id}",
                            arguments = listOf(
                                navArgument("id") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                }
                            )
                        ) { backStackEntry ->
                            val idParam = backStackEntry.arguments?.getString("id")
                            val id = idParam?.toIntOrNull()
                            AddEditJournalScreen(
                                navController = navController,
                                viewModel = viewModel,
                                journalId = id
                            )
                        }
                        composable("about") {
                            AboutScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}
