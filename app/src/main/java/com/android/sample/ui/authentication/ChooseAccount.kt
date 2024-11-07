// ChooseAccount.kt
package com.android.sample.ui.authentication

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.sample.model.auth.SignInViewModel
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.ui.ProfileImage
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen

@Composable
fun ChooseAccountScreen(
    navigationActions: NavigationActions,
    signInViewModel: SignInViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
  // Collect the user profile data from ProfileViewModel
  val userProfile by profileViewModel.userState.collectAsState()

  LazyColumn(
      modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp).testTag("chooseAccountScreen"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        // Greeting Text
        item {
          Text(
              text = "Hello ${userProfile?.name ?: "User"}, you are already signed in!",
              fontSize = 24.sp,
              fontWeight = FontWeight.Bold,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(bottom = 16.dp).testTag("greetingText"),
              overflow = TextOverflow.Ellipsis,
              maxLines = 2)
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Profile Image
        item {
          ProfileImage(
              url = userProfile?.photo,
              modifier = Modifier.size(100.dp).clip(CircleShape).testTag("profilePicture"))
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Continue as Text (Clickable)
        item {
          Text(
              text = "Continue as ${userProfile?.name}",
              fontSize = 16.sp,
              color = Color.Blue,
              fontWeight = FontWeight.SemiBold,
              textDecoration = TextDecoration.Underline,
              modifier =
                  Modifier.padding(vertical = 8.dp)
                      .clickable { navigationActions.navigateTo(Screen.OVERVIEW) }
                      .testTag("continueText"))
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Switch Account Button
        item {
          Button(
              onClick = {
                signInViewModel.signOut()
                navigationActions.navigateTo(Screen.AUTH)
              },
              modifier =
                  Modifier.fillMaxWidth(0.8f)
                      .height(48.dp)
                      .clip(RoundedCornerShape(12.dp))
                      .testTag("switchAccountButton")) {
                Text(
                    text = "Switch Account",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp))
              }
        }
      }
}
