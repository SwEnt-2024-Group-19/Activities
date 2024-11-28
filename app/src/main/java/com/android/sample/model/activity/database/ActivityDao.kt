package com.android.sample.model.activity.database

import androidx.room.*
import com.android.sample.model.activity.Activity

@Dao
interface ActivityDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertActivities(activities: List<Activity>)

  @Query("SELECT * FROM activities WHERE uid IN (:ids)")
  suspend fun getActivitiesByIds(ids: List<String>): List<Activity>

  @Query("DELETE FROM activities WHERE status = :status")
  suspend fun clearActivitiesByStatus(status: String)
}
