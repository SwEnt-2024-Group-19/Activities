package com.android.sample.model.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ListActivityViewModel(private val repository: ActivityRepository): ViewModel() {
    private val activities_ = MutableStateFlow<List<Activity>>(emptyList())
    val todos: StateFlow<List<Activity>> = activities_.asStateFlow()

    // Selected todo, i.e the todo for the detail view
    private val selectedActivity_ = MutableStateFlow<Activity?>(null)
    open val selectedActivity: StateFlow<Activity?> = selectedActivity_.asStateFlow()

    init {
        repository.init { getActivities() }
    }

    fun getActivities() {
        repository.getActivities({ activities_.value = it },{})
    }

    fun addActivity(activity: Activity) {
        repository.addActivity(activity, {getActivities()}, {})
    }

    fun updateActivity(activity: Activity) {
        repository.updateActivity(activity, { getActivities() }, {})
    }

    fun deleteActivityById(id: String) {
        repository.deleteActivityById(id, { getActivities() }, {})
    }

    fun selectActivity(activity: Activity) {
        selectedActivity_.value = activity
    }



}