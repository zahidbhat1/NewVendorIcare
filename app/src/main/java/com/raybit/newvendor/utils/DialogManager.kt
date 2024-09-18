package com.raybit.newvendor.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.raybit.newvendor.R


interface DialogClickListener{
    fun onButtonClicked()
}

fun Activity.showLogoutAlert(context: Context, listener: DialogClickListener) {
    val dialog = AlertDialog.Builder(context)
        .setTitle("Logout")
        .setMessage("Are you sure you want to Logout!!")
        .setCancelable(true)
        .setPositiveButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        .setNeutralButton("Logout") { dialog, _ ->
            listener.onButtonClicked()
            dialog.dismiss()
        }
        .create()

    dialog.show()
}

fun Activity.showAlertDialog(message: String) {
    val dialog = AlertDialog.Builder(this)
        .setMessage(message)
        .setCancelable(true)
        .setNeutralButton("Ok") { dialog, _ ->
            dialog.dismiss()
        }
        .create()

    dialog.show()
}


fun Activity.showConfirmationAlertDialog(
    message: String,
    listener: DialogInterface.OnClickListener
) {
    val dialog = AlertDialog.Builder(this)
        .setMessage(message)
        .setCancelable(true)
        .setNeutralButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        .setPositiveButton("Ok", listener)
        .create()
    dialog.show()
}

fun Activity.showSelectPhotoDialog(camera: View.OnClickListener, gallery: View.OnClickListener):Dialog {
    val photoDialog = Dialog(this, R.style.Theme_MyDialog)
    photoDialog.setContentView(R.layout.dialog_select_photo)
    photoDialog.window?.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT
    )
    photoDialog.show()
    photoDialog.setTitle("")

    val btnCamera = photoDialog.findViewById<TextView>(R.id.btnCamera)
    val btnGallery = photoDialog.findViewById<TextView>(R.id.btnGallery)

    btnCamera?.setOnClickListener {
        photoDialog.dismiss()
        camera.onClick(it)
    }
    btnGallery?.setOnClickListener {
        photoDialog.dismiss()
        gallery.onClick(it)
    }

    return photoDialog
}

fun Context.showNeverAskAgainDialog() {
    val builder = androidx.appcompat.app.AlertDialog.Builder(this)
    builder.setMessage(R.string.deniedpermission)
        .setCancelable(false)
        .setPositiveButton(R.string.gotosettings) { _, _ ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            (this as Activity).startActivityForResult(intent, 0)
        }.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
    val alert = builder.create()
    alert.show()
}


