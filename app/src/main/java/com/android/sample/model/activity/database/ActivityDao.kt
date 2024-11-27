package com.android.sample.model.activity.database

import androidx.room.*
import com.android.sample.model.activity.Activity

@Dao
interface ActivityDao {

  // Retrieve all activities from the database
  @Query("SELECT * FROM activities") fun getAllActivities(): List<Activity>

  // Retrieve a specific activity by ID
  @Query("SELECT * FROM activities WHERE uid = :uid") fun getActivityById(uid: String): Activity?

  // Insert a list of activities (bulk insert), replacing any conflicts
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertActivities(activities: List<Activity>)

  // Delete all activities
  @Query("DELETE FROM activities") suspend fun clearActivities()
}
