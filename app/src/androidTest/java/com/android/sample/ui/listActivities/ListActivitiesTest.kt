package com.android.sample.ui.listActivities

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.sample.R
import com.android.sample.model.activity.ActivitiesRepository
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.google.firebase.Timestamp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

class OverviewScreenTest {
  private lateinit var activitiesRepository: ActivitiesRepository
  private lateinit var navigationActions: NavigationActions
  private lateinit var listActivitiesViewModel: ListActivitiesViewModel

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    activitiesRepository = mock(ActivitiesRepository::class.java)
    navigationActions = mock(NavigationActions::class.java)
    listActivitiesViewModel = ListActivitiesViewModel(activitiesRepository)

    `when`(navigationActions.currentRoute()).thenReturn(Route.OVERVIEW)
    composeTestRule.setContent { ListActivitiesScreen(listActivitiesViewModel, navigationActions) }
  }

  @Test
  fun displayTextWhenEmpty() {
    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(listOf())
    }
    listActivitiesViewModel.getActivities()

    composeTestRule.onNodeWithTag("emptyActivityPrompt").assertIsDisplayed()
  }

  @Test
  fun hasRequiredComponents() {
    composeTestRule.onNodeWithTag("listActivitiesScreen").assertIsDisplayed()
    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(
          listOf(
              Activity(
                  uid = "3",
                  name = "Fun Farm",
                  description = "Come discover the new farm and enjoy with your family!",
                  date = Timestamp.now(),
                  location = "Lausanne",
                  organizerName = "Rola",
                  image = R.drawable.farm.toLong(),
                  20,
                  22)))
    }
    listActivitiesViewModel.getActivities()
    composeTestRule.onNodeWithTag("activityCard").assertIsDisplayed()
  }
}
