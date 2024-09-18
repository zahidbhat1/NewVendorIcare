
package com.raybit.newvendor.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.core.content.FileProvider
import androidx.core.content.res.use
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.raybit.newvendor.BuildConfig
import com.raybit.newvendor.R
import com.raybit.newvendor.ui.activity.AuthenticationActivity
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import java.io.File
import java.io.IOException
import java.util.*
import java.util.regex.Pattern

private const val SECOND_MILLIS = 1000
private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
private const val DAY_MILLIS = 24 * HOUR_MILLIS
fun isValidEmail(target: CharSequence?): Boolean {
    return target != null && Patterns.EMAIL_ADDRESS.matcher(target)
        .matches()
}


fun View.onSnackbar(msg: String) {
    //Snackbar(view)
    val snackbar = Snackbar.make(
        this, msg,
        Snackbar.LENGTH_LONG
    )

    snackbar.setAction("ok") { snackbar.dismiss() }
    val snackbarView = snackbar.view
    snackbarView.setBackgroundColor(Color.parseColor("#000000"))
    snackbar.setActionTextColor(Color.parseColor("#000000"))
    snackbar.show()
}

fun String?.toTextRequestBody(): RequestBody? = this?.toRequestBody("text/plain".toMediaType())
fun String.toMediaRequestBody(): RequestBody =
    this.toRequestBody("multipart/form-data".toMediaType())

fun toMultipart(fileName: String, path: String,keyName :String): MultipartBody.Part {
    val file = File(path)

    val requestFile = file.asRequestBody("image/*".toMediaType())

    val requestBody = MultipartBody.Part.createFormData(keyName, file.name, requestFile)

    return requestBody
}

 fun isValidEmailId(email: String): Boolean {
    val EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

    return Pattern.compile(EMAIL_PATTERN).matcher(email).matches()
}

fun FragmentManager.getCurrentNavigationFragment(): Fragment? =
    primaryNavigationFragment?.childFragmentManager?.fragments?.first()

fun pxFromDp(dp: Int, context: Context?): Int {
    return (dp * (context?.resources?.displayMetrics?.density ?: 0f)).toInt()
}



fun Context.isNetworkConnected(): Boolean {
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = cm.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting
}

fun Context.getWidth(): Int {

    val displayMetrics = DisplayMetrics()
    val windowmanager: WindowManager =
        this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    windowmanager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}

fun Context.getHeight(): Int {

    val displayMetrics = DisplayMetrics()
    val windowmanager: WindowManager =
        this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    windowmanager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.heightPixels
}


fun toVisibility(constraint: Boolean): Int = if (constraint) {
    View.VISIBLE
} else {
    View.GONE
}

@ColorInt
@SuppressLint("Recycle")
fun Context.themeColor(
    @AttrRes themeAttrId: Int
): Int {
    return obtainStyledAttributes(
        intArrayOf(themeAttrId)
    ).use {
        it.getColor(0, Color.MAGENTA)
    }
}

fun Activity.toStart() {
    Intent(this, AuthenticationActivity::class.java).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        it.putExtra("openFragment","login_sign_up")
        startActivity(it)
    }
}


fun setBaseUrl(baseUrl: String, retrofit: Retrofit) {
    val field = Retrofit::class.java.getDeclaredField("baseUrl")
    field.isAccessible = true
    val newHttpUrl = baseUrl.toHttpUrlOrNull()
    field.set(retrofit, newHttpUrl)
}

fun getAddress(mContext: Context, latitude: Double?, longitude: Double?): String {

    val addresses: List<Address>
    var address: Address
    var addressData = ""
    val geocoder = Geocoder(mContext, Locale.getDefault())

    try {
        addresses = geocoder.getFromLocation(
            latitude ?: 0.0,
            longitude ?: 0.0,
            1
        )!! // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        for (i in addresses.indices) {
            address = addresses[i]
            if (address.getAddressLine(0) != null) {
                addressData = address.getAddressLine(0)
                break
            }
        }

        return addressData
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return addressData
}

fun Activity.forceLayoutToRTL() {
    this.window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
}

fun Activity.forceLayoutToLTR() {
    this.window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
}

