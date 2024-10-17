package com.android.sample.ui.activityDetails

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.ui.navigation.NavigationActions
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class ActivityDetailsScreenAndroidTest {

  @get:Rule val composeTestRule = createComposeRule()
  private val mockViewModel = mockk<ListActivitiesViewModel>()
  private val mockNavigationActions = mock(NavigationActions::class.java)

  @Test
  fun activityTitle_isDisplayed() {
    /* val activity =
        Activity(
            uid = "1",
            title = "Sample Activity",
            description = "Sample Description",
            date = Timestamp(GregorianCalendar(2025, Calendar.NOVEMBER, 3).time),
            price = 10.0,
            placesLeft = 5,
            maxPlaces = 10,
            creator = "Creator",
            status = ActivityStatus.ACTIVE,
            location = "Sample Location",
            images = listOf("1"),
            participants = listOf())

    `when`(mockViewModel.selectedActivity).thenReturn(MutableStateFlow(activity).asStateFlow())
    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel, navigationActions = mockNavigationActions)
    }

    // Verify title is displayed
    composeTestRule.onNodeWithText("TopAppBar").assertIsDisplayed()
    composeTestRule.onNodeWithText("Title").assertIsDisplayed()
    composeTestRule.onNodeWithText("Description").assertIsDisplayed()
    composeTestRule.onNodeWithText("priceIcon").assertIsDisplayed()

    */
  }
}
