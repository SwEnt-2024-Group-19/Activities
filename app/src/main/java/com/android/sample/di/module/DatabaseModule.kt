package com.android.sample.di.module

import android.content.Context
import androidx.room.Room
import com.android.sample.model.activity.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

  @Provides
  @Singleton
  fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
    return Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
        .fallbackToDestructiveMigration() // For testing, handle migrations in production
        .build()
  }

  @Provides @Singleton fun provideUserDao(appDatabase: AppDatabase) = appDatabase.userDao()

  @Provides @Singleton fun provideActivityDao(appDatabase: AppDatabase) = appDatabase.activityDao()
}
