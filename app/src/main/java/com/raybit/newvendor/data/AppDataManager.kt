package com.raybit.newvendor.data

import androidx.datastore.preferences.core.Preferences
import com.raybit.newvendor.data.dataStore.DataStoreHelper
import com.raybit.newvendor.data.models.AgoraData
import com.raybit.newvendor.data.models.ConsultationRequest
import com.raybit.newvendor.data.models.ImageResponse
import com.raybit.newvendor.data.models.Resource
import com.raybit.newvendor.data.models.chat.ChatMessage
import com.raybit.newvendor.data.models.chat.Conversation
import com.raybit.newvendor.data.models.clients.Client
import com.raybit.newvendor.data.models.login.LoginResponse
import com.raybit.newvendor.data.models.service.HomeResponse
import com.raybit.newvendor.data.models.wallet.Transaction
import com.raybit.newvendor.data.network.ApiService
import com.raybit.newvendor.utils.apiRequest
import com.raybit.newvendor.utils.apiRequestDirection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject


class AppDataManager
@Inject
constructor(
    private val mDataStoreHelper: DataStoreHelper,
    private val mApiHelper: ApiService
) : DataManager {
    override suspend fun setUserAsLoggedOut() {
        mDataStoreHelper.logOut()
    }

    override fun updateUserInf(): HashMap<String, String> {
        return HashMap<String, String>()
    }

    override fun updateApiHeader(userId: Long?, accessToken: String) {

    }

    override suspend fun <T> setKeyValue(key: Preferences.Key<T>, value: T) {
        mDataStoreHelper.setKeyValue(key, value)

    }

    override fun <T> getKeyValue(key: Preferences.Key<T>): Flow<Any?> {
        return mDataStoreHelper.getKeyValue(key)
    }

    override suspend fun <T> getGsonValue(key: Preferences.Key<String>, type: Class<T>): Flow<T?> {
        return mDataStoreHelper.getGsonValue(key, type)

    }

    override suspend fun addGsonValue(key: Preferences.Key<String>, value: String) {
        return mDataStoreHelper.addGsonValue(key, value)

    }


    override suspend fun logOut() {
        mDataStoreHelper.logOut()
    }

    override suspend fun clear() {
        mDataStoreHelper.clear()
    }

    override suspend fun getCurrentUserLoggedIn(): Flow<Boolean> {
        return mDataStoreHelper.getCurrentUserLoggedIn()
    }

    override suspend fun isDocumentUploaded(): Flow<Boolean> {
        return mDataStoreHelper.isDocumentUploaded()

    }

    suspend fun login(hashMap: HashMap<String, String>): Flow<Resource<LoginResponse>?> = flow {

        val data =
            apiRequest(Dispatchers.IO) {
                mApiHelper.login(hashMap)
            }
        emit(data)

    }

    suspend fun addMoney(id: Int, amount: String): Flow<Resource<Any>?> = flow {

        val data =
            apiRequest(Dispatchers.IO) {
                mApiHelper.addMoney(id, amount)
            }
        emit(data)

    }

    suspend fun register(
        hashMap: java.util.HashMap<String, RequestBody>,
        imageBody: MultipartBody.Part
    ): Flow<Resource<Any>?> = flow {

        val data =
            apiRequest(Dispatchers.IO) {
                mApiHelper.register(hashMap, imageBody)
            }
        emit(data)

    }
    suspend fun getTransactions(id: Int): Flow<Resource<ArrayList<Transaction>>?> = flow {

        val data =
            apiRequest(Dispatchers.IO) {
                mApiHelper.getTransactions(id)
            }
        emit(data)

    }
    suspend fun update(
        hashMap: java.util.HashMap<String, RequestBody>,
        imageBody: MultipartBody.Part
    ): Flow<Resource<LoginResponse>?> = flow {

        val data =
            apiRequest(Dispatchers.IO) {
                mApiHelper.update(hashMap, imageBody)
            }
        emit(data)

    }

    suspend fun update(hashMap: java.util.HashMap<String, RequestBody>): Flow<Resource<LoginResponse>?> =
        flow {

            val data =
                apiRequest(Dispatchers.IO) {
                    mApiHelper.update(hashMap)
                }
            emit(data)

        }

    suspend fun getHomeData(): Flow<Resource<HomeResponse>?> = flow {

        val data =
            apiRequest(Dispatchers.IO) {
                mApiHelper.getHomeData()
            }
        emit(data)

    }

    suspend fun getDoctors(id: Int): Flow<Resource<ArrayList<LoginResponse>>?> = flow {

        val data =
            apiRequest(Dispatchers.IO) {
                mApiHelper.getDoctors(id)
            }
        emit(data)

    }

    suspend fun getMyRequests(
        id: Int,
        type: String
    ): Flow<Resource<ArrayList<ConsultationRequest>>?> = flow {

        val data =
            apiRequest(Dispatchers.IO) {
                mApiHelper.getMyRequests(id, type)
            }
        emit(data)

    }

    suspend fun getConversations(id: Int): Flow<Resource<ArrayList<Conversation>>?> = flow {

        val data =
            apiRequest(Dispatchers.IO) {
                mApiHelper.getConversations(id)
            }
        emit(data)

    }

    suspend fun getChat(id: Int): Flow<Resource<ArrayList<ChatMessage>>?> = flow {

        val data =
            apiRequest(Dispatchers.IO) {
                mApiHelper.getChat(id)
            }
        emit(data)

    }

    suspend fun getUser(id: Int, userId: Int): Flow<Resource<Conversation>?> = flow {

        val data =
            apiRequest(Dispatchers.IO) {
                mApiHelper.getUser(id, userId)
            }
        emit(data)

    }

    suspend fun acceptRequest(id: Int, status: String): Flow<Resource<ConsultationRequest>?> =
        flow {

            val data =
                apiRequest(Dispatchers.IO) {
                    mApiHelper.acceptRequest(id, status)
                }
            emit(data)

        }

    suspend fun uploadFile(imageBody: MultipartBody.Part): Flow<Resource<ImageResponse>?> = flow {

        val data =
            apiRequest(Dispatchers.IO) {
                mApiHelper.uploadFile(imageBody)
            }
        emit(data)

    }
    suspend fun sendCallRequest(hashMap: java.util.HashMap<String, Any>): Flow<Resource<Any>?> = flow {

        val data =
            apiRequest(Dispatchers.IO) {
                mApiHelper.sendCallRequest(hashMap)
            }
        emit(data)

    }


    suspend fun getClient(id: Int): Flow<Resource<Client>?> = flow {
        val data =
            apiRequest(Dispatchers.IO) {
                mApiHelper.getClient(id)
            }
        emit(data)

    }

    suspend fun getVoiceToken(hashMap: java.util.HashMap<String, Any>): Flow<AgoraData?> =
        flow {
            val data =
                apiRequestDirection(Dispatchers.IO) {
                    mApiHelper.getVoiceToken(hashMap)
                }
            emit(data)

        }


}