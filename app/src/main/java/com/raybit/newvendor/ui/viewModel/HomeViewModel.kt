package com.raybit.newvendor.ui.viewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.aamir.icarepro.data.models.carwash.CarWash
import com.aamir.icarepro.data.models.carwash.ServiceDetails

import com.aamir.icarepro.data.models.service.carRepair.CarRepairBooking
import com.raybit.newvendor.data.AppDataManager
import com.raybit.newvendor.data.models.AgoraData
import com.raybit.newvendor.data.models.ConsultationRequest
import com.raybit.newvendor.data.models.ErrorModel
import com.raybit.newvendor.data.models.ImageResponse
import com.raybit.newvendor.data.models.Resource
import com.raybit.newvendor.data.models.chat.ChatMessage
import com.raybit.newvendor.data.models.chat.Conversation
import com.raybit.newvendor.data.models.clients.Client
import com.raybit.newvendor.data.models.clients.ClientsResponse
import com.raybit.newvendor.data.models.directions.Direction
import com.raybit.newvendor.data.models.login.LoginResponse
import com.raybit.newvendor.data.models.notifications.Notify
import com.raybit.newvendor.data.models.service.HomeResponse
import com.raybit.newvendor.data.models.service.Insurance
import com.raybit.newvendor.data.models.wallet.Transaction
import com.raybit.newvendor.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*
import javax.inject.Inject


