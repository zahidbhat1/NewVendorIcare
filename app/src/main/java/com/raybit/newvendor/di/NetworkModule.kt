package com.raybit.newvendor.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.raybit.newvendor.BuildConfig
import com.raybit.newvendor.data.AppDataManager
import com.raybit.newvendor.data.DataManager
import com.raybit.newvendor.data.dataStore.DataStoreHelper
import com.raybit.newvendor.data.network.ApiService
import com.raybit.newvendor.data.network.AuthenticationInterceptor
import com.raybit.newvendor.pushNotifications.MessagingService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder()
            .create()
    }


    @Singleton
    @Provides
    fun providersLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Singleton
    @Provides
    fun providedAuthenticationInterceptor(dataStoreHelper: DataStoreHelper): AuthenticationInterceptor {
        return AuthenticationInterceptor(
            dataStoreHelper
        )
    }
    @Provides
    @Singleton
    fun provideMessagingService(messagingService: MessagingService): MessagingService = MessagingService()

    @Singleton
    @Provides
    fun providesOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authenticationInterceptor: AuthenticationInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authenticationInterceptor)
            .readTimeout(240, TimeUnit.SECONDS)
            .connectTimeout(240, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()

    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit
            .create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideAppManager(
        mPreferencesHelper: DataStoreHelper,
        mApiHelper: ApiService
    ): DataManager {
        return AppDataManager(mPreferencesHelper, mApiHelper)
    }




}