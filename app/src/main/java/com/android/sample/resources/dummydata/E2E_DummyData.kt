package com.android.sample.resources.dummydata

import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.ActivityType
import com.android.sample.model.activity.Comment
import com.android.sample.model.map.Location
import com.android.sample.model.profile.Interest
import com.android.sample.model.profile.User
import com.google.firebase.Timestamp

/* Please do not modify the following dummy data for general testing, as it is designed for e2e tests */

private val e2e_interests =
    listOf(Interest("Indoor Activity", "reading"), Interest("Outdoor Activity", "hiking"))

private val e2e_locations =
    mapOf(
        "EPFL" to Location(46.519962, 6.633597, "EPFL"),
        "Lausanne" to Location(46.5, 6.6, "Lausanne"))

private val e2e_user1 =
    User(
        id = "user123",
        name = "John",
        surname = "Doe",
        photo = "profile.jpg",
        interests = listOf(e2e_interests[0], e2e_interests[1]),
        activities = listOf("e2e_activity1", "e2e_activity2"),
        likedActivities = listOf("liked1", "liked2"))

private val e2e_user1_idToken = e2e_user1.id.hashCode().toString()
private val e2e_user1_email =
    "${e2e_user1.name.lowercase()}.${e2e_user1.surname.lowercase()}@example.com"
private val e2e_user1_password = e2e_user1.hashCode().toString()

private val e2e_user2 =
    User(
        id = "user456",
        name = "Jane",
        surname = "Smith",
        photo = "profile.jpg",
        interests = listOf(),
        activities = listOf(),
        likedActivities = listOf())

private val e2e_user2_idToken = e2e_user2.id.hashCode().toString()
private val e2e_user2_email =
    "${e2e_user2.name.lowercase()}.${e2e_user2.surname.lowercase()}@example.com"
private val e2e_user2_password = e2e_user2.hashCode().toString()

private val e2e_reply1 =
    Comment(
        uid = "reply1",
        userId = e2e_user2.id,
        userName = "${e2e_user2.name} ${e2e_user2.surname}",
        content = "I agree!",
        timestamp = Timestamp.now())

private val e2e_comment1 =
    Comment(
        uid = "comment1",
        userId = e2e_user1.id,
        userName = "${e2e_user1.name} ${e2e_user1.surname}",
        content = "Nice activity!",
        timestamp = Timestamp.now(),
        replies = listOf(e2e_reply1))

private val e2e_activity1 =
    Activity(
        uid = "e2e_activity1",
        title = "Sample Title 1",
        description = "Sample Description 1",
        date = Timestamp.now(),
        startTime = "10:00",
        duration = "2 hours",
        price = 0.0,
        location = e2e_locations["EPFL"],
        creator = e2e_user1.id,
        images = listOf("image1.jpg", "image2.jpg"),
        placesLeft = 10,
        maxPlaces = 20,
        status = ActivityStatus.ACTIVE,
        type = ActivityType.INDIVIDUAL,
        participants = listOf(e2e_user1),
        comments = listOf())

private val e2e_activity2 =
    Activity(
        uid = "activity2",
        title = "Sample Title 1",
        description = "Sample Description 1",
        date = Timestamp.now(),
        startTime = "10:00",
        duration = "2 hours",
        price = 15.0,
        location = e2e_locations["Lausanne"],
        creator = e2e_user2.id,
        images = listOf("image1.jpg", "image2.jpg"),
        placesLeft = 5,
        maxPlaces = 20,
        status = ActivityStatus.ACTIVE,
        type = ActivityType.PRO,
        participants = listOf(e2e_user1),
        comments = listOf(e2e_comment1))

val defaultUserCredentials =
    mapOf(
        "email" to e2e_user1_email,
        "password" to e2e_user1_password,
        "full name" to "${e2e_user1.name} ${e2e_user1.surname}")

val e2e_Users = listOf(e2e_user1, e2e_user2)
val e2e_Activities = listOf(e2e_activity1, e2e_activity2)
val e2e_Credentials =
    mapOf(e2e_user1_email to e2e_user1_password, e2e_user2_email to e2e_user2_password)
val e2e_EmailToUserId = mapOf(e2e_user1_email to e2e_user1.id, e2e_user2_email to e2e_user2.id)
val e2e_IdTokenToUserId =
    mapOf(e2e_user1_idToken to e2e_user1.id, e2e_user2_idToken to e2e_user2.id)
