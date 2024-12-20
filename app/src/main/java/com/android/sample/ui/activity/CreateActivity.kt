package com.android.sample.ui.activity

import android.graphics.Bitmap
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.activity.Category
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.hour_date.HourDateViewModel
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.map.Location
import com.android.sample.model.map.LocationViewModel
import com.android.sample.model.network.NetworkManager
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.User
import com.android.sample.resources.C.Tag.BUTTON_HEIGHT_SM
import com.android.sample.resources.C.Tag.BUTTON_WIDTH
import com.android.sample.resources.C.Tag.DARK_BLUE_COLOR
import com.android.sample.resources.C.Tag.LARGE_PADDING
import com.android.sample.resources.C.Tag.MAIN_BACKGROUND
import com.android.sample.resources.C.Tag.MAIN_COLOR_DARK
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.PRIMARY
import com.android.sample.resources.C.Tag.ROUNDED_CORNER_SHAPE_DEFAULT
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.ui.camera.CameraScreen
import com.android.sample.ui.camera.GalleryScreen
import com.android.sample.ui.components.WaitingScreen
import com.android.sample.ui.dialogs.AddImageDialog
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import java.time.ZoneId

/**
 * Composable function to display the Create Activity screen. This screen allows users to create an
 * activity by filling out a form with details such as title, description, date, time, location,
 * etc.
 *
 * @param listActivityViewModel ViewModel for managing the list of activities.
 * @param navigationActions Navigation actions for navigating between screens.
 * @param profileViewModel ViewModel for managing user profiles.
 * @param locationViewModel ViewModel for managing location data.
 * @param imageViewModel ViewModel for managing images.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateActivityScreen(
    listActivityViewModel: ListActivitiesViewModel,
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    locationViewModel: LocationViewModel,
    imageViewModel: ImageViewModel
) {
  val hourDateViewModel: HourDateViewModel = HourDateViewModel()
  val context = LocalContext.current
  val networkManager = NetworkManager(context)
  var expandedType by remember { mutableStateOf(false) }
  var expandedCategory by remember { mutableStateOf(false) }
  var expandedInterest by remember { mutableStateOf(false) }
  var selectedOptionType by remember { mutableStateOf("Select a type") }
  var selectedOptionCategory: Category? by remember { mutableStateOf(null) }
  var selectedOptionInterest: String? by remember { mutableStateOf(null) }
  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  val creator = FirebaseAuth.getInstance().currentUser?.uid ?: ""
  var dateIsOpen by remember { mutableStateOf(false) }
  var dateIsSet by remember { mutableStateOf(false) }
  var startTimeIsOpen by remember { mutableStateOf(false) }
  var startTimeIsSet by remember { mutableStateOf(false) }
  var durationIsOpen by remember { mutableStateOf(false) }
  var durationIsSet by remember { mutableStateOf(false) }
  var price by remember { mutableStateOf("") }
  var placesMax by remember { mutableStateOf("") }
  var dueDate by remember { mutableStateOf(Timestamp.now()) }
  var isCamOpen by remember { mutableStateOf(false) }
  var isGalleryOpen by remember { mutableStateOf(false) }
  var startTime by remember { mutableStateOf("") }
  var duration by remember { mutableStateOf("") }
  var showDialogUser by remember { mutableStateOf(false) }
  var showDialogImage by remember { mutableStateOf(false) }

  val maxTitleLength = 50
  val maxDescriptionLength = 500
  val locationQuery by locationViewModel.query.collectAsState()
  locationViewModel.setQuery("")
  var showDropdown by remember { mutableStateOf(false) }
  //  val locationSuggestions by locationViewModel.locationSuggestions.collectAsState()
  val locationSuggestions by
      locationViewModel.locationSuggestions.collectAsState(initial = emptyList<Location?>())

  var selectedLocation by remember { mutableStateOf<Location?>(null) }
  // Add scroll
  val scrollState = rememberScrollState()
  var isDefaultPictureOpen by remember { mutableStateOf(false) }
  // Attendees
  val attendees_: List<User> = listOf<User>()
  var attendees: List<User> by remember { mutableStateOf(attendees_) }
  var selectedImages = remember { mutableStateListOf<Bitmap>() }
  var items = remember { mutableStateListOf<String>() }
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("createActivityScreen"),
      topBar = {
        TopAppBar(
            modifier = Modifier.testTag("createActivityTopBar").background(Color(PRIMARY)),
            title = {
              Text(
                  modifier = Modifier.testTag("createActivityTopTitle"),
                  text = stringResource(id = R.string.title_screen_create_activity),
                  textAlign = TextAlign.Center,
                  color = Color.White,
              )
            },
            colors =
                TopAppBarColors(
                    containerColor = Color(MAIN_COLOR_DARK),
                    titleContentColor = Color.Black,
                    scrolledContainerColor = Color(MAIN_BACKGROUND),
                    navigationIconContentColor = Color(MAIN_COLOR_DARK),
                    actionIconContentColor = Color(MAIN_COLOR_DARK)))
      },
      content = { paddingValues ->
        if (!networkManager.isNetworkAvailable()) {
          WaitingScreen(stringResource(id = R.string.no_internet_connection))
        } else {
          if (isCamOpen) {
            CameraScreen(
                paddingValues = paddingValues,
                controller =
                    remember {
                      LifecycleCameraController(context).apply {
                        setEnabledUseCases(CameraController.IMAGE_CAPTURE)
                      }
                    },
                context = context,
                isCamOpen = { isCamOpen = false },
                addElem = { bitmap -> selectedImages.add(bitmap) })
          } else {
            Column(
                modifier =
                    Modifier.padding(paddingValues)
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .background(Color(MAIN_BACKGROUND))
                        .testTag("activityCreateScreen"),
            ) {
              if (isGalleryOpen) {
                GalleryScreen(
                    isGalleryOpen = { isGalleryOpen = false },
                    addImage = { bitmap -> selectedImages.add(bitmap) },
                    context = context)
              }
              if (showDialogImage) {
                AddImageDialog(
                    onDismiss = { showDialogImage = false },
                    onGalleryClick = {
                      showDialogImage = false
                      isGalleryOpen = true
                    },
                    onCameraClick = {
                      showDialogImage = false
                      isCamOpen = true
                    },
                    onSelectDefault = { showDialogImage = false })
              }
              ActivityForm(
                  context = context,
                  selectedImages = selectedImages,
                  onOpenDialogImage = { showDialogImage = true },
                  onDeleteImage = { bitmap -> selectedImages.remove(bitmap) },
                  title = title,
                  onTitleChange = { title = it },
                  maxTitleSize = maxTitleLength,
                  description = description,
                  maxDescriptionSize = maxDescriptionLength,
                  onDescriptionChange = { description = it },
                  onClickDate = { dateIsOpen = true },
                  onCloseDate = { dateIsOpen = false },
                  onSelectDate = {
                    dueDate = it
                    dateIsOpen = false
                    dateIsSet = true
                  },
                  dueDate = dueDate,
                  dateIsOpen = dateIsOpen,
                  dateIsSet = dateIsSet,
                  onClickStartingTime = { startTimeIsOpen = true },
                  startTimeIsOpen = startTimeIsOpen,
                  startTimeIsSet = startTimeIsSet,
                  onStartTimeSelected = { time ->
                    startTime =
                        time.toInstant().atZone(ZoneId.systemDefault()).toLocalTime().toString()
                    startTimeIsOpen = false
                    startTimeIsSet = true
                  },
                  startTime = startTime,
                  onCloseStartTime = { startTimeIsOpen = false },
                  onClickDurationTime = { durationIsOpen = true },
                  durationIsOpen = durationIsOpen,
                  durationIsSet = durationIsSet,
                  onSelectDuration = { time ->
                    duration =
                        time.toInstant().atZone(ZoneId.systemDefault()).toLocalTime().toString()
                    durationIsOpen = false
                    durationIsSet = true
                  },
                  onCloseDuration = { durationIsOpen = false },
                  duration = duration,
                  price = price,
                  onPriceChange = { price = it },
                  placesMax = placesMax,
                  onPlacesMaxChange = { placesMax = it },
                  locationQuery = locationQuery,
                  onLocationQueryChange = {
                    locationViewModel.setQuery(it)
                    showDropdown = it != "" // Show dropdown when user starts typing
                  },
                  showDropdown = showDropdown,
                  locationSuggestions = locationSuggestions,
                  onDismissLocation = { showDropdown = false },
                  onLocationClick = { location ->
                    locationViewModel.setQuery(location.name)
                    selectedLocation = location
                    showDropdown = false // Close dropdown on selection
                  },
                  expandedType = expandedType,
                  onExpandedTypeChange = { expandedType = !expandedType },
                  onSelectType = { selectionOption ->
                    selectedOptionType = selectionOption.name
                    expandedType = false
                  },
                  onDismissType = { expandedType = false },
                  selectedOptionType = selectedOptionType,
                  expandedCategory = expandedCategory,
                  onExpandedCategoryChange = { expandedCategory = !expandedCategory },
                  onDismissCategory = { expandedCategory = false },
                  selectedOptionCategory = selectedOptionCategory,
                  selectedOptionInterest = selectedOptionInterest,
                  expandedInterest = expandedInterest,
                  onInterestExpandChange = { expandedInterest = !expandedInterest },
                  onInterestDismiss = { expandedInterest = false },
                  onInterestSelect = { selectionOption ->
                    selectedOptionInterest = selectionOption
                    expandedInterest = false
                  },
                  onSelectCategory = {
                    selectedOptionCategory = it
                    expandedCategory = false
                  },
                  attendees = attendees,
                  showDialogUser = showDialogUser,
                  deleteAttendant = { user -> attendees -= user },
                  onDismissUserDialog = { showDialogUser = false },
                  onAddUser = { user -> attendees += user },
                  onOpenUserDialog = { showDialogUser = true },
                  onProfileClick = {},
                  imageViewModel = imageViewModel,
              )
              Spacer(modifier = Modifier.height(LARGE_PADDING.dp))
              Button(
                  enabled =
                      listActivityViewModel.isButtonEnabled(
                          title = title,
                          description = description,
                          dueDate = dueDate,
                          startTime = startTime,
                          duration = duration,
                          price = price,
                          placesMax = placesMax,
                          selectedLocation = selectedLocation?.name,
                          selectedOptionType = selectedOptionType,
                          selectedOptionCategory = selectedOptionCategory?.name),
                  onClick = {
                    val activityId = listActivityViewModel.getNewUid()

                    val activityTimestamps =
                        hourDateViewModel.combineDateAndTime(dueDate, startTime)
                    val activityDateTime = activityTimestamps.toInstant().toEpochMilli()

                    if (listActivityViewModel.validateActivityCreation(
                        context,
                        activityDateTime,
                        attendees,
                        placesMax,
                        creator,
                        hourDateViewModel,
                        startTime,
                        duration,
                        price,
                        selectedLocation,
                        selectedOptionCategory,
                        selectedOptionInterest)) {
                      listActivityViewModel.createActivity(
                          activityId,
                          listActivityViewModel,
                          hourDateViewModel,
                          dueDate,
                          startTime,
                          attendees,
                          profileViewModel,
                          imageViewModel,
                          selectedImages,
                          items,
                          title,
                          description,
                          duration,
                          price,
                          placesMax,
                          creator,
                          selectedLocation,
                          selectedOptionType,
                          selectedOptionCategory,
                          selectedOptionInterest,
                          navigationActions,
                          context,
                          addUser = { user -> attendees += user },
                      )
                    }
                  },
                  modifier =
                      Modifier.width(BUTTON_WIDTH.dp)
                          .background(Color.Transparent)
                          .height(BUTTON_HEIGHT_SM.dp)
                          .testTag("createButton")
                          .align(Alignment.CenterHorizontally),
                  shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE_DEFAULT.dp),
                  colors =
                      ButtonColors(
                          containerColor = Color(MAIN_COLOR_DARK),
                          contentColor = Color.White,
                          disabledContentColor = Color.Gray,
                          disabledContainerColor = Color.LightGray,
                      ),
              ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "add a new activity",
                )
                Text(text = stringResource(id = R.string.button_create_activity))
              }
              Spacer(modifier = Modifier.height(MEDIUM_PADDING.dp))
            }
          }
        }
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = Route.ADD_ACTIVITY)
      })
}

/**
 * Composable function to display the remaining character count and a progress indicator.
 *
 * @param field The current text input field whose length is being tracked.
 * @param maxLength The maximum allowed length for the text input field.
 */
@Composable
fun RemainingPlace(field: String, maxLength: Int) {
  Row(
      modifier =
          Modifier.fillMaxWidth().padding(horizontal = MEDIUM_PADDING.dp).testTag("remainingPlace"),
      horizontalArrangement = Arrangement.End) {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.testTag("remainingPlaceColumn")) {
              Text(
                  text = "${field.length}/$maxLength characters",
                  fontSize = MEDIUM_PADDING.sp,
                  color = Color.Gray,
                  modifier = Modifier.testTag("remainingPlaceText"))
              LinearProgressIndicator(
                  progress = field.length / maxLength.toFloat(),
                  modifier =
                      Modifier.height(STANDARD_PADDING.dp)
                          .width(130.dp)
                          .clip(RoundedCornerShape(SMALL_PADDING.dp))
                          .testTag("remainingPlaceProgress"),
                  color = Color(DARK_BLUE_COLOR),
                  backgroundColor = Color.LightGray)
            }
      }
}
