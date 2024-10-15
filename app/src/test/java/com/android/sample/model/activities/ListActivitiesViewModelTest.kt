package com.android.sample.model.activities

import com.android.sample.model.activity.ActivitiesRepository
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.ListActivitiesViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

class ListActivitiesViewModelTest {

        private lateinit var activitiesRepository: ActivitiesRepository
        private lateinit var listActivitiesViewModel: ListActivitiesViewModel

    private val activity =
        Activity(
            title = "FOOTBALL",
            uid = "1",
            status = ActivityStatus.ACTIVE,
            location = "",
            date = Timestamp.now(),
            creator = "me",
            description = "Do something",
            placesLeft = 0,
            maxPlaces = 0,
            participants = listOf(),
            images = listOf(),
            price = 0.0
        )

        @Before
        fun setUp() {
            activitiesRepository = mock(ActivitiesRepository::class.java)
            listActivitiesViewModel = ListActivitiesViewModel(activitiesRepository)
        }

        @Test
        fun getNewUid() {
            `when`(activitiesRepository.getNewUid()).thenReturn("uid")
            assertThat(listActivitiesViewModel.getNewUid(), `is`("uid"))
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
        fun selectActivityUpdatesSelectedActivity() = runBlocking {
            listActivitiesViewModel.selectActivity(activity)
            val selected = listActivitiesViewModel.selectedActivity.first()
            assertThat(selected, `is`(activity))
        }
    }

