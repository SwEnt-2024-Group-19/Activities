package com.android.sample.ui.activity

import android.graphics.fonts.FontStyle
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityRepositoryFirestore
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.ListActivityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateActivityScreen(lAV: ListActivityViewModel,
                         aRF: ActivityRepositoryFirestore,
                         ) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var creator by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var placesLeft by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            ,
        topBar = {
            TopAppBar(
                title = { Text("Create a new activity") },
                navigationIcon = { Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "", // Add a valid content description
                )},
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFFF0F0F0)
                    ))
        },
        content = { paddingValues ->
            Column (
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(color = Color(0xFFFFFFFF)),
            ){
                Carousel()
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value =title ,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    placeholder = { Text("Give a title of the activity") },
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value =description ,
                    onValueChange = {
                        description = it
                    },
                    label = { Text("Description") },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    placeholder = { Text("Describe the activity") },
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value =dueDate ,
                    onValueChange = {
                        dueDate = it
                    },
                    label = { Text("Date") },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    placeholder = { Text("dd/mm/yyyy") },
                )
                Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value =price ,
                        onValueChange = {
                            price = it
                        },
                        label = { Text("Price") },
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        placeholder = { Text("Price/person") },
                    )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value =placesLeft ,
                    onValueChange = {
                        placesLeft = it
                    },
                    label = { Text("Places Left") },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    placeholder = { Text("Places left/Total places") },
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value =location ,
                    onValueChange = {
                        location = it
                    },
                    label = { Text("Location") },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    placeholder = { Text("Where is it taking place") },
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        val activity = Activity(
                            uid=aRF.getNewUid(),
                            title = title,
                            description = description,
                            date = dueDate,
                            price = price.toDouble(),
                            placesLeft = parseFraction(placesLeft,0) ?: 0,
                            maxPlaces = parseFraction(placesLeft,2) ?:0,
                            creator = creator,
                            status = ActivityStatus.ACTIVE,
                            location = location,
                            images = listOf(),

                            )
                        lAV.addActivity(activity)

                    },
                    modifier = Modifier
                        .width(300.dp)
                        .height(40.dp)
                        .align(Alignment.CenterHorizontally),


                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,

                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "add a new activity",
                        )
                        Text("Create")
                        }
                }
            }
        },
    )

}

data class CarouselItem(
    val id: Int,
    @DrawableRes val imageResId: Int,
    val contentDescription: String,
)

var items =
    listOf(
        CarouselItem(0, R.drawable.ic_launcher_background, "" /* Add a description for the image */),
        CarouselItem(1, R.drawable.ic_launcher_background, "" /* Add a description for the image */),
        CarouselItem(2, R.drawable.ic_launcher_background, "" /* Add a description for the image */),
        CarouselItem(3, R.drawable.ic_launcher_background, "" /* Add a description for the image */),
        CarouselItem(4, R.drawable.ic_launcher_background, "" /* Add a description for the image */),
        CarouselItem(5, R.drawable.ic_launcher_background, "" /* Add a description for the image */),
    )


@Composable
fun Carousel() {
    Row ( modifier = Modifier
        .fillMaxWidth()
        .height(135.dp)
        .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        LazyRow(
            modifier = Modifier
                .width(340.dp)
                .height(135.dp)
               ,
        ) {
            items(items.size) { index ->
                Image(
                    painter = painterResource(id = items[index].imageResId),
                    contentDescription = items[index].contentDescription,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier
                .padding(16.dp)
                .size(30.dp)
                .background(Color(0xFFFFFFFF)), // Use size modifier for simplicity
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End // Center the icon horizontally
        ) {
            FloatingActionButton(
                content = {
                    Icon(imageVector = Icons.Outlined.AddCircle, contentDescription = "Add a new image")

                },
                containerColor = Color(0xFFFFFFFF),
                onClick = { /*TODO*/ },
                modifier = Modifier.size(50.dp)
                    .background(Color(0xFFFFFFFF)),)

            Text(
                text = "Add Image",
                modifier = Modifier.padding(8.dp),
                color = Color(0xFF000000),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                ),
            )

        }
    }
}

fun parseFraction(fraction: String,index:Int): Int? {
    val parts = fraction.split("/")
    return if (parts.size == 2)
        parts[0].toIntOrNull()
     else null
}
