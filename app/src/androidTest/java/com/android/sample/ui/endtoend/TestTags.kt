package com.android.sample.ui.endtoend

import com.android.sample.model.activity.Category

/*
 * File containing most test tags used in the app for e2e testing.
 * Centralizing test tags ensures consistency and maintainability.
 * Ideally, this object should be shared across the app modules.
 * But for the sake of avoiding refactoring the existing tests and code, it is placed in the `test` module.
 */

/** Bottom navigation tags */
object BottomNavigation {
  const val MENU = "bottomNavigationMenu"
  const val OVERVIEW = "Overview"
  const val Map = "Map"
  const val CREATE_ACTIVITY = "Add Activity"
  const val Liked = "Liked"
  const val PROFILE = "Profile"
}

/** Auth tags */
object Auth {
  object SignUp {
    const val SCREEN = "SignUpColumn"
    const val SIGN_UP_COLUMN = "SignUpColumn"
    const val GO_TO_SIGN_IN_BUTTON = "GoToSignInButton"
    const val EMAIL_TEXT_FIELDS = "EmailTextField"
    const val PASSWORD_TEXT_FIELDS = "PasswordTextField"
    const val NAME_TEXT_FIELDS = "nameTextField"
    const val SURNAME_TEXT_FIELDS = "surnameTextField"
    const val SIGN_UP_BUTTON = "SignUpButton"
  }

  object SignIn {
    const val SCREEN = "SignInScreen"
    const val SIGN_IN_COLUMN = "SignInScreenColumn"
    const val GO_TO_SIGN_UP_BUTTON = "GoToSignUpButton"
    const val SIGN_IN_BUTTON = "SignInButton"
    const val GUEST_BUTTON = "ContinueAsGuestButton"
    const val EMAIL_INPUT = "EmailTextField"
    const val PASSWORD_INPUT = "PasswordTextField"

    const val TEXT_INVALID_EMAIL = "Please enter a valid address: example@mail.xx"
  }
}

/** Overview tags */
object Overview {
  const val SCREEN = "listActivitiesScreen"
  const val ACTIVITY_CARD = "activityCard"
  const val SEGMENTED_BUTTON_ROW = "segmentedButtonRow"
  const val FILTER_DIALOG_BUTTON = "filterDialog"
  const val FILTER_DIALOG = "FilterDialog"

  object Filters {
    const val ONLY_PRO_CHECKBOX_ROW = "onlyPROCheckboxRow"
    const val ONLY_PRO_CHECKBOX = "onlyPROCheckbox"
    const val FILTER_BUTTON = "filterButton"
  }

  object SearchBar {
    const val SEARCH_BAR = "searchBar"
  }

  fun SEGMENTED_BUTTON_(type: Category) = "segmentedButton${type.name}"

  const val EMPTY_ACTIVITY = "emptyActivityPrompt"

  object ActivityDetails {
    const val SCREEN = "activityDetailsScreen"
    const val TopAppBar = "topAppBar"
    const val GO_BACK_BUTTON = "goBackButton"
    const val Image = "image"
    const val Title = "title"
    const val TitleText = "titleText"
    const val DescriptionText = "descriptionText"
    const val Price = "price"
    const val PriceText = "priceText"
    const val Location = "location"
    const val LocationText = "locationText"
    const val Schedule = "schedule"
    const val ScheduleText = "scheduleText"
    const val NOT_LOGGED_IN_TEXT = "notLoggedInText"
    const val ENROLL_BUTTON = "enrollButton"
  }
}

/** Profile tags */
object Profile {
  const val SCREEN = "profileScreen"
  const val MORE_OPTIONS_BUTTON = "moreOptionsButton"
  const val LOGOUT_BUTTON = "logoutMenuItem"
  const val EDIT_PROFILE_BUTTON = "editProfileMenuItem"
  const val ENROLLED_BUTTON = "enrolledActivities"
  const val ACTIVITY_ROW = "activityRow"
  const val PLUS_BUTTON_TO_CREATE = "plusRowToCreate"

  object NotLoggedIn {
    const val PROMPT = "loadingText"
    const val SIGN_IN_BUTTON = "signInButton"
  }

  object EditProfile {
    const val EDIT_PROFILE_SCREEN = "editProfileScreen"
    const val INPUT_NAME = "inputProfileName"
    const val SAVE_BUTTON = "profileSaveButton"
  }
}

object NoConnection {
  const val NOT_CONNECTED = "notConnectedPrompt"
}

/** Activity details tags */
object ActivityDetails {
  const val SCREEN = "activityDetailsScreen"
  const val TopAppBar = "topAppBar"
  const val Image = "image"
  const val Title = "title"
  const val TitleText = "titleText"
  const val detailsIcon = "ACTIVITY_DETAILS button"
  const val attendantsIcon = "ATTENDANT_DETAILS button"
  const val commentsIcon = "ACTIVITY_COMMENTS button"
  const val DescriptionText = "descriptionText"
  const val Price = "price"
  const val PriceText = "priceText"
  const val Location = "location"
  const val LocationText = "locationText"
  const val Schedule = "schedule"
  const val ScheduleText = "scheduleText"
  const val GoBackButton = "goBackButton"
}

// Inputs
object Inputs {
  const val InputTitleCreate = "inputTitleCreate"
  const val InputDescriptionCreate = "inputDescriptionCreate"
  const val InputDateCreate = "inputDateCreate"
  const val InputPriceCreate = "inputPriceCreate"
  const val InputPlacesCreate = "inputPlacesCreate"
  const val InputLocationCreate = "inputLocationCreate"
  const val ChooseTypeMenu = "chooseTypeMenu"
}

// Filter & Lists
object Filters {
  const val SegmentedButtonRow = "segmentedButtonRow"
  const val SegmentedButtonCulture = "segmentedButtonCULTURE"
  const val SegmentedButtonSport = "segmentedButtonSPORT"
}

// Prompts
object Prompts {
  const val NOT_CONNECTED = "notConnectedPrompt"
  const val EmptyActivityPrompt = "emptyActivityPrompt"
  const val SignInButton = "signInButton"
  const val GoToSignInButton = "GoToSignInButton"
}

// Map
object Map {
  const val Map = "Map"
  const val CenterOnCurrentLocation = "centerOnCurrentLocation"
}

// Liked Activities
object LikedActivities {
  const val SCREEN = "likedActivitiesScreen"
  const val ACTIVITY_CARD = "activityCard"
}
