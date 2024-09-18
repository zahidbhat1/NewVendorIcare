package com.raybit.newvendor.utils;

import android.app.Activity
import android.app.Dialog
import android.view.View
import com.raybit.newvendor.R

class ProgressDialog() {

    private lateinit var dialog: Dialog

    constructor(context: Activity) : this() {
        val dialogView = View.inflate(context, R.layout.dialog_progress, null)
        dialog = Dialog(context, R.style.CustomDialog)
        dialog.setContentView(dialogView)
        dialog.setCancelable(false)
    }

    private fun show() {
        if (!dialog.isShowing)
            dialog.show()
    }

    private fun dismiss() {
        if (dialog.isShowing)
            dialog.dismiss()
    }

    fun setLoading(isLoading: Boolean) {
        if (isLoading)
            show()
        else
            dismiss()
    }
    fun isLoading():Boolean{
      return  dialog.isShowing
    }
}