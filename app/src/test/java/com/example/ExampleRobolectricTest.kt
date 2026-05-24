package com.example

import android.content.Context
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule
  val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Weather Forecast", appName)
  }

  @Test
  fun `full navigation and screen flow test`() {
    // 1. App starts on Splash screen. Verify it exists
    composeTestRule.onNodeWithTag("splash_screen").assertExists()

    // 2. Wait for Splash screen delay to finish and trigger navigation to Home
    composeTestRule.mainClock.advanceTimeBy(3000L)
    ShadowLooper.idleMainLooper(3000, TimeUnit.MILLISECONDS)
    composeTestRule.waitForIdle()

    // Verify we arrived on Home screen
    composeTestRule.onNodeWithTag("home_screen_content").assertExists()
    composeTestRule.onNodeWithTag("current_weather_card").assertExists()

    // 3. Navigate to About Screen
    composeTestRule.onNodeWithTag("info_button").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("about_coords_card").assertExists()

    // Go back using about back button
    composeTestRule.onNodeWithTag("about_back").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("home_screen_content").assertExists()

    // 4. Navigate to Settings / Config screen
    composeTestRule.onNodeWithTag("config_button").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("config_back").assertExists()

    // Go back using config back button
    composeTestRule.onNodeWithTag("config_back").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("home_screen_content").assertExists()

    // 5. Navigate to Add Journal screen
    composeTestRule.onNodeWithTag("add_journal_fab").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("back_button").assertExists()

    // Go back using back button
    composeTestRule.onNodeWithTag("back_button").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("home_screen_content").assertExists()
  }
}


