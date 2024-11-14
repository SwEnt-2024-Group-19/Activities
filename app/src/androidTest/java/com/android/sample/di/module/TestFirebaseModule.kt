package com.android.sample.di.module

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton
import org.mockito.Mockito
import org.mockito.Mockito.`when`

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [FirebaseModule::class])
object TestFirebaseModule {

  @Provides
  @Singleton
  fun provideMockFirebaseAuth(): FirebaseAuth {
    val mockAuth = Mockito.mock(FirebaseAuth::class.java)

    `when`(mockAuth.currentUser).thenReturn(null)

    return mockAuth
  }

  @Provides
  @Singleton
  fun provideMockFirebaseFirestore(): FirebaseFirestore {
    return Mockito.mock(FirebaseFirestore::class.java)
  }
}
