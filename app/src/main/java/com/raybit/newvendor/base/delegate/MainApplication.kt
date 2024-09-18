package com.raybit.newvendor.base.delegate

import android.app.Application
import android.app.LocaleManager
import android.content.Context
import android.graphics.Typeface
import com.raybit.newvendor.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyApplication : Application(){


    companion object {
        @JvmField
        var regular: Typeface? = null

        @JvmField
        var semi_bold: Typeface? = null

        @JvmField
        var name = ""

        @JvmField
        var context: Context? = null

        var localeManager: LocaleManager? = null

        // var localeManager: LocaleManager? = null
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        initTimber()
    }

    private fun initTimber() {
        if(BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }
}