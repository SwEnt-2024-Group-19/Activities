package com.android.sample.ui.activity

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.android.sample.R
import com.android.sample.model.activity.ActivityType
import com.android.sample.model.activity.Category
import com.android.sample.model.activity.types
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.map.Location
import com.android.sample.model.profile.User
import com.android.sample.model.profile.interestStringValues
import com.android.sample.resources.C.Tag.BUTTON_HEIGHT_SM
import com.android.sample.resources.C.Tag.BUTTON_WIDTH
import com.android.sample.resources.C.Tag.CARD_ELEVATION_DEFAULT
import com.android.sample.resources.C.Tag.LARGE_PADDING
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.resources.C.Tag.TOP_TITLE_SIZE
import com.android.sample.ui.camera.Carousel
import com.android.sample.ui.components.AttendantPreview
import com.android.sample.ui.components.MyDatePicker
import com.android.sample.ui.components.MyTimePicker
import com.android.sample.ui.components.TextFieldWithErrorState
import com.android.sample.ui.components.TextFieldWithIcon
import com.android.sample.ui.dialogs.AddUserDialog
import com.google.firebase.Timestamp
import com.vanpra.composematerialdialogs.MaterialDialogState

/**
 * Composable function to display the activity form. Modularized and used in the CreateActivity and
 * EditActivity screens.
 *
 * @param context The context in which the form is displayed.
 * @param selectedImages List of selected images to be displayed in the carousel.
 * @param onOpenDialogImage Callback to open the image dialog.
 * @param onDeleteImage Callback to delete an image from the selected images.
 * @param onTitleChange Callback to handle changes in the title input field.
 * @param title The current value of the title input field.
 * @param maxTitleSize The maximum allowed size for the title.
 * @param onDescriptionChange Callback to handle changes in the description input field.
 * @param description The current value of the description input field.
 * @param maxDescriptionSize The maximum allowed size for the description.
 * @param onClickDate Callback to handle the click event for the date picker.
 * @param onCloseDate Callback to handle the close event for the date picker.
 * @param onSelectDate Callback to handle the selection of a date.
 * @param dueDate The currently selected due date.
 * @param dateIsOpen Boolean indicating if the date picker is open.
 * @param dateIsSet Boolean indicating if the date has been set.
 * @param onClickStartingTime Callback to handle the click event for the starting time picker.
 * @param startTimeIsOpen Boolean indicating if the starting time picker is open.
 * @param startTimeIsSet Boolean indicating if the starting time has been set.
 * @param onStartTimeSelected Callback to handle the selection of a starting time.
 * @param startTime The currently selected starting time.
 * @param onCloseStartTime Callback to handle the close event for the starting time picker.
 * @param onClickDurationTime Callback to handle the click event for the duration time picker.
 * @param durationIsOpen Boolean indicating if the duration time picker is open.
 * @param durationIsSet Boolean indicating if the duration time has been set.
 * @param onSelectDuration Callback to handle the selection of a duration time.
 * @param onCloseDuration Callback to handle the close event for the duration time picker.
 * @param duration The currently selected duration time.
 * @param price The current value of the price input field.
 * @param onPriceChange Callback to handle changes in the price input field.
 * @param placesMax The current value of the maximum places input field.
 * @param onPlacesMaxChange Callback to handle changes in the maximum places input field.
 * @param locationQuery The current value of the location query input field.
 * @param onLocationQueryChange Callback to handle changes in the location query input field.
 * @param showDropdown Boolean indicating if the location suggestions dropdown is shown.
 * @param locationSuggestions List of location suggestions to be displayed in the dropdown.
 * @param onDismissLocation Callback to handle the dismissal of the location suggestions dropdown.
 * @param onLocationClick Callback to handle the selection of a location from the suggestions.
 * @param expandedType Boolean indicating if the activity type dropdown is expanded.
 * @param onExpandedTypeChange Callback to handle the expansion change of the activity type
 *   dropdown.
 * @param onSelectType Callback to handle the selection of an activity type.
 * @param onDismissType Callback to handle the dismissal of the activity type dropdown.
 * @param selectedOptionType The currently selected activity type.
 * @param expandedCategory Boolean indicating if the category dropdown is expanded.
 * @param onExpandedCategoryChange Callback to handle the expansion change of the category dropdown.
 * @param onDismissCategory Callback to handle the dismissal of the category dropdown.
 * @param selectedOptionCategory The currently selected category.
 * @param selectedOptionInterest The currently selected interest.
 * @param expandedInterest Boolean indicating if the interest dropdown is expanded.
 * @param onInterestExpandChange Callback to handle the expansion change of the interest dropdown.
 * @param onInterestDismiss Callback to handle the dismissal of the interest dropdown.
 * @param onInterestSelect Callback to handle the selection of an interest.
 * @param onSelectCategory Callback to handle the selection of a category.
 * @param attendees List of attendees to be displayed.
 * @param showDialogUser Boolean indicating if the user dialog is shown.
 * @param deleteAttendant Callback to handle the deletion of an attendant.
 * @param onDismissUserDialog Callback to handle the dismissal of the user dialog.
 * @param onAddUser Callback to handle the addition of a user.
 * @param onOpenUserDialog Callback to handle the opening of the user dialog.
 * @param onProfileClick Callback to handle the click event on a user's profile.
 * @param imageViewModel The view model for the image.
 */
