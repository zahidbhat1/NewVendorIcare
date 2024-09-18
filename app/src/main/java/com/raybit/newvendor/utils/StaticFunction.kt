package com.raybit.newvendor.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import com.raybit.newvendor.R
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer

import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object StaticFunction {

    fun dpToPx(dp: Int): Float {
        return (dp * Resources.getSystem().displayMetrics.density)
    }

    fun changeLocationStroke(color: String, type: String): GradientDrawable {

        val strokeWidth: Int
        if (type == "rate")
            strokeWidth = 30
        else
            strokeWidth = 20

        val gradient = GradientDrawable()
        gradient.shape = GradientDrawable.RECTANGLE
        gradient.cornerRadius = strokeWidth.toFloat()
        gradient.setColor(Color.parseColor(color))

        return gradient
    }

    fun getAddressObject(activity: Activity, latLng: LatLng): Address? {
        var address: Address? = null
        try {
            val geocoder = Geocoder(activity, Locale.getDefault())
            val addresses: List<Address>? =
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses!!.isNotEmpty()) {
                address = addresses[0]
            }
        } catch (e: Exception) {
            when (e.message) {
                "grpc failed" -> {
                    Log.e("Error", e.message!!)
                }
                else -> throw e
            }
            e.printStackTrace()
        }
        return address
    }

    fun priceFormatter(price: String): String {
        val formatter = DecimalFormat("#,##0.00")
        return formatter.format(java.lang.Double.valueOf(price))
    }

    fun pickImages(): Intent? {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        return intent
    }

    fun pickVideos(): Intent? {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "video/*"
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        return intent
    }


    fun hasPermission(context: Context?): Boolean {
        return ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasPermissionLocation(context: Context?): Boolean {
        return ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    fun hasPermissionPhone(context: Context?): Boolean {
        return ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun takeMediaPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            101
        )
    }

    fun changeStrokeColor(color: String): GradientDrawable {
        val gradient = GradientDrawable()
        gradient.shape = GradientDrawable.RECTANGLE
        gradient.setStroke(1, Color.parseColor(color))
        gradient.cornerRadii = floatArrayOf(8f, 8f, 8f, 8f, 8f, 8f, 8f, 8f)
        return gradient
    }


    fun isValidColorHex(color: String?): Boolean {
        if (color == null) return false
        val colorPattern = Pattern.compile("#([0-9a-f]{3}|[0-9a-f]{6}|[0-9a-f]{8})")
        val m = colorPattern.matcher(color.toLowerCase())
        return m.matches()
    }

    fun pxToDp(px: Float, context: Context?): Float {
        return px / ((context?.resources?.displayMetrics?.densityDpi?.toFloat()
            ?: 0f) / DisplayMetrics.DENSITY_DEFAULT)
    }


    fun isAtLeastVersion(version: Int): Boolean {
        return Build.VERSION.SDK_INT >= version
    }




    fun changeGradientColor(): GradientDrawable {
        val ButtonColors = intArrayOf(Color.parseColor("#1A000000"), Color.parseColor("#1A000000"))

        return GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, ButtonColors
        )
    }


    fun changeBorderColor(color: String, strokeColor: String, shape: Int): GradientDrawable {
        val gradient = GradientDrawable()
        gradient.shape = shape
        if (strokeColor.isEmpty()) {
            gradient.setColor(Color.parseColor(color))
            gradient.setStroke(3, Color.parseColor(color))
        } else
            gradient.setStroke(2, Color.parseColor(strokeColor))


        if (shape == GradientDrawable.RADIAL_GRADIENT) {
            gradient.cornerRadius = 20f
            // gradient.setCornerRadii(new float[]{3, 3, 3, 3, 0, 0, 0, 0});
        } else {
            gradient.cornerRadius = 10f
        }
        return gradient
    }

    fun getAge(year: Int, month: Int, day: Int): String {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        dob[year, month] = day
        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
            age--
        }
        val ageInt = age
        return ageInt.toString()
    }

    object BookingStatus {
        const val PENDING = 1
        const val UPCOMING = 2
        const val PAST = 3
    }

    object WASH_STATUS {
        const val PENDING = 1
        const val START = 2
        const val ON_THE_WAY = 3
        const val ARIVED = 4
        const val STARTED_CARWASH = 5
        const val COMPLETED = 6
    }

    object CAR_REPAIR_STATUS {
        const val ALL = 0
        const val PENDING = 1
        const val UPCOMING = 2
        const val PAST = 3
        const val AGENTPAST = 4
        const val REJECTED = 5
        const val START = 6
        const val ON_THE_WAY_FOR_PICKUP = 7
        const val ARIVED_FOR_PICKUP = 8
        const val CAR_PICKEDUP = 9
        const val REACHED_TO_SHOP = 10
        const val STARTED_REPAIR = 11
        const val COMPLETED_REPAIR = 12
        const val ON_THE_WAY_FOR_HANDOVER = 13
        const val ARIVED_FOR_HANDOVER = 14
    }

    object FILE_TYPE {
        const val IMAGE = "1"
        const val VIDEO = "2"
    }

    object TIME_TYPE {
        const val DAY = "1"
        const val HOUR = "2"
    }
    fun getCurrencey(amount: String?): String {
        if (amount.isNullOrEmpty())
            return "₹ NA"
        else
            return "₹ ${String.format("%.2f", amount.toFloat())}"
    }
    fun convertSecondsToMinute(seconds: Long): String {
        val s = seconds % 60
        val m = seconds / 60 % 60
        return String.format("%02d:%02d", m, s)
    }
}

