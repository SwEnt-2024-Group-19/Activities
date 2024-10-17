package com.android.sample.ui.activity /*
                                       import androidx.compose.ui.test.assertIsDisplayed
                                       import androidx.compose.ui.test.assertTextContains
                                       import androidx.compose.ui.test.assertTextEquals
                                       import androidx.compose.ui.test.junit4.createComposeRule
                                       import androidx.compose.ui.test.onNodeWithTag
                                       import androidx.compose.ui.test.performClick
                                       import com.android.sample.model.activity.ActivitiesRepository
                                       import com.android.sample.model.activity.Activity
                                       import com.android.sample.model.activity.ActivityStatus
                                       import com.android.sample.model.activity.ListActivitiesViewModel
                                       import com.android.sample.ui.navigation.NavigationActions
                                       import com.android.sample.ui.navigation.Screen
                                       import com.google.firebase.Timestamp
                                       import io.mockk.mockk
                                       import org.junit.Before
                                       import org.junit.Rule
                                       import org.junit.Test
                                       import org.mockito.Mockito.`when`

                                       class EditActivityScreenTest {
                                         private lateinit var activitiesRepository: ActivitiesRepository
                                         private lateinit var navigationActions: NavigationActions
                                         private lateinit var listActivitiesViewModel: ListActivitiesViewModel

                                         @get:Rule val composeTestRule = createComposeRule()

                                         private val activity =
                                           Activity(
                                             "1",
                                             "First Activity",
                                             "Do something",
                                             creator = "John Doe",
                                             date = Timestamp.now(),
                                             location = "EPFL",
                                             status = ActivityStatus.ACTIVE,
                                             participants = listOf(),
                                             price = 10.0,
                                             placesLeft = 10,
                                             maxPlaces = 20,
                                             images = listOf("image1", "image2"))

                                         @Before
                                         fun setUp() {
                                           activitiesRepository = mockk<ActivitiesRepository>(relaxed = true)
                                           navigationActions = mockk(relaxed = true)
                                           listActivitiesViewModel = ListActivitiesViewModel(activitiesRepository)
                                           listActivitiesViewModel.selectActivity(activity)

                                           `when`(navigationActions.currentRoute()).thenReturn(Screen.EDIT_ACTIVITY)
                                         }

                                         @Test
                                         fun displayAllComponents() {
                                           listActivitiesViewModel.selectActivity(activity)
                                           composeTestRule.setContent { EditActivityScreen(listActivitiesViewModel, navigationActions) }

                                           composeTestRule.onNodeWithTag("editScreen").assertIsDisplayed()
                                           composeTestRule.onNodeWithTag("editActivityTitle").assertIsDisplayed()
                                           composeTestRule.onNodeWithTag("editActivityTitle").assertTextEquals("Edit Task")
                                           composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
                                           composeTestRule.onNodeWithTag("activitySave").assertIsDisplayed()
                                           composeTestRule.onNodeWithTag("activitySave").assertTextEquals("Save")
                                           composeTestRule.onNodeWithTag("activityDelete").assertIsDisplayed()
                                           composeTestRule.onNodeWithTag("activityDelete").assertTextEquals("Delete")

                                           composeTestRule.onNodeWithTag("inputTitleEdit").assertIsDisplayed()
                                           composeTestRule.onNodeWithTag("inputDescriptionEdit").assertIsDisplayed()
                                           composeTestRule.onNodeWithTag("inputAssigneeEdit").assertIsDisplayed()
                                           composeTestRule.onNodeWithTag("inputLocationEdit").assertIsDisplayed()
                                           composeTestRule.onNodeWithTag("inputDateEdit").assertIsDisplayed()
                                           composeTestRule.onNodeWithTag("inputStatusEdit").assertIsDisplayed()
                                         }

                                         @Test
                                         fun inputsHaveInitialValue() {
                                           listActivitiesViewModel.selectActivity(activity)
                                           composeTestRule.setContent { EditActivityScreen(listActivitiesViewModel, navigationActions) }

                                           composeTestRule.waitForIdle()

                                           composeTestRule.onNodeWithTag("inputTitleEdit").assertTextContains(activity.title)
                                           composeTestRule.onNodeWithTag("inputDescriptionEdit").assertTextContains(activity.description)
                                           composeTestRule.onNodeWithTag("inputAssigneeEdit").assertTextContains(activity.creator)
                                           composeTestRule.onNodeWithTag("inputDateEdit").assertTextContains("5/9/2024")
                                           composeTestRule.onNodeWithTag("inputStatusEdit").assertTextContains("Active")
                                         }

                                         @Test
                                         fun correctlyIteratesStatuses() {
                                           listActivitiesViewModel.selectActivity(activity)
                                           composeTestRule.setContent { EditActivityScreen(listActivitiesViewModel, navigationActions) }
                                           composeTestRule.onNodeWithTag("inputStatusEdit").assertTextContains("Active")

                                           composeTestRule.onNodeWithTag("inputStatusEdit").performClick()
                                           composeTestRule.onNodeWithTag("inputStatusEdit").assertTextContains("Started")

                                           composeTestRule.onNodeWithTag("inputStatusEdit").performClick()
                                           composeTestRule.onNodeWithTag("inputStatusEdit").assertTextContains("Ended")

                                           composeTestRule.onNodeWithTag("inputStatusEdit").performClick()
                                           composeTestRule.onNodeWithTag("inputStatusEdit").assertTextContains("Archived")
                                         }
                                       }*/
