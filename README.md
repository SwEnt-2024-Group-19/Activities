# Aptivity

## App Description

### Pitch

Finding local activities like art classes, yoga sessions, local events, or self-organizing activities can be overwhelming. Many people miss out on experiences due to a lack of information or difficulty in discovering them. Aptivity solves this by offering an easy-to-navigate app where users can explore and register for activities happening near them. Our app simplifies the process for both participants and organizers by connecting them effortlessly. 

We target the following user groups:
- Parents and their children
- Students
- Young adults and couples
- Elderly

---

### Split-App Model

We plan to use **Firebase** as our primary cloud service for user authentication, real-time database, and notifications:

- **Firebase Authentication** will manage user accounts and sign-ins.
- **Firebase Firestore** will store activity data, user profiles, and reviews.
- **Firebase Storage** will be used for media storage (event images, videos).
- **Firebase Cloud Functions** will handle reminders and push notifications for upcoming activities, enhancing engagement and user experience.

---

### Multi-User Support

Our app will support multiple users through **Firebase Authentication**, allowing users to register using their email or Google accounts. 

#### Key Features:
- Each user will have their own profile, storing preferences and past activity history.
- Organizers will have additional tools for creating and managing events.
- We will implement **role-based permissions**, distinguishing between:
  - Regular users
  - Organizers

These roles will have different access levels to manage their respective tasks.

---

### Sensor Use

The app will make use of the following sensors:

- **GPS Location Services**: To help users discover activities near their current location.
- **Camera**: 
  - Organizers can showcase their activities.
  - Users can share pictures of their participation in events.

---

### Offline Mode

The app will feature an **Offline Mode** for better usability without network access. While offline, users will:

- Be able to **browse activities** they have previously viewed.
- Access **events they have signed up for**, including key details like time and location.

An offline toast message will inform users when they are in offline mode.

**Note**: Event registration and browsing new activities will require the user to be online.

---
