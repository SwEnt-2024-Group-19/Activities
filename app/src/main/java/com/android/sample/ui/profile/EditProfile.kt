package com.android.sample.ui.profile

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.activity.Category
import com.android.sample.model.activity.categories
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.network.NetworkManager
import com.android.sample.model.profile.Interest
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.User
import com.android.sample.model.profile.interestStringValues
import com.android.sample.resources.C.Tag.CARD_ELEVATION_DEFAULT
import com.android.sample.resources.C.Tag.IMAGE_SIZE
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.ROUNDED_CORNER_SHAPE_DEFAULT
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.resources.C.Tag.SUBTITLE_FONTSIZE
import com.android.sample.resources.C.Tag.TEXT_FONTSIZE
import com.android.sample.resources.C.Tag.colorOfCategory
import com.android.sample.ui.camera.CameraScreen
import com.android.sample.ui.camera.GalleryScreen
import com.android.sample.ui.camera.ProfileImage
import com.android.sample.ui.components.performOfflineAwareAction
import com.android.sample.ui.dialogs.AddImageDialog
import com.android.sample.ui.navigation.NavigationActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    profileViewModel: ProfileViewModel,
    navigationActions: NavigationActions,
    imageViewModel: ImageViewModel
) {
  val profile =
      profileViewModel.userState.collectAsState().value
          ?: return Text(text = "No profile selected.", color = Color.Red)

  var name by remember { mutableStateOf(profile.name) }
  var surname by remember { mutableStateOf(profile.surname) }
  var interests by remember { mutableStateOf(profile.interests) }
  var isCamOpen by remember { mutableStateOf(false) }
  var isGalleryOpen by remember { mutableStateOf(false) }
  var showDialogImage by remember { mutableStateOf(false) }
  var photo by remember { mutableStateOf(profile.photo) }
  val scrollState = rememberScrollState()

  val context = LocalContext.current
  val networkManager = NetworkManager(context)
  var selectedImage by remember { mutableStateOf<Bitmap?>(null) }
  var isPictureRemoved by remember { mutableStateOf(false) }
  Scaffold(
      modifier = Modifier.testTag("editProfileScreen"),
      topBar = {
        TopAppBar(
            title = { Text("Edit Profile", modifier = Modifier.testTag("editProfileTitle")) },
            navigationIcon = {
              IconButton(
                  modifier = Modifier.testTag("goBackButton"),
                  onClick = { navigationActions.goBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back")
                  }
            })
      },
      content = { paddingValues ->
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
              })
        }

        if (isGalleryOpen) {
          GalleryScreen(
              isGalleryOpen = { isGalleryOpen = false },
              addImage = { bitmap -> selectedImage = bitmap },
              context = context)
        }
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
              addElem = { bitmap -> selectedImage = bitmap })
        } else {
          Column(
              modifier =
                  Modifier.fillMaxSize()
                      .padding(paddingValues)
                      .padding(MEDIUM_PADDING.dp)
                      .testTag("editProfileContent")
                      .verticalScroll(scrollState),
              verticalArrangement = Arrangement.spacedBy(STANDARD_PADDING.dp)) {
                ProfileImage(
                    userId = profile.id,
                    modifier =
                        Modifier.size(IMAGE_SIZE.dp).clip(CircleShape).testTag("profilePicture"),
                    imageViewModel,
                    editing = true,
                    bitmap = selectedImage,
                    onRemoveImage = {
                      selectedImage = null
                      isPictureRemoved = true // Mark picture for removal
                    })

                ModifyPictureButton(
                    context = context,
                    networkManager = networkManager,
                    onPerformAction = { showDialogImage = true })

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    placeholder = { Text("Your Name") },
                    modifier = Modifier.fillMaxWidth().testTag("inputProfileName"))
                OutlinedTextField(
                    value = surname,
                    onValueChange = { surname = it },
                    label = { Text("Surname") },
                    placeholder = { Text("Your surname") },
                    modifier =
                        Modifier.fillMaxWidth()
                            .height(IMAGE_SIZE.dp)
                            .testTag("inputProfileSurname"))

                // Interest list and add button

                var newListInterests by remember { mutableStateOf(interests ?: emptyList()) }

                ManageInterests(
                    initialInterests = interests ?: emptyList(),
                    onUpdateInterests = { newListInterests = it })

                Button(
                    onClick = {
                      if (isPictureRemoved) {
                        imageViewModel.deleteProfilePicture(
                            profile.id,
                            onSuccess = {
                              photo = null // Clear photo reference
                            },
                            onFailure = { error -> })
                      }
                      selectedImage?.let { bitmap ->
                        imageViewModel.uploadProfilePicture(
                            profile.id,
                            bitmap,
                            onSuccess = { url ->
                              photo = url // Update photo URL in profile
                            },
                            onFailure = { error -> })
                      }
                      try {
                        profileViewModel.updateProfile(
                            User(
                                id = profile.id,
                                name = name,
                                surname = surname,
                                interests = newListInterests,
                                activities = profile.activities,
                                photo = photo,
                                likedActivities = profile.likedActivities))
                        navigationActions.goBack()
                      } catch (_: NumberFormatException) {}
                      performOfflineAwareAction(
                          context = context,
                          networkManager = networkManager,
                          onPerform = {
                            selectedImage?.let { bitmap ->
                              imageViewModel.uploadProfilePicture(
                                  profile.id,
                                  bitmap,
                                  onSuccess = { url ->
                                    photo = url // Update photo URL in profile
                                  },
                                  onFailure = { error -> })
                            }
                          })
                    },
                    modifier = Modifier.fillMaxWidth().testTag("profileSaveButton")) {
                      Text("Save", color = Color.White)
                    }
              }
        }
      })
}

