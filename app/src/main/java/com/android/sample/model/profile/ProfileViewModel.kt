package com.android.sample.model.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class ProfileViewModel(private val repository: ProfilesRepository, userId: String) :
    ViewModel() {
    private var userState_ = MutableStateFlow<User?>(null)
    open val userState: StateFlow<User?> = userState_.asStateFlow()

    init {
        fetchUserData(userId)
    }

    fun fetchUserData(userId: String) {
        repository.getUser(
            userId,
            onSuccess = { userState_.value = it },
            onFailure = { Log.e("error", " not fetching") })
    }

    companion object {
        fun Factory(uid: String): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(
                            ProfilesRepositoryFirestore(
                                Firebase.firestore), uid) as T
                }
            }
    }
}
