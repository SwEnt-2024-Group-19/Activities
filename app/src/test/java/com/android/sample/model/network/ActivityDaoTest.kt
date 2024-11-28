package com.android.sample.model.network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.database.ActivityDao
import com.android.sample.model.activity.database.AppDatabase
import com.android.sample.resources.dummydata.activity1
import com.android.sample.resources.dummydata.activity2
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ActivityDaoTest {

  @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

  private lateinit var database: AppDatabase
  private lateinit var activityDao: ActivityDao

  @Before
  fun setUp() {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    database =
        Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    activityDao = database.activityDao()
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun insertAndRetrieveActivities() = runBlocking {
    val activity = activity1
    activityDao.insertActivities(listOf(activity))

    val retrievedActivities = activityDao.getActivitiesByIds(listOf(activity.uid))
    assertEquals(listOf(activity), retrievedActivities)
  }

  @Test
  fun clearActivitiesByStatus() = runBlocking {
    val activity1 = activity1
    val activity2 = activity2
    activity1.status = ActivityStatus.ACTIVE
    activity2.status = ActivityStatus.FINISHED
    activityDao.insertActivities(listOf(activity1, activity2))

    activityDao.clearActivitiesByStatus(ActivityStatus.ACTIVE.name)

    val remainingActivities = activityDao.getActivitiesByIds(listOf(activity1.uid, activity2.uid))
    assertEquals(listOf(activity2), remainingActivities)
  }
}