@Composable
fun ManageInterests(initialInterests: List<Interest>, onUpdateInterests: (List<Interest>) -> Unit) {

  var newListInterests by remember { mutableStateOf(initialInterests) }
  var newInterest: Interest? by remember { mutableStateOf(null) }
  val context = LocalContext.current
  val networkManager = NetworkManager(context)

  Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(STANDARD_PADDING.dp),
      horizontalAlignment = Alignment.CenterHorizontally) {
        // Input Row
        InterestInputRow(onInterestChange = { newInterest = it })
        Spacer(Modifier.height(STANDARD_PADDING.dp))
        // Add Button
        Button(
            onClick = {
              performOfflineAwareAction(
                  context = context,
                  networkManager = networkManager,
                  onPerform = {
                    if (newInterest != null) {
                      val updatedList = newListInterests + newInterest!!
                      newListInterests = updatedList
                      newInterest = null
                      onUpdateInterests(updatedList)
                    }
                  })
            },
            enabled = newInterest != null,
            modifier = Modifier.testTag("addInterestButton"),
            shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE_DEFAULT.dp)) {
              Text("Add Interest")
            }
        // Interests List
        LazyRow(
            modifier = Modifier.testTag("interestsList"),
            horizontalArrangement = Arrangement.spacedBy(STANDARD_PADDING.dp)) {
              items(newListInterests.size) { interest ->
                InterestEditBox(
                    interest = newListInterests[interest],
                    onRemove = {
                      performOfflineAwareAction(
                          context = context,
                          networkManager = networkManager,
                          onPerform = {
                            val updatedList = newListInterests - newListInterests[interest]
                            newListInterests = updatedList
                            onUpdateInterests(updatedList)
                          })
                    })
              }
            }
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestInputRow(onInterestChange: (Interest?) -> Unit) {
  var selectedCategory by remember { mutableStateOf<Category?>(null) }
  var expandedCategory by remember { mutableStateOf(false) }
  var selectedInterest by remember { mutableStateOf<String?>(null) }
  var expandedInterest by remember { mutableStateOf(false) }
  val context = LocalContext.current

  Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(STANDARD_PADDING.dp)) {
        // Category Dropdown
        Card(
            modifier = Modifier.weight(1f).testTag("TextFieldWithErrorStateCard"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            elevation = CardDefaults.cardElevation(defaultElevation = CARD_ELEVATION_DEFAULT.dp),
            shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE_DEFAULT.dp)) {
              ExposedDropdownMenuBox(
                  expanded = expandedCategory, onExpandedChange = { expandedCategory = it }) {
                    OutlinedTextField(
                        value =
                            selectedCategory?.name
                                ?: context.getString(R.string.select_activity_category),
                        onValueChange = { selectedCategory = Category.valueOf(it) },
                        label = { Text("Category") },
                        readOnly = true,
                        modifier = Modifier.menuAnchor().testTag("categoryDropdown"),
                        colors =
                            TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent,
                                errorContainerColor = Color.Transparent))

                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }) {
                          categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                  selectedCategory = category
                                  selectedInterest = null
                                  onInterestChange(null)
                                  expandedCategory = false
                                })
                          }
                        }
                  }
            }

        // New Interest Input
        Card(
            modifier = Modifier.weight(1f).testTag("TextFieldWithErrorStateCard"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            elevation = CardDefaults.cardElevation(defaultElevation = CARD_ELEVATION_DEFAULT.dp),
            shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE_DEFAULT.dp)) {
              ExposedDropdownMenuBox(
                  expanded = expandedInterest, onExpandedChange = { expandedInterest = it }) {
                    OutlinedTextField(
                        value =
                            selectedInterest ?: context.getString(R.string.select_activity_type),
                        onValueChange = { selectedInterest = it },
                        label = { Text("Interest") },
                        readOnly = true,
                        modifier = Modifier.menuAnchor().testTag("interestDropdown"),
                        colors =
                            TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent,
                                errorContainerColor = Color.Transparent))

                    ExposedDropdownMenu(
                        expanded = expandedInterest,
                        onDismissRequest = { expandedInterest = false }) {
                          interestStringValues[selectedCategory]?.forEach { interest ->
                            DropdownMenuItem(
                                text = { Text(interest) },
                                onClick = {
                                  selectedInterest = interest
                                  onInterestChange(Interest(interest, selectedCategory!!))
                                  expandedInterest = false
                                })
                          }
                        }
                  }
            }
      }
}

