package com.android.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.android.sample.model.ProfilesRepositoryFirestore
import com.android.sample.model.UserProfileViewModel
import com.android.sample.ui.profile.ProfileScreen
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    FirebaseApp.initializeApp(this)

    val db = FirebaseFirestore.getInstance()
    val repository = ProfilesRepositoryFirestore(db)

    // Log.e("Not an error "," just after repository creation")
    val userId = "jp3oRcsfzjcIL7QkiEm7"

    val viewModel: UserProfileViewModel by viewModels {
      UserProfileViewModel.provideFactory(repository, userId)
    }

    setContent { ProfileScreen(viewModel) }
  }
}
