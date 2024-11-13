package com.android.sample.di.module

import com.android.sample.model.activity.ActivitiesRepository
import com.android.sample.model.activity.MockActivitiesRepository
import com.android.sample.model.auth.SignInRepository
import com.android.sample.model.map.LocationRepository
import com.android.sample.model.map.MockLocationPermissionChecker
import com.android.sample.model.map.PermissionChecker
import com.android.sample.model.profile.MockProfilesRepository
import com.android.sample.model.profile.ProfilesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton
import okhttp3.OkHttpClient
import org.mockito.Mockito.mock

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [RepositoryModule::class])
object TestRepositoryModule {

  @Provides
  @Singleton
  fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder().build()
  }

  @Provides
  @Singleton
  fun provideSignInRepository(): SignInRepository {
    return mock(SignInRepository::class.java)
  }

  @Provides
  @Singleton
  fun provideProfilesRepository(): ProfilesRepository {
    return MockProfilesRepository()
  }

  @Provides
  @Singleton
  fun providePermissionChecker(): PermissionChecker {
    return MockLocationPermissionChecker(true)
  }

  @Provides
  @Singleton
  fun provideLocationRepository(): LocationRepository {
    return mock(LocationRepository::class.java)
  }

  @Provides
  @Singleton
  fun provideActivitiesRepository(): ActivitiesRepository {
    return MockActivitiesRepository()
  }
}