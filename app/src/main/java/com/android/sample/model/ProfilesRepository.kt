package com.android.sample.model

import kotlinx.coroutines.flow.Flow

interface ProfilesRepository {
    fun getUser(userId: String, onSuccess: (User?) -> Unit, onFailure: (Exception) -> Unit)
}
