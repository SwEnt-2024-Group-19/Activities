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
import com.android.sample.resources.C.Tag.BUTTON_WIDTH
import com.android.sample.resources.C.Tag.IMAGE_SIZE
import com.android.sample.resources.C.Tag.LARGE_BUTTON_HEIGHT
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.resources.C.Tag.SUBTITLE_FONTSIZE
import com.android.sample.resources.C.Tag.TITLE_FONTSIZE
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
  val continueMessage =
      if (userProfile != null) {
        "Continue as ${userProfile?.name}"
      } else "Complete profile creation"
  LazyColumn(
      modifier =
          Modifier.fillMaxSize()
              .padding(horizontal = MEDIUM_PADDING.dp)
              .testTag("chooseAccountScreen"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        // Greeting Text
        item {
          Text(
              text = "Hello ${userProfile?.name ?: "User"}, you are already signed in!",
              fontSize = TITLE_FONTSIZE.sp,
              fontWeight = FontWeight.Bold,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(bottom = MEDIUM_PADDING.dp).testTag("greetingText"),
              overflow = TextOverflow.Ellipsis,
              maxLines = 2)
        }

        item { Spacer(modifier = Modifier.height(MEDIUM_PADDING.dp)) }

        // Profile Image
        item {
          userProfile?.id?.let {
            ProfileImage(
                userId = it,
                modifier = Modifier.size(IMAGE_SIZE.dp).clip(CircleShape).testTag("profilePicture"))
          }
        }

        item { Spacer(modifier = Modifier.height(MEDIUM_PADDING.dp)) }
        // Continue Button
        item {
          Button(
              onClick = {
                if (profileViewModel.userState.value != null) {
                  navigationActions.navigateTo(Screen.OVERVIEW)
                } else navigationActions.navigateTo(Screen.CREATE_PROFILE)
              },
              modifier =
                  Modifier.width(BUTTON_WIDTH.dp)
                      .height(LARGE_BUTTON_HEIGHT.dp)
                      .clip(RoundedCornerShape(MEDIUM_PADDING.dp))
                      .testTag("continueText")) {
                Text(
                    text = continueMessage,
                    fontSize = SUBTITLE_FONTSIZE.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = STANDARD_PADDING.dp))
              }
        }

        item { Spacer(modifier = Modifier.height(MEDIUM_PADDING.dp)) }

        // Switch Account Button
        item {
          Text(
              text = "Switch Account",
              fontSize = SUBTITLE_FONTSIZE.sp,
              color = Color.Blue,
              fontWeight = FontWeight.SemiBold,
              textDecoration = TextDecoration.Underline,
              modifier =
                  Modifier.padding(vertical = STANDARD_PADDING.dp)
                      .clickable {
                        signInViewModel.signOut()
                        navigationActions.navigateTo(Screen.AUTH)
                      }
                      .testTag("switchAccountButton"))
        }
      }
}