@Suppress("NAME_SHADOWING")
@Composable
fun ActivityForm(
    context: Context,
    selectedImages: List<Bitmap>,
    onOpenDialogImage: () -> Unit,
    onDeleteImage: (Bitmap) -> Unit,
    onTitleChange: (String) -> Unit,
    title: String,
    maxTitleSize: Int,
    onDescriptionChange: (String) -> Unit,
    description: String,
    maxDescriptionSize: Int,
    onClickDate: () -> Unit,
    onCloseDate: (MaterialDialogState) -> Unit,
    onSelectDate: (Timestamp) -> Unit,
    dueDate: Timestamp,
    dateIsOpen: Boolean,
    dateIsSet: Boolean,
    onClickStartingTime: () -> Unit,
    startTimeIsOpen: Boolean,
    startTimeIsSet: Boolean,
    onStartTimeSelected: (Timestamp) -> Unit,
    startTime: String,
    onCloseStartTime: (MaterialDialogState) -> Unit,
    onClickDurationTime: () -> Unit,
    durationIsOpen: Boolean,
    durationIsSet: Boolean,
    onSelectDuration: (Timestamp) -> Unit,
    onCloseDuration: (MaterialDialogState) -> Unit,
    duration: String,
    price: String,
    onPriceChange: (String) -> Unit,
    placesMax: String,
    onPlacesMaxChange: (String) -> Unit,
    locationQuery: String,
    onLocationQueryChange: (String) -> Unit,
    showDropdown: Boolean,
    locationSuggestions: List<Location?>,
    onDismissLocation: () -> Unit,
    onLocationClick: (Location) -> Unit,
    expandedType: Boolean,
    onExpandedTypeChange: (Boolean) -> Unit,
    onSelectType: (ActivityType) -> Unit,
    onDismissType: () -> Unit,
    selectedOptionType: String,
    expandedCategory: Boolean,
    onExpandedCategoryChange: (Boolean) -> Unit,
    onDismissCategory: () -> Unit,
    selectedOptionCategory: Category?,
    selectedOptionInterest: String?,
    expandedInterest: Boolean,
    onInterestExpandChange: (Boolean) -> Unit,
    onInterestDismiss: () -> Unit,
    onInterestSelect: (String) -> Unit,
    onSelectCategory: (Category) -> Unit,
    attendees: List<User>,
    showDialogUser: Boolean,
    deleteAttendant: (User) -> Unit,
    onDismissUserDialog: () -> Unit,
    onAddUser: (User) -> Unit,
    onOpenUserDialog: () -> Unit,
    onProfileClick: (User) -> Unit,
    imageViewModel: ImageViewModel,
) {
  // Used for displaying images in a carousel
  Carousel(openDialog = onOpenDialogImage, itemsList = selectedImages, deleteImage = onDeleteImage)
  Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
  // Title section with the remaining characters
  RemainingPlace(title, maxTitleSize)
  TextFieldWithErrorState(
      value = title,
      onValueChange = onTitleChange,
      label = stringResource(id = R.string.request_activity_title),
      modifier = Modifier.padding(STANDARD_PADDING.dp).fillMaxWidth(),
      validation = { title ->
        when {
          title.isEmpty() -> R.string.title_empty.toString()
          title.length > maxTitleSize -> R.string.title_too_long.toString()
          else -> null
        }
      },
      testTag = "inputTitleCreate",
      errorTestTag = "TitleErrorText")
  Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
  // Description section with the remaining characters
  RemainingPlace(description, maxDescriptionSize)
  TextFieldWithErrorState(
      value = description,
      onValueChange = onDescriptionChange,
      label = stringResource(id = R.string.request_activity_description),
      modifier = Modifier.padding(STANDARD_PADDING.dp).fillMaxWidth(),
      validation = { description ->
        when {
          description.isEmpty() -> R.string.description_empty.toString()
          description.length > maxDescriptionSize -> R.string.description_too_long.toString()
          else -> null
        }
      },
      testTag = "inputDescriptionCreate",
      errorTestTag = "DescriptionErrorText")

  if (dateIsOpen) {
    // Date picker displayed iff clicked on the button
    MyDatePicker(
        onCloseRequest = onCloseDate,
        onDateSelected = onSelectDate,
        isOpen = dateIsOpen,
        initialDate = null)
  }
  if (startTimeIsOpen) {
    // Start time picker displayed iff clicked on the button
    MyTimePicker(
        onTimeSelected = onStartTimeSelected,
        isOpen = startTimeIsOpen,
        onCloseRequest = onCloseStartTime)
  }
  if (durationIsOpen) {
    // Duration time picker displayed iff clicked on the button
    MyTimePicker(
        onTimeSelected = onSelectDuration,
        isOpen = durationIsOpen,
        onCloseRequest = onCloseDuration,
        isAmPm = false)
  }

  Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))

  // Price, places, location, type, category, interest, and attendees section
  TextFieldWithErrorState(
      value = price,
      onValueChange = onPriceChange,
      label = stringResource(id = R.string.request_price_activity),
      modifier = Modifier.padding(STANDARD_PADDING.dp).fillMaxWidth(),
      validation = { price ->
        when {
          price.isEmpty() -> R.string.price_empty.toString()
          price.toIntOrNull() == null -> R.string.price_nan.toString()
          else -> null
        }
      },
      testTag = "inputPriceCreate",
      errorTestTag = "PriceErrorText")
  Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
  TextFieldWithErrorState(
      value = placesMax,
      onValueChange = onPlacesMaxChange,
      label = stringResource(id = R.string.request_placesMax_activity),
      modifier = Modifier.padding(STANDARD_PADDING.dp).fillMaxWidth(),
      validation = { placesMax ->
        when {
          placesMax.isEmpty() -> R.string.places_empty.toString()
          placesMax.toIntOrNull() == null -> R.string.places_nan.toString()
          else -> null
        }
      },
      testTag = "inputPlacesCreate",
      errorTestTag = "PlacesErrorText")
  Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
  Box {
    TextFieldWithErrorState(
        value = locationQuery,
        onValueChange = onLocationQueryChange,
        label = stringResource(id = R.string.request_location_activity),
        modifier = Modifier.padding(STANDARD_PADDING.dp).fillMaxWidth(),
        validation = { locationQuery ->
          when {
            locationQuery.isEmpty() -> R.string.location_empty.toString()
            else -> null
          }
        },
        testTag = "inputLocationCreate",
        errorTestTag = "LocationErrorText")

    // Dropdown menu for location suggestions
    DropdownMenu(
        expanded = showDropdown && locationSuggestions.isNotEmpty(),
        onDismissRequest = onDismissLocation,
        properties = PopupProperties(focusable = false)) {
          locationSuggestions.filterNotNull().take(3).forEach { location ->
            DropdownMenuItem(
                text = {
                  Text(
                      text =
                          location.name.take(TOP_TITLE_SIZE) +
                              if (location.name.length > TOP_TITLE_SIZE) "..."
                              else "", // Limit name length
                      maxLines = 1 // Ensure name doesn't overflow
                      )
                },
                onClick = { onLocationClick(location) },
                modifier = Modifier.padding(STANDARD_PADDING.dp))
          }
        }
  }
  Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))

  Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))

  MyDropDownMenu(
      mode = "type",
      expanded = expandedType,
      onExpandChange = onExpandedTypeChange,
      onDismiss = onDismissType,
      valueItem = selectedOptionType,
      onSelect = { type -> onSelectType(type as ActivityType) },
      context = context,
      listItems = types)

  MyDropDownMenu(
      mode = "category",
      expanded = expandedCategory,
      onExpandChange = onExpandedCategoryChange,
      onDismiss = onDismissCategory,
      valueItem =
          selectedOptionCategory?.name ?: context.getString(R.string.select_activity_category),
      onSelect = { category -> onSelectCategory(category as Category) },
      context = context,
      listItems = Category.values().toList())

  if (selectedOptionCategory != null) {
    Spacer(modifier = Modifier.height(SMALL_PADDING.dp))

    MyDropDownMenu(
        mode = "interest",
        expanded = expandedInterest,
        onExpandChange = onInterestExpandChange,
        onDismiss = onInterestDismiss,
        valueItem = selectedOptionInterest ?: context.getString(R.string.select_activity_interest),
        onSelect = { interest -> onInterestSelect(interest as String) },
        context = context,
        listItems = interestStringValues[selectedOptionCategory] ?: listOf())
  }

  Spacer(modifier = Modifier.height(LARGE_PADDING.dp))

  Card(
      elevation = CardDefaults.cardElevation(defaultElevation = CARD_ELEVATION_DEFAULT.dp),
  ) {
    TextButton(
        onClick = onOpenUserDialog,
        modifier =
            Modifier.width(BUTTON_WIDTH.dp)
                .height(BUTTON_HEIGHT_SM.dp)
                .testTag("addAttendeeButton")) {
          Row(
              horizontalArrangement =
                  Arrangement.spacedBy(STANDARD_PADDING.dp, Alignment.CenterHorizontally),
              verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = "add a new attendee",
            )
            Text("Add Attendee")
          }
        }
  }
  if (attendees.isNotEmpty()) {
    LazyRow(
        modifier = Modifier.fillMaxHeight().padding(STANDARD_PADDING.dp),
    ) {
      items(attendees.size) { index ->
        AttendantPreview(
            onProfileClick = onProfileClick,
            imageViewModel = imageViewModel,
            deleteAttendant = deleteAttendant,
            user = attendees[index],
            index = index)
      }
    }
  }
  if (showDialogUser) {
    // Dialog to add a new user
    AddUserDialog(
        onDismiss = onDismissUserDialog,
        onAddUser = onAddUser,
    )
  }
  // Button to display the date picker
  TextButton(
      onClick = onClickDate,
      modifier = Modifier.fillMaxWidth().padding(STANDARD_PADDING.dp).testTag("inputDateCreate"),
  ) {
    Icon(
        Icons.Filled.CalendarMonth,
        contentDescription = "select date",
        modifier = Modifier.padding(end = STANDARD_PADDING.dp).testTag("iconDateCreate"))
    if (dateIsSet)
        Text(
            "Selected date: ${dueDate.toDate().toString().take(11)}," +
                "${dueDate.toDate().year + 1900}  (click to change)")
    else Text("Select Date for the activity")
  }
  Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
  // Button to display the start time picker
  TextButton(
      onClick = onClickStartingTime,
      modifier =
          Modifier.fillMaxWidth().padding(STANDARD_PADDING.dp).testTag("inputStartTimeCreate"),
  ) {
    Icon(
        Icons.Filled.AccessTime,
        contentDescription = "select start time",
        modifier = Modifier.padding(end = STANDARD_PADDING.dp).testTag("iconStartTimeCreate"))
    if (startTimeIsSet) Text("Start time: $startTime (click to change)")
    else Text("Select start time")
  }

  Spacer(modifier = Modifier.width(STANDARD_PADDING.dp))
  // Button to display the duration time picker
  TextButton(
      onClick = onClickDurationTime,
      modifier = Modifier.fillMaxWidth().padding(STANDARD_PADDING.dp).testTag("inputEndTimeCreate"),
  ) {
    Icon(
        Icons.Filled.HourglassTop,
        contentDescription = "select duration",
        modifier =
            Modifier.padding(end = STANDARD_PADDING.dp)
                .align(Alignment.CenterVertically)
                .testTag("iconEndTimeCreate"))
    if (durationIsSet) Text("Duration Time: $duration (click to change)")
    else Text("Select Duration")
  }
}