fun formatBookingDate(inputDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss aa", Locale.getDefault())

    val date = inputFormat.parse(inputDate)
    return outputFormat.format(date)
}

fun viewImageFull(activity: Activity, itemsImage: ArrayList<String>, pos: Int) {
    if (itemsImage.isEmpty()) {
        Log.e("viewFullImage", "Image list is empty")
        return
    }

    StfalconImageViewer.Builder(activity, itemsImage) { imageView, imageUrl ->
        Picasso.get().load(imageUrl).into(imageView)
    }.withStartPosition(pos)
        .withBackgroundColor(activity.getColor(R.color.black))

        .withHiddenStatusBar(false)
        .allowZooming(true)
        .allowSwipeToDismiss(true)
        .withOverlayView(null) // If you have any custom view to overlay on images
        .withImageChangeListener { position ->
            // Handle image change event
        }
        .withDismissListener {
            // Handle dismiss event
        }
        .show()
}
fun isYesterday(calendar: Calendar): Boolean {
    val tempCal = Calendar.getInstance()
    tempCal.add(Calendar.DAY_OF_MONTH, -1)
    return calendar.get(Calendar.DAY_OF_MONTH) == tempCal.get(Calendar.DAY_OF_MONTH)
}

fun dateFormatFromMillis(format: String, timeInMillis: Long?): String {
    val fmt = SimpleDateFormat(format, Locale.ENGLISH)
    return if (timeInMillis == null || timeInMillis == 0L)
        ""
    else
        fmt.format(timeInMillis)
}
object CallAction {
    const val PENDING = "pending"
    const val ACCEPT = "accept"
    const val REJECT = "reject"
    const val INPROGRESS = "in-progress"
    const val COMPLETED = "completed"
    const val CANCELED="canceled"
    const val FAILED = "failed"

}
object ChatType {
    const val MESSAGE_TYPE_TEXT = "text"
    const val MESSAGE_TYPE_IMAGE = "image"
}
object CallType {
    const val CALL = "voice"
    const val CHAT = "chat"
    const val VIDEO = "video"
}
const val POSITION = "POSITION"
object LoadingStatus {
    const val ITEM = 0
    const val LOADING = 1
}
suspend fun compressImage(activity: Activity?, actualImageFile: File?): File? {
    if (activity == null || actualImageFile == null) {
        Log.e("compressImage", "Activity or Image file is null")
        return null
    }

    return try {
        Compressor.compress(activity, actualImageFile) {
            quality(90)
            format(Bitmap.CompressFormat.JPEG)
            size(300_000) // Max size in bytes
        }
    } catch (e: Exception) {
        Log.e("compressImage", "Compression failed", e)
        actualImageFile // Return the original file if compression fails
    }
}
fun getPathUri(context: Context, uri: Uri): String? {
    val projection =
        arrayOf<String>(MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(uri, projection, null, null, null)
        ?: return ""
    val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    cursor.moveToFirst()
    val result = cursor.getString(column_index)
    cursor.close()
    return result
}
object AppRequestCode {
    const val AUTOCOMPLETE_REQUEST_CODE: Int = 1001
    const val IMAGE_PICKER: Int = 100
    const val PROFILE_UPDATE: Int = 102
    const val PAYOUT = 103
    const val REQ_CHAT = 214
    const val ADD_CLASS = 215
}