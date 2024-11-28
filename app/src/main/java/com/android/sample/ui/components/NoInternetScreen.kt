package com.android.sample.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.resources.C.Tag.LARGE_PADDING
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.SUBTITLE_FONTSIZE
import com.android.sample.resources.C.Tag.TITLE_FONTSIZE

@Composable
fun NoInternetScreen(paddingValues: PaddingValues) {
  Column(
      horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
      verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
      modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        Image(
            painter = painterResource(id = R.drawable.no_signal),
            contentDescription = R.string.no_internet_connection.toString())
        Spacer(modifier = Modifier.padding(LARGE_PADDING.dp))
        Text(
            text = R.string.no_internet_connection.toString(),
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = TITLE_FONTSIZE.sp))
        Spacer(modifier = Modifier.padding(MEDIUM_PADDING.dp))
        Text(
            text = R.string.internet_connection_ask.toString(),
            style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = SUBTITLE_FONTSIZE.sp))
      }
}
