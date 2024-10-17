package com.android.sample.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.User
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    profileViewModel: ProfileViewModel ,
    navigationActions: NavigationActions,
    activitiesViewModel: ListActivitiesViewModel
) {
    val profile =
        profileViewModel.userState.collectAsState().value
            ?: return Text(text = "No profile selected. Should not happen", color = Color.Red)

    var name by remember { mutableStateOf(profile.name) }
    var surname by remember { mutableStateOf(profile.surname) }
    var interests by remember { mutableStateOf(profile.interests) }
    var activities by remember { mutableStateOf(profile.activities) }
    var photo by remember { mutableStateOf(profile.photo) }


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
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Title Input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    placeholder = { Text("Your Name") },
                    modifier = Modifier.fillMaxWidth().testTag("inputProfileName"))

                // Description Input
                OutlinedTextField(
                    value = surname,
                    onValueChange = { surname = it },
                    label = { Text("Surname") },
                    placeholder = { Text("Your surname") },
                    modifier = Modifier.fillMaxWidth().height(100.dp).testTag("inputProfileSurname"))

                // Assignee Input
                OutlinedTextField(
                    value = interests.toString(),
                    onValueChange = {  },
                    label = { Text("Interests") },
                    placeholder = { Text("Your interests") },
                    modifier = Modifier.fillMaxWidth().testTag("inputProfileInterests"))

                ProfileImage(
                    url = photo,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .testTag("profilePicture")
                )
                ActivitiesList(activitiesViewModel, profileViewModel, navigationActions)
                // Save Button
                Button(
                    onClick = {
                        try{
                            profileViewModel.updateProfile(
                                User(
                                    id = profile.id,
                                    name = name,
                                    surname = surname,
                                    interests = interests,
                                    activities = activities,
                                    photo = profile.photo

                                )
                            )
                            navigationActions.goBack()
                            return@Button
                        } catch (_: NumberFormatException) {}

                    },
                    modifier = Modifier.fillMaxWidth().testTag("ProfileSave")
                ) {
                    Text("Save", color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Delete Button

            }
        })
}

@Composable
fun ActivitiesList(activitiesViewModel: ListActivitiesViewModel, profileViewModel: ProfileViewModel,
                   navigationActions: NavigationActions) {
    val userState = profileViewModel.userState.collectAsState().value
        ?: return Text(text = "No profile selected. Should not happen", color = Color.Red)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Your Activities" , modifier = Modifier.padding(8.dp))

        LazyRow {
            userState.activities?.let {
                items(it.size) { index ->
                    ActivityCard(activityId = userState.activities[index], profileViewModel,
                        navigationActions = navigationActions, activitiesViewModel = activitiesViewModel)
                }
            }
        }
    }
}

@Composable
fun ActivityCard(activityId: String, profileViewModel: ProfileViewModel,
                 navigationActions: NavigationActions,
                 activitiesViewModel: ListActivitiesViewModel) {
    activitiesViewModel.getActivityById(activityId)  // Assume this method fetches the activity details from your ViewModel
    val activity = activitiesViewModel.selectedActivity.collectAsState().value
        ?: return Text("No selectedActivity. Should not happen")
    val profile = profileViewModel.userState.collectAsState().value ?: return
    Text(text = "No Profile selected. Should not happen", color = Color.Red)

    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(200.dp)
            .height(100.dp)
    ) {
        Column {
            Text(text = "Activity ID: $activityId", modifier = Modifier.padding(8.dp))
            Button(
                onClick = {
                    profileViewModel.deleteActivityFromProfile(profile.id, activityId)
                    navigationActions.goBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .testTag("deleteActivity_$activityId")
            ) {
                Text("Delete", color = Color.White)
            }


            // Show the Edit button only if the organizerName matches profile.id
            if (activity.creator == profile.id) {
                Button(
                    onClick = {
                        // Define the navigation action for editing the activity
                        navigationActions.navigateTo(Screen.EDIT_ACTIVITY)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .testTag("editActivity_$activityId")
                ) {
                    Text("EditYourActivity", color = Color.White)
                }
            }
        }
    }
}