@ExperimentalCoroutinesApi
@HiltViewModel
class HomeViewModel
@Inject
constructor(private val appDataManager: AppDataManager) : ViewModel() {


    val docs by lazy { SingleLiveEvent<ArrayList<LoginResponse>>() }
    val myRequests by lazy { SingleLiveEvent<ArrayList<ConsultationRequest>>() }
    val conversations by lazy { SingleLiveEvent<ArrayList<Conversation>>() }
    val transactions by lazy { SingleLiveEvent<ArrayList<Transaction>>() }
    val request by lazy { SingleLiveEvent<ConsultationRequest>() }
    val file by lazy { SingleLiveEvent<ImageResponse>() }
    val chats by lazy { SingleLiveEvent<ArrayList<ChatMessage>>() }
    val user by lazy { SingleLiveEvent<Conversation>() }
    val update by lazy { SingleLiveEvent<LoginResponse>() }
    val homeData by lazy { SingleLiveEvent<HomeResponse>() }
    val clients by lazy { SingleLiveEvent<ClientsResponse>() }
    val client by lazy { SingleLiveEvent<Client>() }
    val repairBookings by lazy { SingleLiveEvent<ArrayList<CarRepairBooking>>() }
    val repairBooking by lazy { SingleLiveEvent<CarRepairBooking>() }
    val carWash by lazy { SingleLiveEvent<ArrayList<CarWash>>() }
    val service by lazy { SingleLiveEvent<ArrayList<CarWash>>() }
    val singleService by lazy { SingleLiveEvent<CarWash>() }
    val carWashAddon by lazy { SingleLiveEvent<ArrayList<ServiceDetails>>() }
    val singleCarWash by lazy { SingleLiveEvent<CarWash>() }
    val singleInsurance by lazy { SingleLiveEvent<Insurance>() }
    val insurance by lazy { SingleLiveEvent<ArrayList<Insurance>>() }
    val addMoney by lazy { SingleLiveEvent<Any>() }
    val loading by lazy { SingleLiveEvent<Boolean>() }
    val direction by lazy { SingleLiveEvent<Direction>() }
    val agoraData by lazy { SingleLiveEvent<AgoraData>() }
    val notifyCall by lazy { SingleLiveEvent<Any>() }
    val notifications by lazy { SingleLiveEvent<ArrayList<Notify>>() }

    //
    val error by lazy { SingleLiveEvent<ErrorModel>() }

    fun update(hashMap: HashMap<String, RequestBody>, imageBody: MultipartBody.Part) {
        loading.value = true
        viewModelScope.launch {
            appDataManager.update(hashMap, imageBody).onEach { dataState ->
                loading.value = false
                when (dataState) {
                    is Resource.Success ->
                        update.value = dataState.data

                    is Resource.Error -> handleError(dataState)
                    else -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
    fun getTransactions(id: Int) {
        loading.value = true
        viewModelScope.launch {
            appDataManager.getTransactions(id).onEach { dataState ->
                loading.value = false
                when (dataState) {
                    is Resource.Success ->
                        transactions.value = dataState.data
                    is Resource.Error -> handleError(dataState)
                    else -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
    fun addMoney(id: Int, amount: String) {
        loading.value = true
        viewModelScope.launch {
            appDataManager.addMoney(id, amount).onEach { dataState ->
                loading.value = false
                when (dataState) {
                    is Resource.Success ->
                        addMoney.value = dataState.data

                    is Resource.Error -> handleError(dataState)
                    else -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun update(hashMap: HashMap<String, RequestBody>) {
        loading.value = true
        viewModelScope.launch {
            appDataManager.update(hashMap).onEach { dataState ->
                loading.value = false
                when (dataState) {
                    is Resource.Success ->
                        update.value = dataState.data

                    is Resource.Error -> handleError(dataState)
                    else -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getClient(id: Int) {
        loading.value = true
        viewModelScope.launch {
            appDataManager.getClient(id).onEach { dataState ->
                loading.value = false
                when (dataState) {
                    is Resource.Success ->
                        client.value = dataState.data

                    is Resource.Error -> handleError(dataState)
                    else -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getHomeData() {
        loading.value = true
        viewModelScope.launch {
            appDataManager.getHomeData().onEach { dataState ->
                loading.value = false
                when (dataState) {
                    is Resource.Success ->
                        homeData.value = dataState.data

                    is Resource.Error -> handleError(dataState)
                    else -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getDoctors(id: Int) {
        loading.value = true
        viewModelScope.launch {
            appDataManager.getDoctors(id).onEach { dataState ->
                loading.value = false
                when (dataState) {
                    is Resource.Success ->
                        docs.value = dataState.data

                    is Resource.Error -> handleError(dataState)
                    else -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getMyRequests(id: Int, type: String) {
        loading.value = true
        viewModelScope.launch {
            appDataManager.getMyRequests(id, type).onEach { dataState ->
                loading.value = false
                when (dataState) {
                    is Resource.Success ->
                        myRequests.value = dataState.data

                    is Resource.Error -> handleError(dataState)
                    else -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getConversations(id: Int) {
        loading.value = true
        viewModelScope.launch {
            appDataManager.getConversations(id).onEach { dataState ->
                loading.value = false
                when (dataState) {
                    is Resource.Success ->
                        conversations.value = dataState.data

                    is Resource.Error -> handleError(dataState)
                    else -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getChat(id: Int) {
        loading.value = true
        viewModelScope.launch {
            appDataManager.getChat(id).onEach { dataState ->
                loading.value = false
                when (dataState) {
                    is Resource.Success ->
                        chats.value = dataState.data

                    is Resource.Error -> handleError(dataState)
                    else -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun <T> handleError(dataState: Resource.Error<T>) {
        error.value = ErrorModel(
            message = dataState.msg,
            status = dataState.status?.name
        )

    }

    fun loadUserData(id: Int, userId: Int) {
        loading.value = true
        viewModelScope.launch {
            appDataManager.getUser(id, userId).onEach { dataState ->
                loading.value = false
                when (dataState) {
                    is Resource.Success ->
                        user.value = dataState.data

                    is Resource.Error -> handleError(dataState)
                    else -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun acceptRequest(id: Int, status: String) {
        loading.value = true
        viewModelScope.launch {
            appDataManager.acceptRequest(id, status).onEach { dataState ->
                loading.value = false
                when (dataState) {
                    is Resource.Success ->
                        request.value = dataState.data

                    is Resource.Error -> handleError(dataState)
                    else -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun uploadFile(imageBody: MultipartBody.Part) {
        loading.value = true
        viewModelScope.launch {
            appDataManager.uploadFile(imageBody).onEach { dataState ->
                loading.value = false
                when (dataState) {
                    is Resource.Success ->
                        file.value = dataState.data

                    is Resource.Error -> handleError(dataState)
                    else -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getVoiceToken(hashMap: HashMap<String, Any>) {
        loading.value = true
        viewModelScope.launch {
            appDataManager.getVoiceToken(hashMap).onEach { dataState ->
                loading.value = false
                agoraData.value = dataState

            }.launchIn(viewModelScope)
        }
    }
    fun sendCallRequest(hashMap: HashMap<String, Any>) {
        loading.value = true
        viewModelScope.launch {
            appDataManager.sendCallRequest(hashMap).onEach { dataState ->
                loading.value = false
                when (dataState) {
                    is Resource.Success ->
                        notifyCall.value = dataState.data
                    is Resource.Error -> handleError(dataState)
                    else -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }


}