package com.android.sample.model.profile

interface ProfilesRepository {
  fun getUser(userId: String, onSuccess: (User?) -> Unit, onFailure: (Exception) -> Unit)
}
