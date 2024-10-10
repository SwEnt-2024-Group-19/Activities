package com.android.sample.model

interface ProfilesRepository {
  fun getUser(userId: String, onSuccess: (User?) -> Unit, onFailure: (Exception) -> Unit)
}
