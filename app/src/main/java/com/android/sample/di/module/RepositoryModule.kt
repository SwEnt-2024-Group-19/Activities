package com.android.sample.di.module

import com.android.sample.model.activity.ActivitiesRepository
import com.android.sample.model.activity.ActivitiesRepositoryFirestore
import com.android.sample.model.auth.SignInRepository
import com.android.sample.model.auth.SignInRepositoryFirebase
import com.android.sample.model.map.LocationRepository
import com.android.sample.model.map.NominatimLocationRepository
import com.android.sample.model.profile.ProfilesRepository
import com.android.sample.model.profile.ProfilesRepositoryFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

  @Provides
  @Singleton
  fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder().build()
  }

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

  @Provides
  @Singleton
  fun provideLocationRepository(client: OkHttpClient): LocationRepository {
    return NominatimLocationRepository(client)
  }

  @Provides
  @Singleton
  fun provideActivitiesRepository(
      firestoreActivitiesRepository: ActivitiesRepositoryFirestore
  ): ActivitiesRepository {
    return firestoreActivitiesRepository
  }
}
