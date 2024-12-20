package com.android.sample.model.activity.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.android.sample.model.activity.Activity
import com.android.sample.model.profile.User
import com.android.sample.model.profile.database.UserDao

@Database(entities = [User::class, Activity::class], version = 5)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun activityDao(): ActivityDao

  abstract fun userDao(): UserDao
}