inline fun <reified T : Any> Activity.launchActivity(
    requestCode: Int = -1,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {

    val intent = newIntent<T>(this)
    intent.init()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        startActivityForResult(intent, requestCode, options)
    } else {
        startActivityForResult(intent, requestCode)
    }
}


fun EditText.onChange(cb: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            cb(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}



fun Context.getStringByLocale(
    @StringRes stringRes: Int,
    locale: Locale,
    vararg formatArgs: Any
): String {
    val configuration = Configuration(resources.configuration)
    configuration.setLocale(locale)
    return createConfigurationContext(configuration).resources.getString(stringRes, *formatArgs)
}

fun Long.timeAgo(): String? = getTimeAgo(this)

fun getTimeAgo(timeF: Long): String? {
    var time = timeF
    if (time < 1000000000000L) {
        time *= 1000
    }

    val now = System.currentTimeMillis()
    var diff = 0L
    if (time > now || time <= 0) {
        diff = time - now
    } else {
        diff = now - time
    }

    return when {
        diff < MINUTE_MILLIS -> "just now"
        diff < 2 * MINUTE_MILLIS -> "a minute ago"
        diff < 50 * MINUTE_MILLIS -> "${diff / MINUTE_MILLIS} minutes ago"
        diff < 90 * MINUTE_MILLIS -> "an hour ago"
        diff < 24 * HOUR_MILLIS -> "${diff / HOUR_MILLIS} hours ago"
        diff < 48 * HOUR_MILLIS -> "yesterday"
        else -> "${diff / DAY_MILLIS} days ago"
    }
}

internal fun ImageView.loadImage(url: String, imageHeight: Int? = null, imageWidth: Int? = null) {



    var thumbUrl = ""

    val imgHeight = if (imageHeight != null && imageHeight!= 0) {
        StaticFunction.dpToPx(imageHeight)
    } else {
        StaticFunction.dpToPx(250)
    }.toFloat()

    val imgWidth = if (imageWidth != null &&  imageWidth!= 0) {
        StaticFunction.dpToPx(imageWidth)
    } else {
        StaticFunction.dpToPx(250)
    }.toFloat()

    thumbUrl = if (url.isNotEmpty() && url.contains("cdn-assets.royoapps.com")) {
        "${BuildConfig.IMAGE_URL}${url.substring(url.lastIndexOf("/") + 1)}?w=${imgWidth}&h=${imgHeight}&auto=format"
    } else {
        url
    }

    val glide = Glide.with(this.context)


    val requestOptions = RequestOptions
        .bitmapTransform(
            RoundedCornersTransformation(
                8, 0,
                RoundedCornersTransformation.CornerType.ALL
            )
        )
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(R.drawable.error )
        .error( R.drawable.error )

    Glide.with(this.context)
        .load(thumbUrl)
        .apply(
            RequestOptions()
                .transform(RoundedCorners(8))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.error)
                .error(R.drawable.error)
        )
        .listener(object : RequestListener<Drawable> {

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                // Resource is ready
                return false
            }

            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
        })
        .into(this)
}

internal fun ImageView.loadImage(url: String,placeholderResId: Int) {
    Glide.with(this.context)
        .load(url)
        .apply(
            RequestOptions()
                .transform(RoundedCorners(8))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
               // .placeholder(this.context.getDrawable(R.drawable.placeholder))
                .placeholder(placeholderResId)
                .error(R.drawable.error)
        )
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
        })
        .into(this)

}


inline fun <reified T : Any> Context.launchActivity(
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {

    val intent = newIntent<T>(this)
    intent.init()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        startActivity(intent, options)
    } else {
        startActivity(intent)
    }
}

inline fun <reified T : Any> newIntent(context: Context): Intent =
    Intent(context, T::class.java)


fun Context.createImageFile(): File {
    val dir = File(getExternalFilesDir(null)?.absolutePath, "/iCarePro")
    if (!dir.exists())
        dir.mkdirs()

    val imageName = System.currentTimeMillis()
    return File(dir.absolutePath, "iCarePro_$imageName.jpeg")
}

fun Context.getFileUri(outputFile:File): Uri {
    return FileProvider.getUriForFile(this,  "$packageName.provider", outputFile)
}

