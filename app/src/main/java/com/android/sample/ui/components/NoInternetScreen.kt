package com.android.sample.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.resources.C.Tag.LARGE_PADDING
import com.android.sample.resources.C.Tag.TITLE_FONTSIZE

@Composable
fun NoInternetScreen() {
  val isSpinning = true
  Box(
      modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
      contentAlignment = Alignment.Center) {
        val logo = painterResource(id = R.drawable.aptivity)
        if (isSpinning) {
          val infiniteTransition = rememberInfiniteTransition(label = "")

          // Animation for rotation
          val rotation by
              infiniteTransition.animateFloat(
                  initialValue = 0f,
                  targetValue = 360f,
                  animationSpec =
                      infiniteRepeatable(
                          animation = tween(durationMillis = 1500, easing = LinearEasing)),
                  label = "")

          // Animation for fading
          val alpha by
              infiniteTransition.animateFloat(
                  initialValue = 0f,
                  targetValue = 1f,
                  animationSpec =
                      infiniteRepeatable(
                          animation = tween(durationMillis = 1000, easing = LinearEasing),
                          repeatMode = RepeatMode.Reverse),
                  label = "")

          Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center,
              modifier = Modifier) {
                Image(
                    painter = logo,
                    contentDescription = "Loading Logo",
                    modifier = Modifier.size(200.dp).graphicsLayer(rotationZ = rotation))
                Spacer(modifier = Modifier.height(LARGE_PADDING.dp))
                Text(
                    text = "No Internet Connection",
                    style =
                        TextStyle(
                            fontSize = TITLE_FONTSIZE.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black // Ensure text is visible on the dark background
                            ),
                    modifier = Modifier.graphicsLayer(alpha = alpha) // Apply alpha animation
                    )
              }
        } else {
          Image(
              painter = logo, contentDescription = "Loading Logo", modifier = Modifier.size(100.dp))
        }
      }
}
