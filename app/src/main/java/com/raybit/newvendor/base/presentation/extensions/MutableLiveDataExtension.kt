package  com.raybit.newvendor.base.presentation.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

@Suppress("detekt.UnsafeCast")
fun <T> MutableLiveData<T>.toLiveData() = this as LiveData<T>