/**
 * Composable function to display a dropdown menu with different modes.
 *
 * @param mode The mode of the dropdown menu (e.g., "category", "type", "interest").
 * @param expanded Boolean indicating if the dropdown menu is expanded.
 * @param onExpandChange Callback to handle the expansion change of the dropdown menu.
 * @param onDismiss Callback to handle the dismissal of the dropdown menu.
 * @param valueItem The currently selected value item.
 * @param onSelect Callback to handle the selection of an item from the dropdown menu.
 * @param context The context in which the dropdown menu is displayed.
 * @param listItems List of items to be displayed in the dropdown menu.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDropDownMenu(
    mode: String,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    valueItem: String?,
    onSelect: (Any) -> Unit,
    context: Context,
    listItems: List<Any> = listOf(Any())
) {
  ExposedDropdownMenuBox(
      // Set the test tag based on the mode
      modifier =
          Modifier.testTag(
                  when (mode) {
                    "category" -> "chooseCategoryMenu"
                    "type" -> "chooseTypeMenu"
                    else -> "chooseInterestMenu"
                  })
              .fillMaxWidth()
              .padding(STANDARD_PADDING.dp),
      expanded = expanded,
      onExpandedChange = onExpandChange) {
        TextFieldWithIcon(
            value =
                valueItem
                    ?: when (mode) {
                      "category" -> context.getString(R.string.select_activity_category)
                      "type" -> context.getString(R.string.select_activity_type)
                      "interest" -> context.getString(R.string.select_activity_interest)
                      else -> ""
                    },
            label = {
              when (mode) {
                "category" -> Text(context.getString(R.string.activity_category))
                "type" -> Text(context.getString(R.string.activity_type))
                "interest" -> Text(context.getString(R.string.activity_interest))
                else -> {}
              }
            },
            icon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            testTag =
                when (mode) {
                  "category" -> "categoryTextField"
                  "type" -> "typeTextField"
                  "interest" -> "interestTextField"
                  else -> ""
                },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismiss,
            modifier = Modifier.fillMaxWidth().padding(STANDARD_PADDING.dp)) {
              listItems.forEach {
                DropdownMenuItem(
                    modifier = Modifier.fillMaxWidth().padding(STANDARD_PADDING.dp),
                    text = {
                      when (it) {
                        is Category -> Text(it.name)
                        is String -> Text(it)
                        is ActivityType -> Text(it.name)
                        else -> {}
                      }
                    },
                    onClick = { onSelect(it) })
              }
            }
      }
}
