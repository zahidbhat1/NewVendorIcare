package com.raybit.newvendor.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import androidx.annotation.RequiresApi

import com.raybit.newvendor.utils.StaticFunction.isAtLeastVersion
import com.raybit.newvendor.data.AppConstants
import com.raybit.newvendor.data.dataStore.DataStoreConstants
import com.raybit.newvendor.data.dataStore.DataStoreHelper
import kotlinx.coroutines.flow.collectLatest
import java.util.*

class LocaleManager(private val context: Context?) {


    lateinit var dataStoreHelper: DataStoreHelper

    suspend fun setLocale(c: Context): Context {
        return updateResources(c, getLanguage())
    }

    fun setNewLocale(c: Context, language: String): Context {
        AppConstants.LANG_CODE = language
        persistLanguage(language)
        return updateResources(c, language)
    }

    suspend fun getLanguage(): String {
        var language: String? = ""
        dataStoreHelper.getKeyValue(DataStoreConstants.SELECTED_LANGUAGE).collectLatest {
            language = it?.toString()
        }
        return language ?: ""
    }


    @SuppressLint("ApplySharedPref")
    private fun persistLanguage(language: String) {
        // use commit() instead of apply(), because sometimes we kill the application process
        // immediately that prevents apply() from finishing
      /*  GlobalScope.launch {
            dataStoreHelper.setKeyValue(DataStoreConstants.SELECTED_LANGUAGE, language)
        }*/
    }

    private fun updateResources(context: Context, language: String?): Context {
        var context = context
        val locale = Locale(language)
        Locale.setDefault(locale)
        val res = context.resources
        val config = Configuration(res.configuration)
        if (isAtLeastVersion(Build.VERSION_CODES.N)) {
            setLocaleForApi24(config, locale)
            context = context.createConfigurationContext(config)
        } else if (isAtLeastVersion(Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            config.setLocale(locale)
            context = context.createConfigurationContext(config)
        } else {
            config.locale = locale
            res.updateConfiguration(config, res.displayMetrics)
        }
        return context
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun setLocaleForApi24(config: Configuration, target: Locale) {
        val set: MutableSet<Locale> = LinkedHashSet()
        // bring the target locale to the front of the list
        set.add(target)
        val all = LocaleList.getDefault()
        for (i in 0 until all.size()) {
            // append other locales supported by the user
            set.add(all[i])
        }
        val locales = set.toTypedArray()
        config.setLocales(LocaleList(*locales))
    }

    companion object {
        const val LANGUAGE_ENGLISH = "english"

        fun getLocale(res: Resources): Locale {
            val config = res.configuration
            return if (isAtLeastVersion(Build.VERSION_CODES.N)) config.locales[0] else config.locale
        }
    }

}