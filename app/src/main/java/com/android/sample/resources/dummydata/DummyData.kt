package com.android.sample.resources.dummydata

import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.ActivityType
import com.android.sample.model.activity.Comment
import com.android.sample.model.map.Location
import com.android.sample.model.profile.User
import com.google.firebase.Timestamp
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.UUID

val timestamp = Timestamp.now()
val location = Location(46.519962, 6.633597, "EPFL")
val location2 = Location(46.5, 6.6, "Lausanne")
val locationList = listOf(location, location2)
val activity =
    Activity(
        "1",
        "First Activity",
        "Do something",
        creator = "John Doe",
        date = Timestamp(GregorianCalendar(2024, 8, 5).time),
        location = location,
        status = ActivityStatus.ACTIVE,
        participants = listOf(),
        price = 10.0,
        placesLeft = 10,
        maxPlaces = 20,
        images = listOf(),
        type = ActivityType.PRO,
        startTime = "09:30",
        duration = "00:30")

val testComment =
    Comment(
        uid = UUID.randomUUID().toString(),
        userId = "123",
        userName = "Amine",
        content = "This is a comment",
        timestamp = timestamp,
        replies =
            listOf(
                Comment(
                    uid = UUID.randomUUID().toString(),
                    userId = "124",
                    userName = "John",
                    content = "This is a reply",
                    timestamp = timestamp)))
val userProfile =
    User(
        name = "John Doe",
        photo = "https://example.com/photo.jpg",
        interests = listOf(),
        surname = "Doe",
        id = "123",
        activities = listOf())

const val password = "testPassword"

val activityWithParticipants =
    Activity(
        uid = "123",
        title = "Sample Activity",
        description = "Sample Description",
        date = Timestamp(GregorianCalendar(2025, Calendar.NOVEMBER, 3).time),
        price = 10.0,
        placesLeft = 5,
        maxPlaces = 10,
        creator = "Creator",
        status = ActivityStatus.ACTIVE,
        location = Location(46.519962, 6.633597, "EPFL"),
        images = listOf("1"),
        participants =
            listOf(
                User(
                    id = "1",
                    name = "Amine",
                    surname = "A",
                    interests = listOf("Cycling"),
                    activities = listOf(),
                    photo = "",
                    likedActivities = listOf("1")),
                User(
                    id = "2",
                    name = "John",
                    surname = "Doe",
                    interests = listOf("Reading"),
                    activities = listOf(),
                    photo = "",
                    likedActivities = listOf("1"))),
        duration = "02:00",
        startTime = "10:00",
        type = ActivityType.INDIVIDUAL,
        comments = listOf())

val simpleUser =
    User(
        id = "",
        name = "John",
        surname = "Doe",
        interests = listOf(),
        activities = listOf(),
        photo = null,
        likedActivities = listOf())

val emptyUser =
    User(
        id = "",
        name = "",
        surname = "",
        interests = listOf(),
        activities = listOf(),
        photo = null,
        likedActivities = listOf())

val activityBiking =
    Activity(
        uid = "1",
        title = "Mountain Biking",
        description = "Exciting mountain biking experience.",
        date = Timestamp.now(),
        location = Location(46.519962, 6.633597, "EPFL"),
        creator = "Chris",
        images = listOf(),
        price = 10.0,
        status = ActivityStatus.ACTIVE,
        type = ActivityType.PRO,
        placesLeft = 8,
        maxPlaces = 15,
        participants =
            listOf(
                User(
                    id = "1",
                    name = "Amine",
                    surname = "A",
                    interests = listOf("Cycling"),
                    activities = listOf(),
                    photo = "",
                    likedActivities = listOf("1")),
                User(
                    id = "2",
                    name = "John",
                    surname = "Doe",
                    interests = listOf("Reading"),
                    activities = listOf(),
                    photo = "",
                    likedActivities = listOf("1"))),
        duration = "2 hours",
        startTime = "10:00")

val testUserId = "testUser123"
val listOfActivitiesUid = listOf("3", "2")

val activity1 =
    Activity(
        uid = "3",
        title = "Fun Farm",
        description = "Come discover the new farm and enjoy with your family!",
        date = Timestamp.now(),
        location = Location(46.5, 6.6, "Lausanne"),
        creator = "Rola",
        price = 1.0,
        images = listOf(),
        placesLeft = 10,
        maxPlaces = 20,
        status = ActivityStatus.ACTIVE,
        type = ActivityType.PRO,
        participants = listOf(),
        duration = "2 hours",
        startTime = "10:00",
    )
val activity2 =
    Activity(
        uid = "2",
        title = "Cooking",
        description = "Great cooking class",
        date = Timestamp.now(),
        location = Location(46.519962, 6.633597, "EPFL"),
        creator = "123",
        price = 1.0,
        images = listOf(),
        placesLeft = 10,
        maxPlaces = 20,
        status = ActivityStatus.ACTIVE,
        type = ActivityType.PRO,
        participants = listOf(),
        duration = "2 hours",
        startTime = "10:00",
    )

val activityList = listOf(activity1, activity2)

val testUser =
    User(
        id = "Rola",
        name = "Amine",
        surname = "A",
        photo = "",
        interests = listOf("Cycling", "Reading"),
        activities = listOf(),
        likedActivities = listOf(activityBiking.uid))

val userWithActivities =
    User(
        id = "Rola",
        name = "Amine",
        surname = "A",
        photo = "",
        interests = listOf("Cycling", "Reading"),
        activities = listOf(),
        likedActivities = listOf(activityBiking.uid))

const val email = "test@example.com"
const val idToken = "testGoogleIdToken"
val uid = "testUid"
