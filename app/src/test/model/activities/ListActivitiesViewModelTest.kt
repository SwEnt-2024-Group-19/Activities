package com.android.sample.model.activities

import com.android.sample.R
import com.android.sample.model.activity.ActivitiesRepository
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ListActivitiesViewModel
import com.google.firebase.Timestamp
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any


class ListToDosViewModelTest {
  private lateinit var activitiesRepository: ActivitiesRepository
  private lateinit var listActivitiesViewModel: ListActivitiesViewModel

  val activity =
      Activity(
          uid = "3",
          name = "Fun Farm",
          description = "Come discover the new farm and enjoy with your family!",
          date = Timestamp.now(),
          location = "Lausanne",
          organizerName = "Rola",
          image = R.drawable.farm.toLong(),
          20,
          22)

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
}
