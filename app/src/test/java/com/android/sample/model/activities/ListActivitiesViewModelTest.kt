package com.android.sample.model.activities

import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.android.sample.model.activity.ActivitiesRepository
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.map.Location
import com.android.sample.model.profile.ProfilesRepository
import com.android.sample.model.profile.User
import com.android.sample.resources.dummydata.activityBiking
import com.android.sample.resources.dummydata.simpleUser
import com.android.sample.resources.dummydata.userWithActivities
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ListActivitiesViewModelTest {

  private lateinit var activitiesRepository: ActivitiesRepository
  private lateinit var listActivitiesViewModel: ListActivitiesViewModel
  private lateinit var profilesRepository: ProfilesRepository

  private val location =
      Location(46.519962, 6.633597, "EPFL", "Ecole Polytechnique Fédérale de Lausanne")
  private val activity = activityBiking

  @Before
  fun setUp() {
    activitiesRepository = mock(ActivitiesRepository::class.java)
    profilesRepository = mock(ProfilesRepository::class.java)
    listActivitiesViewModel = ListActivitiesViewModel(profilesRepository, activitiesRepository)
  }

  @Test
  fun getNewUid() {
    `when`(activitiesRepository.getNewUid()).thenReturn("1")
    assertThat(listActivitiesViewModel.getNewUid(), `is`("1"))
  }

  @Test
  fun getActivitiesCallsRepository() {
    listActivitiesViewModel.getActivities()
    verify(activitiesRepository).getActivities(any(), any())
  }

  @Test
  fun addActivitiesCallsRepository() {
    listActivitiesViewModel.addActivity(activity)
    verify(activitiesRepository).addActivity(eq(activity), any(), any())
  }

  @Test
  fun updateActivitiesCallsRepository() {
    listActivitiesViewModel.updateActivity(activity)
    verify(activitiesRepository).updateActivity(eq(activity), any(), any())
  }

  @Test
  fun deleteActivityByIdCallsRepository() {
    listActivitiesViewModel.deleteActivityById(activity.uid)
    verify(activitiesRepository).deleteActivityById(eq(activity.uid), any(), any())
  }

  @Test
  fun getActivitiesSuccessCallback() {
    val onSuccess = mock<() -> Unit>()
    listActivitiesViewModel.getActivities(onSuccess, {})
    verify(activitiesRepository).getActivities(any(), any())
  }

  @Test
  fun getActivitiesUpdatesUiStateOnSuccess() {
    val activities = listOf(activity)
    `when`(activitiesRepository.getActivities(any(), any())).thenAnswer {
      val onSuccess = it.getArgument<(List<Activity>) -> Unit>(0)
      onSuccess(activities)
    }
    listActivitiesViewModel.getActivities()
    assertThat(
        listActivitiesViewModel.uiState.value,
        `is`(ListActivitiesViewModel.ActivitiesUiState.Success(activities)))
  }

  @Test
  fun getActivitiesUpdatesUiStateOnError() {
    val exception = Exception("Test exception")
    `when`(activitiesRepository.getActivities(any(), any())).thenAnswer {
      val onFailure = it.getArgument<(Exception) -> Unit>(1)
      onFailure(exception)
    }
    listActivitiesViewModel.getActivities()
    assertThat(
        listActivitiesViewModel.uiState.value,
        `is`(ListActivitiesViewModel.ActivitiesUiState.Error(exception)))
  }

  @Test
  fun addActivityCallsGetActivitiesOnSuccess() {
    `when`(activitiesRepository.addActivity(any(), any(), any())).thenAnswer {
      val onSuccess = it.getArgument<() -> Unit>(1)
      onSuccess()
    }
    listActivitiesViewModel.addActivity(activity)
    verify(activitiesRepository).getActivities(any(), any())
  }

  @Test
  fun updateActivityCallsGetActivitiesOnSuccess() {
    FirebaseApp.initializeApp(getApplicationContext())
    `when`(activitiesRepository.updateActivity(any(), any(), any())).thenAnswer {
      val onSuccess = it.getArgument<() -> Unit>(1)
      onSuccess()
    }
    listActivitiesViewModel.updateActivity(activity)
    verify(activitiesRepository).getActivities(any(), any())
  }

  @Test
  fun deleteActivityByIdCallsGetActivitiesOnSuccess() {
    `when`(activitiesRepository.deleteActivityById(any(), any(), any())).thenAnswer {
      val onSuccess = it.getArgument<() -> Unit>(1)
      onSuccess()
    }
    listActivitiesViewModel.deleteActivityById(activity.uid)
    verify(activitiesRepository).getActivities(any(), any())
  }

  @Test
  fun selectActivityUpdatesSelectedActivity() = runBlocking {
    listActivitiesViewModel.selectActivity(activity)
    val selected = listActivitiesViewModel.selectedActivity.first()
    assertThat(selected, `is`(activity))
  }

  @Test
  fun getActivities_onSuccess() {
    val activities = listOf(activity)
    `when`(activitiesRepository.getActivities(any(), any())).thenAnswer {
      val onSuccess = it.getArgument<(List<Activity>) -> Unit>(0)
      onSuccess(activities)
    }

    runBlocking {
      listActivitiesViewModel.getActivities()
      val uiState = listActivitiesViewModel.uiState.first()
      assertThat(uiState, `is`(ListActivitiesViewModel.ActivitiesUiState.Success(activities)))
    }
  }

  @Test
  fun getActivities_onFailure() {
    val exception = Exception("Error")
    `when`(activitiesRepository.getActivities(any(), any())).thenAnswer {
      val onFailure = it.getArgument<(Exception) -> Unit>(1)
      onFailure(exception)
    }

    runBlocking {
      listActivitiesViewModel.getActivities()
      val uiState = listActivitiesViewModel.uiState.first()
      assertThat(uiState, `is`(ListActivitiesViewModel.ActivitiesUiState.Error(exception)))
    }
  }

  @Test
  fun activitiesUiState_Success() {
    val activities = listOf(activity)
    val successState = ListActivitiesViewModel.ActivitiesUiState.Success(activities)
    assertThat(successState.activities, `is`(activities))
  }

  @Test
  fun activitiesUiState_Error() {
    val exception = Exception("Error")
    val errorState = ListActivitiesViewModel.ActivitiesUiState.Error(exception)
    assertThat(errorState.exception, `is`(exception))
  }

  @Test
  fun `should update activity with new likes when review is added`() {
    FirebaseApp.initializeApp(getApplicationContext())
    val initialLikes = mapOf("user1" to true, "user2" to false, "user3" to null)
    val activity = activityBiking.copy(likes = initialLikes)
    val userId = "user3"
    val review = true

    val expectedLikes = initialLikes.plus(userId to review)
    val expectedActivity = activity.copy(likes = expectedLikes)
    var capturedActivity: Activity? = null

    `when`(activitiesRepository.updateActivity(any(), any(), any())).thenAnswer {
      capturedActivity = it.getArgument(0)
      it.getArgument<() -> Unit>(1).invoke()
    }

    listActivitiesViewModel.reviewActivity(activity, userId, review)
    verify(activitiesRepository).updateActivity(any(), any(), any())

    assertEquals(expectedActivity, capturedActivity)
  }

  @Test
  fun `verify initial fetch of activities`() = runBlocking {
    val dummyActivities = listOf(activityBiking)
    `when`(activitiesRepository.getActivities(any(), any())).thenAnswer {
      val onSuccess = it.arguments[0] as (List<Activity>) -> Unit
      onSuccess(dummyActivities)
    }

    listActivitiesViewModel.getActivities()

    val uiState = listActivitiesViewModel.uiState.first()
    assertThat(uiState is ListActivitiesViewModel.ActivitiesUiState.Success, `is`(true))
    val successState = uiState as ListActivitiesViewModel.ActivitiesUiState.Success
    assertEquals(dummyActivities, successState.activities)
  }

  @Test
  fun `verify addActivity updates state`() = runBlocking {
    val dummyActivity = activityBiking
    `when`(activitiesRepository.addActivity(eq(dummyActivity), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as () -> Unit
      onSuccess()
    }

    listActivitiesViewModel.addActivity(dummyActivity)

    verify(activitiesRepository).addActivity(eq(dummyActivity), any(), any())
    val uiState = listActivitiesViewModel.uiState.first()
    assertThat(uiState is ListActivitiesViewModel.ActivitiesUiState.Success, `is`(true))
  }

  @Test
  fun `verify filters update ViewModel state`() {
    listActivitiesViewModel.updateFilterState(
        price = 50.0,
        placesAvailable = 5,
        minDateTimestamp = Timestamp(1635678900L, 0),
        maxDateTimestamp = Timestamp(1638270900L, 0),
        startTime = "08:00",
        endTime = "20:00",
        distance = 15.0,
        seeOnlyPRO = true)

    assertThat(listActivitiesViewModel.maxPrice, `is`(50.0))
    assertThat(listActivitiesViewModel.availablePlaces, `is`(5))
    assertThat(listActivitiesViewModel.onlyPRO, `is`(true))
  }

  @Test
  fun addActivitySchedulesNotificationOnSuccess() {
    val activity = activityBiking
    `when`(activitiesRepository.addActivity(any(), any(), any())).thenAnswer {
      val onSuccess = it.getArgument<() -> Unit>(1)
      onSuccess()
    }
    `when`(profilesRepository.getUser(any(), any(), any())).thenAnswer {
      val onSuccess = it.getArgument<(User?) -> Unit>(1)
      onSuccess(simpleUser)
    }

    listActivitiesViewModel.addActivity(activity)

    verify(activitiesRepository).addActivity(eq(activity), any(), any())
  }

  @Test
  fun addActivityLogsErrorOnFailure() {
    val activity = activityBiking
    val error = Exception("Failed to add activity")
    `when`(activitiesRepository.addActivity(any(), any(), any())).thenAnswer {
      val onFailure = it.getArgument<(Exception) -> Unit>(2)
      onFailure(error)
    }

    listActivitiesViewModel.addActivity(activity)

    verify(activitiesRepository).addActivity(eq(activity), any(), any())
    // Verify that the error is logged (you might need to use a logging framework or mock it)
  }

  @Test
  fun updateActivityLogsErrorOnFailure() {
    val activity = activityBiking
    val error = Exception("Failed to update activity")
    `when`(activitiesRepository.updateActivity(any(), any(), any())).thenAnswer {
      val onFailure = it.getArgument<(Exception) -> Unit>(2)
      onFailure(error)
    }

    listActivitiesViewModel.updateActivity(activity)

    verify(activitiesRepository).updateActivity(eq(activity), any(), any())
    // Verify that the error is logged (you might need to use a logging framework or mock it)
  }

  @Test
  fun updateActivitySchedulesNotificationOnSuccess() {
    FirebaseApp.initializeApp(getApplicationContext())
    val activity = activityBiking
    `when`(activitiesRepository.updateActivity(any(), any(), any())).thenAnswer {
      val onSuccess = it.getArgument<() -> Unit>(1)
      onSuccess()
    }
    `when`(profilesRepository.getUser(any(), any(), any())).thenAnswer {
      val onSuccess = it.getArgument<(User?) -> Unit>(1)
      onSuccess(simpleUser)
    }

    listActivitiesViewModel.updateActivity(activity)

    verify(activitiesRepository).updateActivity(eq(activity), any(), any())
  }

  @Test
  fun calculateParticipationScoreReturnsZeroWhenUserActivitiesIsNull() {
    val result = listActivitiesViewModel.calculateParticipationScore(null, "creatorId")
    assertThat(result, `is`(0.0))
  }

  @Test
  fun calculateParticipationScoreReturnsZeroWhenUserActivitiesIsEmpty() {
    val result = listActivitiesViewModel.calculateParticipationScore(emptyList(), "creatorId")
    assertThat(result, `is`(0.0))
  }

  @Test
  fun calculateParticipationScoreReturnsZeroWhenCreatorIsEmpty() {
    val result = listActivitiesViewModel.calculateParticipationScore(listOf("activity1"), "")
    assertThat(result, `is`(0.0))
  }

  @Test
  fun calculateParticipationScoreReturnsCorrectScoreWhenMatchingActivitiesExist() {
    val userActivities = listOf("activity1", "activity2", "activity3")
    val creator = userWithActivities
    `when`(profilesRepository.getUser(any(), any(), any())).thenAnswer {
      val onSuccess = it.getArgument<(User?) -> Unit>(1)
      onSuccess(creator)
    }

    val result = listActivitiesViewModel.calculateParticipationScore(userActivities, "creatorId")
    assertThat(result, `is`(0.3)) // 2 matching activities / 10
  }

  @Test
  fun calculateParticipationScoreReturnsZeroWhenNoMatchingActivitiesExist() {
    val userActivities = listOf("activity1", "activity2", "activity3")
    val creator = userWithActivities.copy(activities = listOf("activity4", "activity5"))
    `when`(profilesRepository.getUser(any(), any(), any())).thenAnswer {
      val onSuccess = it.getArgument<(User?) -> Unit>(1)
      onSuccess(creator)
    }

    val result = listActivitiesViewModel.calculateParticipationScore(userActivities, "creatorId")
    assertThat(result, `is`(0.0))
  }

  @Test
  fun `verify deleteActivity updates state`() = runBlocking {
    val dummyActivity = activityBiking
    `when`(activitiesRepository.deleteActivityById(eq(dummyActivity.uid), any(), any()))
        .thenAnswer {
          val onSuccess = it.arguments[1] as () -> Unit
          onSuccess()
        }

    listActivitiesViewModel.deleteActivityById(dummyActivity.uid)

    verify(activitiesRepository).deleteActivityById(eq(dummyActivity.uid), any(), any())
    val uiState = listActivitiesViewModel.uiState.first()
    assertThat(uiState is ListActivitiesViewModel.ActivitiesUiState.Success, `is`(true))
  }

  @Test
  fun `verify reviewActivity updates state`() = runBlocking {
    val dummyActivity = activityBiking
    val dummyUserId = "user1"
    val dummyReview = true
    `when`(activitiesRepository.updateActivity(any(), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as () -> Unit
      onSuccess()
    }

    listActivitiesViewModel.reviewActivity(dummyActivity, dummyUserId, dummyReview)

    verify(activitiesRepository).updateActivity(any(), any(), any())
    val uiState = listActivitiesViewModel.uiState.first()
    assertThat(uiState is ListActivitiesViewModel.ActivitiesUiState.Success, `is`(true))
  }

  @Test
  fun `verify selectActivity updates state`() = runBlocking {
    val dummyActivity = activityBiking
    listActivitiesViewModel.selectActivity(dummyActivity)

    val selectedActivity = listActivitiesViewModel.selectedActivity.first()
    assertThat(selectedActivity, `is`(dummyActivity))
  }

  @Test
  fun `verify getNewUid returns correct value`() {
    val expectedUid = "1"
    `when`(activitiesRepository.getNewUid()).thenReturn(expectedUid)

    val newUid = listActivitiesViewModel.getNewUid()
    assertThat(newUid, `is`(expectedUid))
  }

  @Test
  fun `verify getActivities calls repository`() {
    listActivitiesViewModel.getActivities()
    verify(activitiesRepository).getActivities(any(), any())
  }

  @Test
  fun `verify addActivity calls repository`() {
    val dummyActivity = activityBiking
    listActivitiesViewModel.addActivity(dummyActivity)
    verify(activitiesRepository).addActivity(eq(dummyActivity), any(), any())
  }

  @Test
  fun `verify updateActivity calls repository`() {
    val dummyActivity = activityBiking
    listActivitiesViewModel.updateActivity(dummyActivity)
    verify(activitiesRepository).updateActivity(eq(dummyActivity), any(), any())
  }

  @Test
  fun createActivityButtonDisabledWhenTitleIsEmpty() {
    val title = ""
    val description = "Valid Description"
    val price = "10.0"
    val placesMax = "5"
    val selectedLocation = "Valid Location"
    val selectedOptionType = "Valid Type"
    val selectedOptionCategory = "Valid Category"
    val startTime = "10:00"
    val duration = "1:00"
    val dueDate = Timestamp.now().apply { toDate().time += 10000 }

    val isEnabled =
        listActivitiesViewModel.isButtonEnabled(
            title,
            description,
            price,
            placesMax,
            selectedOptionCategory,
            selectedLocation,
            selectedOptionType,
            startTime,
            duration,
            dueDate)

    assertThat(isEnabled, `is`(false))
  }

  @Test
  fun createActivityButtonDisabledWhenDescriptionIsEmpty() {
    val title = "Valid Title"
    val description = ""
    val price = "10.0"
    val placesMax = "5"
    val selectedLocation = "Valid Location"
    val selectedOptionType = "Valid Type"
    val selectedOptionCategory = "Valid Category"
    val startTime = "10:00"
    val duration = "1:00"
    val dueDate = Timestamp.now().apply { toDate().time += 10000 }

    val isEnabled =
        listActivitiesViewModel.isButtonEnabled(
            title,
            description,
            price,
            placesMax,
            selectedOptionCategory,
            selectedLocation,
            selectedOptionType,
            startTime,
            duration,
            dueDate)

    assertThat(isEnabled, `is`(false))
  }

  @Test
  fun createActivityButtonDisabledWhenPriceIsEmpty() {
    val title = "Valid Title"
    val description = "Valid Description"
    val price = ""
    val placesMax = "5"
    val selectedLocation = "Valid Location"
    val selectedOptionType = "Valid Type"
    val selectedOptionCategory = "Valid Category"
    val startTime = "10:00"
    val duration = "1:00"
    val dueDate = Timestamp.now().apply { toDate().time += 10000 }

    val isEnabled =
        listActivitiesViewModel.isButtonEnabled(
            title,
            description,
            price,
            placesMax,
            selectedOptionCategory,
            selectedLocation,
            selectedOptionType,
            startTime,
            duration,
            dueDate)

    assertThat(isEnabled, `is`(false))
  }

  @Test
  fun createActivityButtonDisabledWhenPlacesMaxIsEmpty() {
    val title = "Valid Title"
    val description = "Valid Description"
    val price = "10.0"
    val placesMax = ""
    val selectedLocation = "Valid Location"
    val selectedOptionType = "Valid Type"
    val selectedOptionCategory = "Valid Category"
    val startTime = "10:00"
    val duration = "1:00"
    val dueDate = Timestamp.now().apply { toDate().time += 10000 }

    val isEnabled =
        listActivitiesViewModel.isButtonEnabled(
            title,
            description,
            price,
            placesMax,
            selectedOptionCategory,
            selectedLocation,
            selectedOptionType,
            startTime,
            duration,
            dueDate)

    assertThat(isEnabled, `is`(false))
  }

  @Test
  fun createActivityButtonDisabledWhenSelectedLocationIsNull() {
    val title = "Valid Title"
    val description = "Valid Description"
    val price = "10.0"
    val placesMax = "5"
    val selectedLocation = null
    val selectedOptionType = "Valid Type"
    val selectedOptionCategory = "Valid Category"
    val startTime = "10:00"
    val duration = "1:00"
    val dueDate = Timestamp.now().apply { toDate().time += 10000 }

    val isEnabled =
        listActivitiesViewModel.isButtonEnabled(
            title,
            description,
            price,
            placesMax,
            selectedOptionCategory,
            selectedLocation,
            selectedOptionType,
            startTime,
            duration,
            dueDate)

    assertThat(isEnabled, `is`(false))
  }

  @Test
  fun createActivityButtonDisabledWhenSelectedOptionTypeIsInvalid() {
    val title = "Valid Title"
    val description = "Valid Description"
    val price = "10.0"
    val placesMax = "5"
    val selectedLocation = "Valid Location"
    val selectedOptionType = "Select a type"
    val selectedOptionCategory = "Valid Category"
    val startTime = "10:00"
    val duration = "1:00"
    val dueDate = Timestamp.now().apply { toDate().time += 10000 }

    val isEnabled =
        listActivitiesViewModel.isButtonEnabled(
            title,
            description,
            price,
            placesMax,
            selectedOptionCategory,
            selectedLocation,
            selectedOptionType,
            startTime,
            duration,
            dueDate)

    assertThat(isEnabled, `is`(false))
  }

  @Test
  fun createActivityButtonDisabledWhenSelectedOptionCategoryIsNull() {
    val title = "Valid Title"
    val description = "Valid Description"
    val price = "10.0"
    val placesMax = "5"
    val selectedLocation = "Valid Location"
    val selectedOptionType = "Valid Type"
    val selectedOptionCategory = null
    val startTime = "10:00"
    val duration = "1:00"
    val dueDate = Timestamp.now().apply { toDate().time += 10000 }

    val isEnabled =
        listActivitiesViewModel.isButtonEnabled(
            title,
            description,
            price,
            placesMax,
            selectedOptionCategory,
            selectedLocation,
            selectedOptionType,
            startTime,
            duration,
            dueDate)

    assertThat(isEnabled, `is`(false))
  }

  @Test
  fun createActivityButtonDisabledWhenStartTimeIsEmpty() {
    val title = "Valid Title"
    val description = "Valid Description"
    val price = "10.0"
    val placesMax = "5"
    val selectedLocation = "Valid Location"
    val selectedOptionType = "Valid Type"
    val selectedOptionCategory = "Valid Category"
    val startTime = ""
    val duration = "1:00"
    val dueDate = Timestamp.now().apply { toDate().time += 10000 }

    val isEnabled =
        listActivitiesViewModel.isButtonEnabled(
            title,
            description,
            price,
            placesMax,
            selectedOptionCategory,
            selectedLocation,
            selectedOptionType,
            startTime,
            duration,
            dueDate)

    assertThat(isEnabled, `is`(false))
  }

  @Test
  fun createActivityButtonDisabledWhenDurationIsEmpty() {
    val title = "Valid Title"
    val description = "Valid Description"
    val price = "10.0"
    val placesMax = "5"
    val selectedLocation = "Valid Location"
    val selectedOptionType = "Valid Type"
    val selectedOptionCategory = "Valid Category"
    val startTime = "10:00"
    val duration = ""
    val dueDate = Timestamp.now().apply { toDate().time += 10000 }

    val isEnabled =
        listActivitiesViewModel.isButtonEnabled(
            title,
            description,
            price,
            placesMax,
            selectedOptionCategory,
            selectedLocation,
            selectedOptionType,
            startTime,
            duration,
            dueDate)

    assertThat(isEnabled, `is`(false))
  }

  @Test
  fun createActivityButtonDisabledWhenDueDateIsInPast() {
    val title = "Valid Title"
    val description = "Valid Description"
    val price = "10.0"
    val placesMax = "5"
    val selectedLocation = "Valid Location"
    val selectedOptionType = "Valid Type"
    val selectedOptionCategory = "Valid Category"
    val startTime = "10:00"
    val duration = "1:00"
    val dueDate = Timestamp(0, 0)

    val isEnabled =
        listActivitiesViewModel.isButtonEnabled(
            title,
            description,
            price,
            placesMax,
            selectedOptionCategory,
            selectedLocation,
            selectedOptionType,
            startTime,
            duration,
            dueDate)

    assertThat(isEnabled, `is`(false))
  }
}
