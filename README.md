# Aptivity


### Pitch

Finding local activities like art classes, yoga sessions, local events, or self-organizing activities can be overwhelming. Many people miss out on experiences due to a lack of information or difficulty in discovering them. Aptivity solves this by offering an easy-to-navigate app where users can explore and register for activities happening near them. Our app simplifies the process for both participants and organizers by connecting them effortlessly. 

We target the following user groups:
- Parents and their children
- Students
- Young adults and couples
- Elderly

You got it, we have something for everyone!

---

### How it works

- Explore activities happening near you on the map or search specific activities through the search bar.
- Register for activities you are interested in.
- Save your favorite activities for later to keep track of them.
- Create your own activities and manage them as an organizer.
- Ask any questions on the event page.
- Rate and review activities you have attended.
- Get notified about upcoming activities and changes in the events you are participating in.


### Split-App Model

We use **Firebase** as our primary cloud service for user authentication, real-time database, and notifications:

- **Firebase Authentication** manages user accounts and sign-ins.
- **Firebase Firestore** stores activity data and user profiles data.
- **Firebase Storage** is used for media storage (event images).
- **Local caching** (DAO) is used to support offline mode.

---

### Multi-User Support

Our app supports multiple users through **Firebase Authentication**, allowing users to register using their email or Google accounts. 

#### Key Features:
- Each user has their own profile, storing preferences and past activity history.
- Any user can be an organizer or a participant
- For each activity, only its creator has certain rights such as modifying it, deleting it, removing pictures...
- Each participant receives notifications to alert him : 
- - 24 hours ahead of the activity scheduled time,
- - if the activity is deleted.
- Each user has a participant rating, and if he creates at least an activity, he also has a creator rating.
- Creator rating depends essentially on the likes and dislikes the creator's activities receive but also on the completion of his activities( number of people participating over the total number of places), the number of people that have joined its activities in global, and how susceptible the participants are to give a feedback (feedback ratio)
- At the end of the activity, each participant can rate the activity, and the activity's creator can rate the participants.
- Each user has a personalised overview of activities depending on the interests he puts in his profile, his location, the date proximity with the activity's scheduled date, whether the participant has already enrolled in a specific person's activity in the past, the rating of the creators, and the price.
- Each user can filter the activities he wants to browse depending on various parameters, such as the date of the activity, its location, price...
- Each user can see the activity location on the map, and go from its preview screen to its location on the map and vice-versa.
-


---

### Sensor Use

Our app makes use of the following sensors:

- **GPS Location Services**: To help users discover activities near their current location.
- **Camera**: 
  - Organizers can showcase their activities.
  - Users can upload their profile pictures.

---

### Offline Mode

Our app features an **Offline Mode** for better usability without network access. While offline, users are:

- Able to **browse activities** they have previously viewed.
- Able to access **events they have signed up for**, including key details like time and location.

An offline toast message informs users when they are in offline mode.

**Note**: Event registration and browsing new activities requires the user to be online.

---
