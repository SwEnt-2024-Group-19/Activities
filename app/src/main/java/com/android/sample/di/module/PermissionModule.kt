package com.android.sample.di.module

import com.android.sample.model.map.PermissionChecker
import com.android.sample.model.map.LocationPermissionChecker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PermissionModule {

    @Binds
    abstract fun bindPermissionChecker(
        locationPermissionChecker: LocationPermissionChecker
    ): PermissionChecker
}