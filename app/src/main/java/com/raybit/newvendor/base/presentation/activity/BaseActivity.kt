package com.raybit.newvendor.base.presentation.activity

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

abstract class BaseActivity : AppCompatActivity() {

    @get:LayoutRes
    protected abstract val layoutResId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(layoutResId)

        supportActionBar?.hide()

        // The window will not be resized when virtual keyboard is shown (bottom navigation bar will be
        // hidden under virtual keyboard)

        Timber.v("onCreate ${javaClass.simpleName}")
    }

    open fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.RESULT_UNCHANGED_SHOWN
            )
        }
    }




}