package com.raybit.newvendor.di

import android.content.Context
import com.google.gson.Gson
import com.raybit.newvendor.data.dataStore.AppDataStoreImpl
import com.raybit.newvendor.data.dataStore.DataStoreHelper

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn

import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Singleton
    @Provides
    fun providedDataStore(
        someString: String,
        @ApplicationContext appContext: Context,
        gson: Gson
    ): DataStoreHelper {
        return AppDataStoreImpl(appContext,someString,gson)
    }


}