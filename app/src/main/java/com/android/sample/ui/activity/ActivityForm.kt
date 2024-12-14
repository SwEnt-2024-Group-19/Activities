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
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import com.android.sample.resources.C.Tag.BUTTON_HEIGHT
import com.android.sample.resources.C.Tag.BUTTON_WIDTH
import com.android.sample.resources.C.Tag.LARGE_PADDING
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.resources.C.Tag.TOP_TITLE_SIZE
import com.android.sample.ui.camera.Carousel
import com.android.sample.ui.components.AttendantPreview
import com.android.sample.ui.components.MyDatePicker
import com.android.sample.ui.components.MyTimePicker
import com.android.sample.ui.dialogs.AddUserDialog
import com.google.firebase.Timestamp
import com.vanpra.composematerialdialogs.MaterialDialogState

@OptIn(ExperimentalMaterial3Api::class)
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
  Carousel(openDialog = onOpenDialogImage, itemsList = selectedImages, deleteImage = onDeleteImage)
  Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
  RemainingPlace(title, maxTitleSize)
  OutlinedTextField(
      value = title,
      onValueChange = onTitleChange,
      label = { Text("Title") },
      modifier = Modifier.padding(STANDARD_PADDING.dp).fillMaxWidth().testTag("inputTitleCreate"),
      placeholder = { Text(text = stringResource(id = R.string.request_activity_title)) },
      singleLine = true,
  )
  Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
  RemainingPlace(description, maxDescriptionSize)
  OutlinedTextField(
      value = description,
      onValueChange = onDescriptionChange,
      label = { Text("Description") },
      modifier =
          Modifier.padding(STANDARD_PADDING.dp).fillMaxWidth().testTag("inputDescriptionCreate"),
      placeholder = { Text(text = stringResource(id = R.string.request_activity_description)) })
  Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
  OutlinedButton(
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
  if (dateIsOpen) {
    MyDatePicker(
        onCloseRequest = onCloseDate,
        onDateSelected = onSelectDate,
        isOpen = dateIsOpen,
        initialDate = null)
  }
  Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
  OutlinedButton(
      onClick = onClickStartingTime,
      modifier =
          Modifier.fillMaxWidth().padding(STANDARD_PADDING.dp).testTag("inputStartTimeCreate"),
  ) {
    Icon(
        Icons.Filled.AccessTime,
        contentDescription = "select start time",
        modifier = Modifier.padding(end = STANDARD_PADDING.dp).testTag("iconStartTimeCreate"))
    if (startTimeIsSet) Text("Start time: ${startTime} (click to change)")
    else Text("Select start time")
  }
  if (startTimeIsOpen) {
    MyTimePicker(
        onTimeSelected = onStartTimeSelected,
        isOpen = startTimeIsOpen,
        onCloseRequest = onCloseStartTime)
  }
  Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
  OutlinedButton(
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
    if (durationIsSet) Text("Finishing Time: ${duration} (click to change)")
    else Text("Select End Time")
  }
  if (durationIsOpen) {
    MyTimePicker(
        onTimeSelected = onSelectDuration,
        isOpen = durationIsOpen,
        onCloseRequest = onCloseDuration)
  }
  Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))

  OutlinedTextField(
      value = price,
      onValueChange = onPriceChange,
      label = { Text("Price") },
      modifier = Modifier.padding(STANDARD_PADDING.dp).fillMaxWidth().testTag("inputPriceCreate"),
      placeholder = { Text(text = stringResource(id = R.string.request_price_activity)) },
      singleLine = true,
  )
  Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
  OutlinedTextField(
      value = placesMax,
      onValueChange = onPlacesMaxChange,
      label = { Text("Total Places") },
      modifier = Modifier.padding(STANDARD_PADDING.dp).fillMaxWidth().testTag("inputPlacesCreate"),
      placeholder = { Text(text = stringResource(id = R.string.request_placesMax_activity)) },
      singleLine = true,
  )
  Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
  Box {
    OutlinedTextField(
        value = locationQuery,
        onValueChange = onLocationQueryChange,
        label = { Text("Location") },
        placeholder = { Text("Enter an Address or Location") },
        modifier =
            Modifier.padding(STANDARD_PADDING.dp).fillMaxWidth().testTag("inputLocationCreate"),
        singleLine = true)

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

  Button(
      onClick = onOpenUserDialog,
      modifier =
          Modifier.width(BUTTON_WIDTH.dp).height(BUTTON_HEIGHT.dp).testTag("addAttendeeButton")) {
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
  if (attendees.isNotEmpty()) {
    LazyRow(
        modifier = Modifier.fillMaxHeight().height(85.dp).padding(STANDARD_PADDING.dp),
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
    AddUserDialog(
        onDismiss = onDismissUserDialog,
        onAddUser = onAddUser,
    )
  }
}

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
        OutlinedTextField(
            readOnly = true,
            value =
                valueItem
                    ?: when (mode) {
                      "category" -> context.getString(R.string.select_activity_category)
                      "type" -> context.getString(R.string.select_activity_type)
                      "interest" -> context.getString(R.string.select_activity_interest)
                      else -> ""
                    },
            onValueChange = {},
            label = {
              when (mode) {
                "category" -> Text(context.getString(R.string.activity_category))
                "type" -> Text(context.getString(R.string.activity_type))
                "interest" -> Text(context.getString(R.string.activity_interest))
                else -> {}
              }
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier =
                Modifier.menuAnchor()
                    .fillMaxWidth()
                    .testTag(
                        when (mode) {
                          "category" -> "categoryTextField"
                          "type" -> "typeTextField"
                          "interest" -> "interestTextField"
                          else -> ""
                        }))
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
