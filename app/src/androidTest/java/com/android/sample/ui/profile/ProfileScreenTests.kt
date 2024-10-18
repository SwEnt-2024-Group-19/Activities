package com.android.sample.ui.profile

/*
class ProfileScreenTest {

    private lateinit var userProfileViewModel: ProfileViewModel
    private lateinit var testUser: User
    private lateinit var navigationActions: NavigationActions

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        userProfileViewModel = mock(ProfileViewModel::class.java)
        testUser = User(
            id = "123",
            name = "Amine",
            surname = "A",
            photo = "",
            interests = listOf("Cycling", "Reading"),
            activities = listOf("Football")
        )
        val userStateFlow = MutableStateFlow(testUser)
        navigationActions = mock(NavigationActions::class.java)
        `when`(navigationActions.currentRoute()).thenReturn(Screen.PROFILE)
        `when`(userProfileViewModel.userState).thenReturn(userStateFlow)
    }

    @Test
    fun displayLoadingScreen() {
        val userStateFlow = MutableStateFlow<User?>(null) // Represents loading state
        `when`(userProfileViewModel.userState).thenReturn(userStateFlow)

        composeTestRule.setContent {
            ProfileScreen(userProfileViewModel = userProfileViewModel, navigationActions)
        }
        composeTestRule.onNodeWithTag("loadingText").assertTextEquals("Loading profile...")
        composeTestRule.onNodeWithTag("loadingScreen").assertIsDisplayed()

    }
}

   @Test
   fun displayAllProfileComponents() {
       composeTestRule.setContent {
           ProfileScreen(
               userProfileViewModel = userProfileViewModel, navigationActions = navigationActions
           )
       }



       composeTestRule.onNodeWithTag("profileScreen").assertIsDisplayed()
       composeTestRule.onNodeWithTag("profilePicture").assertIsDisplayed()
       composeTestRule.onNodeWithTag("userName").assertIsDisplayed()
       composeTestRule.onNodeWithTag("userName").assertTextEquals("Amine A")
       composeTestRule.onNodeWithTag("interestsSection").assertIsDisplayed()
       composeTestRule.onNodeWithTag("interestsSection")
           .assertTextEquals("Interests: Cycling, Reading")
       composeTestRule.onNodeWithTag("activitiesSection").assertIsDisplayed()
       composeTestRule.onNodeWithTag("activitiesList").assertIsDisplayed()
       composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
       composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
       composeTestRule.onNodeWithTag("goBackButton")
           .performClick() // test for if on click it goes back
   }
}


 */
