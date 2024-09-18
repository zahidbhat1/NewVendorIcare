package com.raybit.newvendor.di

import android.content.Context
import com.raybit.newvendor.data.dataStore.DataStoreConstants
import com.raybit.newvendor.utils.ImageUtility
import com.raybit.newvendor.utils.PermissionFile

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn

import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProductionModule {


    @Singleton
    @Provides
    fun provideString(): String {
        return DataStoreConstants.DataStoreName
    }

    @Singleton
    @Provides
    fun providePermissionFile(
        @ApplicationContext appContext: Context
    ): PermissionFile {
        return PermissionFile(appContext)
    }


    @Singleton
    @Provides
    fun provideImageUtility(
        @ApplicationContext appContext: Context
    ): ImageUtility {
        return ImageUtility(appContext)
    }

}
