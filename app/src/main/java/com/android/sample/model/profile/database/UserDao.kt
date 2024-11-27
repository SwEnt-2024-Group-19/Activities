package com.android.sample.model.profile.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.sample.model.profile.User

@Dao
interface UserDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(user: User)

  @Query("SELECT * FROM User WHERE id = :userId LIMIT 1") suspend fun getUser(userId: String): User?

  @Query("DELETE FROM User") suspend fun clear()
}
