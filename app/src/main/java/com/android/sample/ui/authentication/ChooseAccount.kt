// ChooseAccount.kt
package com.android.sample.ui.authentication

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.sample.R
import com.android.sample.model.auth.SignInViewModel
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.resources.C.Tag.BUTTON_HEIGHT_MD
import com.android.sample.resources.C.Tag.CARD_ELEVATION_DEFAULT
import com.android.sample.resources.C.Tag.HALF_SCREEN_TEXT_FIELD_PADDING
import com.android.sample.resources.C.Tag.LINE_STROKE
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.ROUNDED_CORNER_SHAPE_DEFAULT
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.resources.C.Tag.SUBTITLE_FONTSIZE
import com.android.sample.resources.C.Tag.TITLE_FONTSIZE
import com.android.sample.resources.C.Tag.WIDTH_FRACTION_SM
import com.android.sample.ui.components.LoadingScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen

@Composable
fun ChooseAccountScreen(
    navigationActions: NavigationActions,
    signInViewModel: SignInViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    imageViewModel: ImageViewModel = hiltViewModel()
) {
  // Collect the user profile data from ProfileViewModel
  val userProfile by profileViewModel.userState.collectAsState()
    val isLoading = userProfile == null // Assume loading state when userProfile is null
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LoadingScreen()
        }
        return
    }

  if (userProfile != null) {
    navigationActions.navigateTo(Screen.OVERVIEW)
      return
  }
  val continueMessage = stringResource(R.string.complete_profile_creation_message)

  Box(modifier = Modifier.fillMaxSize()) {
    // Background Image
    Image(
        painter =
            painterResource(
                id = R.drawable.background_image), // Replace with your drawable resource
        contentDescription = null,
        modifier = Modifier.fillMaxSize().alpha(0.2f),
        contentScale = ContentScale.Crop, // Ensures the image fills the entire screen
    )
  }
  LazyColumn(
      modifier =
          Modifier.fillMaxSize()
              .padding(horizontal = MEDIUM_PADDING.dp)
              .testTag("chooseAccountScreen"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
  ) {
    // Greeting Text
    item {
      Text(
          text = stringResource(R.string.already_signed_in_message),
          fontSize = TITLE_FONTSIZE.sp,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(bottom = MEDIUM_PADDING.dp).testTag("greetingText"),
          overflow = TextOverflow.Ellipsis,
          maxLines = 2)
    }

    item { Spacer(modifier = Modifier.height((2 * HALF_SCREEN_TEXT_FIELD_PADDING).dp)) }
    // Continue Button
    item {
      Card(
          modifier = Modifier.fillMaxWidth(WIDTH_FRACTION_SM).testTag("ChooseAccountCard"),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
          elevation = CardDefaults.cardElevation(defaultElevation = CARD_ELEVATION_DEFAULT.dp),
          shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE_DEFAULT.dp)) {
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 0.dp, vertical = 0.dp)) {
              OutlinedButton(
                  onClick = { navigationActions.navigateTo(Screen.CREATE_PROFILE) },
                  shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE_DEFAULT.dp),
                  modifier =
                      Modifier.height(BUTTON_HEIGHT_MD.dp).fillMaxWidth().testTag("continueText"),
                  border = BorderStroke(LINE_STROKE.dp, Color.Transparent), // Transparent indicator
                  colors =
                      ButtonDefaults.buttonColors(
                          containerColor = MaterialTheme.colorScheme.primary,
                          contentColor = Color.White)) {
                    Text(
                        text = continueMessage,
                        fontSize = SUBTITLE_FONTSIZE.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = STANDARD_PADDING.dp))
                  }
            }
          }
    }

    item { Spacer(modifier = Modifier.height(MEDIUM_PADDING.dp)) }

    // Switch Account Button
    item {
      Text(
          text = stringResource(R.string.switch_account_message),
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
