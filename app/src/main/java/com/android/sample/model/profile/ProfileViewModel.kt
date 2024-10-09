package com.android.sample.model.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

open class ProfileViewModel : ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(
                        /** TODO **/) as T
                }
            }
    }
}