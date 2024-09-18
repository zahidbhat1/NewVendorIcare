package com.raybit.newvendor.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raybit.newvendor.data.AppDataManager
import com.raybit.newvendor.data.models.ErrorModel
import com.raybit.newvendor.data.models.Resource
import com.raybit.newvendor.data.models.login.LoginResponse
import com.raybit.newvendor.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class AuthenticationViewModel
@Inject
    constructor(
    private val appDataManager: AppDataManager

) : ViewModel(){
    val loginData by lazy { SingleLiveEvent<LoginResponse>() }
    val register by lazy { SingleLiveEvent<Any>() }
    val error by lazy { SingleLiveEvent<ErrorModel>() }
    val loading by lazy { SingleLiveEvent<Boolean>() }
    fun login(hashMap: HashMap<String,String>) {
       // setIsLoading(true)
        viewModelScope.launch {
            appDataManager.login(hashMap).onEach { dataState ->
               // setIsLoading(false)
                when (dataState) {
                    is Resource.Success ->
                        loginData.value = dataState.data!!
                    is Resource.Error -> handleError(dataState)
                    else -> {}


                }
            }.launchIn(viewModelScope)
        }
    }
    fun register(hashMap: java.util.HashMap<String, RequestBody>, imageBody: MultipartBody.Part) {
        loading.value = true
        viewModelScope.launch {
            appDataManager.register(hashMap,imageBody).onEach { dataState ->
                loading.value = false
                when (dataState) {
                    is Resource.Success ->
                        register.value = dataState.data
                    is Resource.Error -> handleError(dataState)
                    else -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
    private fun <T> handleError(dataState: Resource.Error<T>) {
        error.value =  ErrorModel(
            message = dataState.msg,
            status = dataState.status?.name
        )

    }


}