@Composable
fun InterestEditBox(interest: Interest, onRemove: () -> Unit) {
  Box(
      modifier =
          Modifier.background(
                  color = colorOfCategory(interest.category),
                  shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE_DEFAULT.dp))
              .padding(horizontal = MEDIUM_PADDING.dp)
              .testTag("$interest"),
      contentAlignment = Alignment.Center // Ensures content is centered within the Box
      ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.SpaceBetween, // Ensures spacing between Text and Icon
            modifier = Modifier.fillMaxWidth() // Allows Row to take full width of the Box
            ) {
              Text(
                  text = interest.name,
                  fontSize = SUBTITLE_FONTSIZE.sp,
                  color = Color.Black,
                  modifier =
                      Modifier.padding(vertical = MEDIUM_PADDING.dp, horizontal = SMALL_PADDING.dp))
              IconButton(
                  onClick = onRemove,
                  modifier =
                      Modifier.size(TEXT_FONTSIZE.dp) // Optional: Adjust size of the IconButton
                          .testTag("removeInterest-$interest")) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        modifier = Modifier.fillMaxSize())
                  }
            }
      }
}

@Composable
fun ModifyPictureButton(
    context: Context,
    networkManager: NetworkManager,
    onPerformAction: () -> Unit,
    modifier: Modifier = Modifier.testTag("uploadPicture")
) {
  Button(
      onClick = {
        performOfflineAwareAction(
            context = context, networkManager = networkManager, onPerform = onPerformAction)
      },
      modifier = modifier) {
        Text("Modify Profile Picture")
      }
}
