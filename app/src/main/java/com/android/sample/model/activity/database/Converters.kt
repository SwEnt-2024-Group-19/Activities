package com.android.sample.model.activity.database

import androidx.room.TypeConverter
import com.android.sample.model.activity.Comment
import com.android.sample.model.map.Location
import com.android.sample.model.profile.Interest
import com.android.sample.model.profile.User
import com.google.firebase.Timestamp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

  @TypeConverter
  fun fromTimestamp(value: Timestamp?): Long? {
    return value?.seconds
  }

  @TypeConverter
  fun toTimestamp(value: Long?): Timestamp? {
    return value?.let { Timestamp(it, 0) }
  }

  @TypeConverter
  fun fromLocation(location: Location?): String? {
    return Gson().toJson(location)
  }

  @TypeConverter
  fun toLocation(location: String?): Location? {
    return location?.let { Gson().fromJson(it, Location::class.java) }
  }

  @TypeConverter
  fun fromUserList(users: List<User>?): String? {
    return Gson().toJson(users)
  }

  @TypeConverter
  fun toUserList(users: String?): List<User>? {
    return users?.let {
      val type = object : TypeToken<List<User>>() {}.type
      Gson().fromJson(it, type)
    }
  }

  @TypeConverter
  fun fromList(value: List<String>?): String {
    return Gson().toJson(value)
  }

  @TypeConverter
  fun toList(value: String): List<String>? {
    val listType = object : TypeToken<List<String>>() {}.type
    return Gson().fromJson(value, listType)
  }

  @TypeConverter
  fun fromCommentList(comments: List<Comment>?): String {
    return Gson().toJson(comments)
  }

  @TypeConverter
  fun toCommentList(value: String): List<Comment>? {
    val listType = object : TypeToken<List<Comment>>() {}.type
    return Gson().fromJson(value, listType)
  }

  @TypeConverter
  fun fromInterestList(interests: List<Interest>?): String {
    return Gson().toJson(interests)
  }

  @TypeConverter
  fun toInterestList(value: String): List<Interest>? {
    val listType = object : TypeToken<List<Interest>>() {}.type
    return Gson().fromJson(value, listType)
  }
}
