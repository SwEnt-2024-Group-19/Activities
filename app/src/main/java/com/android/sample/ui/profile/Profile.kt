package com.android.sample.ui.profile


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*

@Composable
fun ProfileScreen() {

    var name by remember { mutableStateOf("") }
    var newInterest by remember { mutableStateOf("") }
    var interests by remember { mutableStateOf(listOf<String>()) }

    Scaffold(
       // bottomBar = {}
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Profile",
                fontSize = 30.sp,
                modifier = Modifier.padding(top = 16.dp)
            )

            // Profile Picture
            Box(
                modifier = Modifier
                    .size(100.dp) // Size of the circle
                    .clip(CircleShape)
                    .background(Color.Gray) // Placeholder color
                    .padding(16.dp), // Padding inside the circle
                contentAlignment = Alignment.Center
            ) {
                // Replace with an Image when available
                // Image(painter = painterResource(R.drawable.profile_pic), contentDescription = null)
            }

            // Name
            Text(
                text = name,
                fontSize = 20.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Interests Section
            Text(
                text = "Interests",
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )

            // Input field to add interests
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                TextField(
                    value = newInterest,
                    onValueChange = { newInterest = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(text = "Add interest") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(Color.Blue)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Add",
                        color = Color.White,
                        modifier = Modifier.clickable {
                            if (newInterest.isNotBlank()) {
                                interests = interests + newInterest
                                newInterest = ""
                            }
                        }
                    )
                }
            }

            // List of interests
            LazyColumn(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(interests.size) { index ->
                    InterestItem(interests[index])
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Activities Section
            Text(
                text = "Activities Created",
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )

            // Scrollable Lazy Column for activities
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(10) { index -> // Replace 10 with your activities list size
                    ActivityBox(index + 1)
                }
            }
        }


            }


        }
//how do I get access to the activity views ?

@Composable
fun ActivityBox(activityNumber: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(60.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray), // Box background color
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Activity $activityNumber", fontSize = 18.sp)
    }
}

@Composable
fun InterestItem(interest: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray)
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = interest, fontSize = 18.sp)
    }
}
