package com.android.sample.di.module

import com.android.sample.model.auth.SignInRepository
import com.android.sample.model.auth.SignInRepositoryFirebase
import com.android.sample.model.profile.ProfilesRepository
import com.android.sample.model.profile.ProfilesRepositoryFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

  @Provides
  @Singleton
  fun provideSignInRepository(
      signInRepositoryFirebase: SignInRepositoryFirebase
  ): SignInRepository {
    return signInRepositoryFirebase
  }

  @Provides
  @Singleton
  fun provideProfilesRepository(
      firestoreProfilesRepository: ProfilesRepositoryFirestore
  ): ProfilesRepository {
    return firestoreProfilesRepository
  }
}
