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
- Organizers have additional tools for creating and managing events. 
- We implement **role-based permissions**, distinguishing between:
  - Regular users
  - Organizers


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
