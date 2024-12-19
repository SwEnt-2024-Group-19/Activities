package com.android.sample.model.activity

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.sample.App
import com.android.sample.R
import com.android.sample.model.hour_date.HourDateViewModel
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.map.Location
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.ProfilesRepository
import com.android.sample.model.profile.User
import com.android.sample.model.profile.categoryOf
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
open class ListActivitiesViewModel
@Inject
constructor(
    private val profilesRepository: ProfilesRepository,
    private val repository: ActivitiesRepository,
) : ViewModel() {

  private val selectedActivity_ = MutableStateFlow<Activity?>(null)
  open val selectedActivity: StateFlow<Activity?> = selectedActivity_.asStateFlow()

  private val selectedUser_ = MutableStateFlow<User?>(null)
  open val selectedUser: StateFlow<User?> = selectedUser_.asStateFlow()

  private val _uiState = MutableStateFlow<ActivitiesUiState>(ActivitiesUiState.Success(emptyList()))
  open val uiState: StateFlow<ActivitiesUiState> = _uiState

  // Filter state variables
  var maxPrice by mutableStateOf(Double.MAX_VALUE)
  var availablePlaces by mutableStateOf<Int?>(null)
  var minDate by mutableStateOf<Timestamp?>(null)
  var maxDate by mutableStateOf<Timestamp?>(null)

  var startTime by mutableStateOf<String?>(null)
  var endTime by mutableStateOf<String?>(null)
  var distance by mutableStateOf<Double?>(null)
  var onlyPRO by mutableStateOf(false)

  /** Set the UI state to a new value For testing purposes only */
  open fun setUiState(state: ActivitiesUiState) {
    _uiState.value = state
  }

  private val cachedScores_ = mutableMapOf<String, Double>()
  open val cachedScores = cachedScores_

  init {
    repository.init { viewModelScope.launch { getActivities() } }
  }

  /**
   * Updates the filter state with the provided parameters.
   *
   * @param price The maximum price for filtering activities. If null, defaults to Double.MAX_VALUE.
   * @param placesAvailable The number of available places for filtering activities.
   * @param minDateTimestamp The minimum date for filtering activities.
   * @param maxDateTimestamp The maximum date for filtering activities.
   * @param startTime The start time for filtering activities.
   * @param endTime The end time for filtering activities.
   * @param distance The maximum distance for filtering activities.
   * @param seeOnlyPRO A boolean indicating whether to filter only PRO activities. If null, defaults
   *   to false.
   */
  fun updateFilterState(
      price: Double?,
      placesAvailable: Int?,
      minDateTimestamp: Timestamp?,
      maxDateTimestamp: Timestamp?,
      startTime: String?,
      endTime: String?,
      distance: Double?,
      seeOnlyPRO: Boolean?
  ) {
    maxPrice = price ?: Double.MAX_VALUE
    availablePlaces = placesAvailable
    minDate = minDateTimestamp
    maxDate = maxDateTimestamp
    this.startTime = startTime
    this.endTime = endTime
    this.distance = distance
    onlyPRO = seeOnlyPRO ?: false
  }

  /** Generates a new unique identifier. */
  fun getNewUid(): String {
    return repository.getNewUid()
  }

  /**
   * Adds a new activity to the repository.
   *
   * @param activity The activity to be added.
   */
  fun addActivity(activity: Activity) {
    repository.addActivity(
        activity,
        {
          getActivities()
          viewModelScope.launch {
            try {
              Firebase.auth.currentUser?.uid?.let { currentUserId ->
                profilesRepository.getUser(
                    userId = currentUserId,
                    onSuccess = { currentUser ->
                      if (currentUser != null) {
                        App.getInstance()
                            .scheduleNotification(
                                activity = activity, isCreator = activity.creator == currentUser.id)
                      }
                    },
                    onFailure = { _ -> })
              }
            } catch (_: Exception) {}
          }
        },
        { _ -> })
  }

  /**
   * Updates an existing activity in the repository.
   *
   * @param activity The activity to be updated.
   */
  fun updateActivity(activity: Activity) {
    repository.updateActivity(
        activity,
        {
          getActivities()
          // notification scheduling
          Firebase.auth.currentUser?.uid?.let { currentUserId ->
            profilesRepository.getUser(
                userId = currentUserId,
                onSuccess = { currentUser ->
                  if (currentUser != null) {
                    App.getInstance()
                        .scheduleNotification(
                            activity = activity, isCreator = activity.creator == currentUser.id)
                  }
                },
                onFailure = {})
          }
        },
        {})
  }

  /**
   * Deletes an activity from the repository by its identifier.
   *
   * @param id The identifier of the activity to be deleted.
   */
  fun deleteActivityById(id: String) {
    // Get activity before deletion to access its data
    val activityToDelete =
        (_uiState.value as? ActivitiesUiState.Success)?.activities?.find { it.uid == id }

    activityToDelete?.let { activity ->
      // Send deletion notification (which also cancels any scheduled notifications)
      App.getInstance().sendDeletionNotification(activity)
    }
    repository.deleteActivityById(id, { getActivities() }, {})
  }

  /**
   * Selects an activity.
   *
   * @param activity The activity to be selected.
   */
  fun selectActivity(activity: Activity) {
    selectedActivity_.value = activity
  }

  /**
   * Selects a user.
   *
   * @param user The user to be selected.
   */
  fun selectUser(user: User) {
    selectedUser_.value = user
  }

  /**
   * Updates the list of activities.
   *
   * @param onSuccess The callback to be invoked upon successful retrieval of activities.
   * @param onFailure The callback to be invoked upon failure to retrieve activities.
   */
  fun getActivities(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {

    val onS = { activities: List<Activity> ->
      _uiState.value = ActivitiesUiState.Success(activities)
      onSuccess()
    }
    val onF = { exception: Exception ->
      _uiState.value = ActivitiesUiState.Error(exception)
      onFailure(exception)
    }
    repository.getActivities(onS, onF)
  }

  /**
   * Reviews an activity.
   *
   * @param activity The activity to be reviewed.
   * @param userId The identifier of the user reviewing the activity.
   * @param review The review to be added.
   */
  fun reviewActivity(activity: Activity, userId: String, review: Boolean?) {
    val newLikes = activity.likes.plus(userId to review)
    val newActivity = activity.copy(likes = newLikes)
    updateActivity(newActivity)
  }

  /**
   * Sorts the activities by score.
   *
   * @param user The user to sort the activities for.
   * @param distanceTo The function to calculate the distance to the activity.
   */
  fun sortActivitiesByScore(user: User, distanceTo: (Location?) -> Float?) {
    val activities =
        (_uiState.value as? ActivitiesUiState.Success)?.activities?.sortedByDescending {
          calculateActivityScore(it, user, distanceTo)
        }

    if (activities != null) _uiState.value = ActivitiesUiState.Success(activities)
  }

  /** Give the weights for the different factors that influence the score. */
  open fun getWeights(): Map<String, Double> {
    return mapOf(
        "distance" to 0.2,
        "date" to 0.15,
        "interest" to 0.25,
        "participation" to 0.15,
        "completion" to 0.1,
        "price" to 0.15)
  }

  /**
   * Calculates the distance score.
   *
   * @param distance The distance to the activity.
   */
  open fun calculateDistanceScore(distance: Float?): Double {
    val MAX_DISTANCE = 100.0 // 100 km
    if (distance == null) return 0.0
    return 1 - (distance / MAX_DISTANCE).coerceAtMost(1.0)
  }

  /**
   * Calculates the date score.
   *
   * @param date The date of the activity.
   */
  open fun calculateDateScore(date: Timestamp): Double {
    val MAX_HOURS = 96.0 // 4 days
    val hoursBetween = calculateHoursBetween(Timestamp.now(), date)?.toDouble() ?: return 0.0

    return 1 - (hoursBetween / MAX_HOURS).coerceAtMost(1.0)
  }

  /**
   * Calculates the hours between two timestamps.
   *
   * @param start The start timestamp.
   * @param end The end timestamp.
   */
  open fun calculateHoursBetween(start: Timestamp, end: Timestamp): Long? {
    val startMillis = start.toDate().time
    val endMillis = end.toDate().time

    if (startMillis > endMillis) return null

    val differenceMillis = endMillis - startMillis
    return TimeUnit.MILLISECONDS.toHours(differenceMillis)
  }

  /** Calculates the interest score. */
  open fun calculateInterestScore(): Double {
    // return user.interests.count { it == activity.category }.toDouble() /
    // user.interests.size.coerceAtLeast(1) TODO in Sprint 7
    return 0.0
  }

  /**
   * Calculates the participation score.
   *
   * @param userActivities The activities of the user.
   * @param creator The creator of the activity.
   */
  open fun calculateParticipationScore(userActivities: List<String>?, creator: String): Double {
    var participationScore = 0.0
    if (userActivities.isNullOrEmpty() || creator.isEmpty()) return participationScore
    profilesRepository.getUser(
        creator,
        { creatorLambda ->
          val matchingActivities =
              creatorLambda?.activities?.count { userActivities.contains(it) } ?: 0
          participationScore = (matchingActivities.toDouble() / 10).coerceAtMost(1.0)
        },
        { participationScore = 0.0 })
    return participationScore
  }

  /**
   * Calculates the completion score.
   *
   * @param numberParticipants The number of participants in the activity.
   * @param maxPlaces The maximum number of places in the activity.
   */
  open fun calculateCompletionScore(numberParticipants: Int, maxPlaces: Long): Double {
    if (maxPlaces == 0L || numberParticipants == 0 || numberParticipants > maxPlaces.toInt())
        return 0.0
    return (numberParticipants.toDouble() / maxPlaces).coerceAtMost(1.0)
  }

  /**
   * Calculates the price score.
   *
   * @param price The price of the activity.
   */
  open fun calculatePriceScore(price: Double): Double {
    val MAX_PRICE = 100.0 // Maximum reasonable price
    return 1 - (price / MAX_PRICE).coerceAtMost(1.0)
  }

  /**
   * Calculates the score of an activity.
   *
   * @param activity The activity to calculate the score for.
   * @param user The user to calculate the score for.
   * @param distanceTo The function to calculate the distance to the activity.
   */
  open fun calculateActivityScore(
      activity: Activity,
      user: User,
      distanceTo: (Location?) -> Float?
  ): Double {
    val weights = getWeights()
    val totalWeights = weights.values.sum()

    if (cachedScores_.containsKey(activity.uid)) return cachedScores_[activity.uid] ?: 2.5

    val distanceScore = calculateDistanceScore(distanceTo(activity.location))
    val dateScore = calculateDateScore(activity.date)
    val interestScore = calculateInterestScore()
    val participationScore = calculateParticipationScore(user.activities, activity.creator)
    val completionScore = calculateCompletionScore(activity.participants.size, activity.maxPlaces)
    val priceScore = calculatePriceScore(activity.price)
    val score =
        (distanceScore * (weights["distance"] ?: 0.2) +
            dateScore * (weights["date"] ?: 0.15) +
            interestScore * (weights["interest"] ?: 0.25) +
            participationScore * (weights["participation"] ?: 0.15) +
            completionScore * (weights["completion"] ?: 0.1) +
            priceScore * (weights["price"] ?: 0.15)) / totalWeights
    cachedScores_[activity.uid] = score
    return score
  }

  fun isButtonEnabled(
      title: String,
      description: String,
      price: String,
      placesMax: String,
      selectedOptionCategory: String?,
      selectedLocation: String?,
      selectedOptionType: String,
      startTime: String,
      duration: String,
      dueDate: Timestamp
  ): Boolean {
    return title.isNotEmpty() &&
        description.isNotEmpty() &&
        price.isNotEmpty() &&
        placesMax.isNotEmpty() &&
        selectedLocation != null &&
        selectedOptionType != "Select a type" &&
        selectedOptionCategory != null &&
        startTime.isNotEmpty() &&
        duration.isNotEmpty() &&
        dueDate.toDate().after(Timestamp.now().toDate())
  }

  open fun validateActivityCreation(
      context: Context,
      activityDateTime: Long,
      attendees: List<User>,
      placesMax: String,
      creator: String,
      hourDateViewModel: HourDateViewModel,
      startTime: String,
      duration: String,
      price: String,
      selectedLocation: Location?,
      selectedOptionCategory: Category?,
      selectedOptionInterest: String?
  ): Boolean {
    return when {
      activityDateTime - System.currentTimeMillis() < TimeUnit.HOURS.toMillis(1) -> {
        Toast.makeText(context, context.getString(R.string.schedule_activity), Toast.LENGTH_SHORT)
            .show()
        false
      }
      attendees.size >= placesMax.toInt() -> {
        Toast.makeText(context, context.getString(R.string.max_places_exceed), Toast.LENGTH_SHORT)
            .show()
        false
      }
      creator == "" -> {
        Toast.makeText(
                context, context.getString(R.string.login_check_in_create), Toast.LENGTH_SHORT)
            .show()
        false
      }
      price.toDoubleOrNull() == null -> {
        Toast.makeText(
                context, context.getString(R.string.invalid_price_format), Toast.LENGTH_SHORT)
            .show()
        false
      }
      placesMax.toLongOrNull() == null -> {
        Toast.makeText(
                context, context.getString(R.string.invalid_places_format), Toast.LENGTH_SHORT)
            .show()
        false
      }
      selectedLocation == null -> {
        Toast.makeText(context, context.getString(R.string.invalid_no_location), Toast.LENGTH_SHORT)
            .show()
        false
      }
      selectedOptionCategory != null &&
          categoryOf[selectedOptionInterest] != selectedOptionCategory -> {
        Toast.makeText(
                context, context.getString(R.string.invalid_interest_category), Toast.LENGTH_SHORT)
            .show()
        false
      }
      else -> true
    }
  }

  open fun createActivity(
      activityId: String,
      listActivityViewModel: ListActivitiesViewModel,
      hourDateViewModel: HourDateViewModel,
      dueDate: Timestamp,
      startTime: String,
      attendees: List<User>,
      profileViewModel: ProfileViewModel,
      imageViewModel: ImageViewModel,
      selectedImages: List<Bitmap>,
      items: MutableList<String>,
      title: String,
      description: String,
      duration: String,
      price: String,
      placesMax: String,
      creator: String,
      selectedLocation: Location?,
      selectedOptionType: String,
      selectedOptionCategory: Category?,
      selectedOptionInterest: String?,
      navigationActions: NavigationActions,
      context: Context,
      addUser: (User) -> Unit,
  ) {
    if (selectedOptionType == ActivityType.INDIVIDUAL.name)
        profileViewModel.userState.value?.let { addUser(it) }
    try {
      imageViewModel.uploadActivityImages(
          activityId,
          selectedImages,
          onSuccess = { imageUrls ->
            items.addAll(imageUrls) // Store URLs in items to retrieve later
          },
          onFailure = { exception ->
            Toast.makeText(
                    context, "Failed to upload images: ${exception.message}", Toast.LENGTH_SHORT)
                .show()
          })
      val activity =
          Activity(
              uid = activityId,
              title = title,
              description = description,
              date = dueDate,
              startTime = startTime,
              duration = duration,
              price = price.toDouble(),
              placesLeft = attendees.size.toLong(),
              maxPlaces = placesMax.toLongOrNull() ?: 0,
              creator = creator,
              status = ActivityStatus.ACTIVE,
              location = selectedLocation,
              images = items,
              participants = attendees,
              type = types.find { it.name == selectedOptionType } ?: types[1],
              comments = listOf(),
              category = selectedOptionCategory ?: categories[0],
              subcategory = selectedOptionInterest ?: "")
      listActivityViewModel.addActivity(activity)
      profileViewModel.addActivity(creator, activity.uid)
      navigationActions.navigateTo(Screen.OVERVIEW)
    } catch (_: NumberFormatException) {
      println("There is an error")
    }
  }

  fun prepareCalendarEvent(activity: Activity): CalendarEvent? {
    val calendar = Calendar.getInstance()
    calendar.time = activity.date.toDate() // Assuming `toDate` is a valid method
    val startMillis = calendar.timeInMillis

    val durationParts = activity.duration.split(":")
    val hours = durationParts[0].toIntOrNull() ?: return null
    val minutes = durationParts[1].toIntOrNull() ?: return null

    calendar.add(Calendar.HOUR, hours)
    calendar.add(Calendar.MINUTE, minutes)
    val endMillis = calendar.timeInMillis

    return CalendarEvent(
        title = activity.title,
        description = activity.description,
        location = activity.location?.name.orEmpty(),
        startMillis = startMillis,
        endMillis = endMillis)
  }

  data class CalendarEvent(
      val title: String,
      val description: String,
      val location: String,
      val startMillis: Long,
      val endMillis: Long
  )

  sealed class ActivitiesUiState {
    data class Success(val activities: List<Activity>) : ActivitiesUiState()

    data class Error(val exception: Exception) : ActivitiesUiState()
  }
}
