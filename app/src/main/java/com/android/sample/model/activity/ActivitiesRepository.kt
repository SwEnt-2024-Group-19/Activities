package com.android.sample.model.activity

interface ActivitiesRepository {
  /**
   * Generates a new unique identifier.
   *
   * @return A new unique identifier as a String.
   */
  fun getNewUid(): String

  /**
   * Initializes the repository.
   *
   * @param onSuccess Callback function to be invoked upon successful initialization.
   */
  fun init(onSuccess: () -> Unit)

  /**
   * Retrieves a list of activities.
   *
   * @param onSuccess Callback function to be invoked with the list of activities upon success.
   * @param onFailure Callback function to be invoked with an exception upon failure.
   */
  fun getActivities(onSuccess: (List<Activity>) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Adds a new activity to the repository.
   *
   * @param activity The activity to be added.
   * @param onSuccess Callback function to be invoked upon successful addition.
   * @param onFailure Callback function to be invoked with an exception upon failure.
   */
  fun addActivity(activity: Activity, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Updates an existing activity in the repository.
   *
   * @param activity The activity to be updated.
   * @param onSuccess Callback function to be invoked upon successful update.
   * @param onFailure Callback function to be invoked with an exception upon failure.
   */
  fun updateActivity(activity: Activity, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Deletes an activity from the repository by its identifier.
   *
   * @param id The identifier of the activity to be deleted.
   * @param onSuccess Callback function to be invoked upon successful deletion.
   * @param onFailure Callback function to be invoked with an exception upon failure.
   */
  fun deleteActivityById(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
