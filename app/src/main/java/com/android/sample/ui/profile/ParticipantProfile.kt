package com.android.sample.ui.profile

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.User
import com.android.sample.ui.camera.ProfileImage
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun ParticipantProfileScreen(
    listActivitiesViewModel: ListActivitiesViewModel,
    navigationActions: NavigationActions
    ) {

    val selectedParticipant = listActivitiesViewModel.selectedUser.collectAsState().value
    when (val profile = selectedParticipant) {
        null -> ParticipantLoadingScreen(navigationActions) // Show a loading indicator or a retry button
        else -> {
            ParticipantProfileContent(
                user= profile, navigationActions, listActivitiesViewModel)
        }
        // Proceed with showing profile content
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantLoadingScreen(navigationActions: NavigationActions) {
    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("loadingScreen"),
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(
                        onClick = { navigationActions.goBack() },
                        modifier = Modifier.testTag("goBackButton")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back")
                    }
                })
        }) { innerPadding ->
        Column(
            Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "No information available for this participant",
                modifier = Modifier.testTag("loadingText"),
                color = Color.Black)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantProfileContent(
    user: User,
    navigationActions: NavigationActions,
    listActivitiesViewModel: ListActivitiesViewModel,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("profileScreen"),
        topBar = {
            TopAppBar(
                title = { Text("Participant's Profile") },
                navigationIcon = {
                    IconButton(
                        onClick = { navigationActions.goBack() },
                        modifier = Modifier.testTag("goBackButton")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back")
                    }
                }, modifier = Modifier.testTag("topAppBar"))
        }) { innerPadding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally) {
            item {
                Spacer(Modifier.height(16.dp))
                Text(text = "Profile", fontSize = 30.sp, modifier = Modifier.padding(top = 16.dp))

                // Profile Picture
                ProfileImage(
                    userId = user.id,
                    modifier = Modifier.size(100.dp).clip(CircleShape).testTag("profilePicture"))

                // User Name and Surname
                Text(
                    text = "${user.name} ${user.surname}",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 8.dp).testTag("userName"))
            }
            item {
                // Interests Section
                Text(
                    text = "Interests",
                    fontSize = 24.sp,
                    modifier =
                    Modifier.padding(start = 16.dp, top = 16.dp).testTag("interestsSection"))
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)) {
                    user.interests?.let { interests ->
                        items(interests.size) { index ->
                            InterestBox(interest = user.interests[index])
                        }
                    }
                }
            }

            item {

                // Activities Section

                Text(
                    text = "Activities Created",
                    fontSize = 24.sp,
                    modifier =
                    Modifier.padding(start = 16.dp, top = 16.dp)
                        .testTag("activitiesCreatedTitle"))
            }

            // Activities Created

            user.activities?.let { activities ->
                items(activities.size) { index ->
                    ActivityCreatedBox(
                        activity = activities[index],
                        user,
                        listActivitiesViewModel,
                        navigationActions)
                }
            }

            item { // Activities Enrolled in
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Activities Enrolled in",
                    fontSize = 24.sp,
                    modifier =
                    Modifier.padding(start = 16.dp, top = 16.dp)
                        .testTag("activitiesEnrolledTitle"))
            }

            // Activities Enrolled

            user.activities?.let { activities ->
                items(activities.size) { index ->
                    ActivityEnrolledBox(
                        activity = activities[index],
                        user,
                        listActivitiesViewModel,
                        navigationActions)
                }
            }
        }
    }
}

