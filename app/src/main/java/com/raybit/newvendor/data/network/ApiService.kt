package com.raybit.newvendor.data.network

import com.raybit.newvendor.data.network.NetworkConstants.Companion.GET_REQUEST_PATH
import com.raybit.newvendor.data.models.AgoraData
import com.raybit.newvendor.data.models.ApiResponse
import com.raybit.newvendor.data.models.ConsultationRequest
import com.raybit.newvendor.data.models.ImageResponse
import com.raybit.newvendor.data.models.chat.ChatMessage
import com.raybit.newvendor.data.models.chat.Conversation
import com.raybit.newvendor.data.models.clients.Client
import com.raybit.newvendor.data.models.login.LoginResponse
import com.raybit.newvendor.data.models.service.HomeResponse
import com.raybit.newvendor.data.models.wallet.Transaction
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*


interface ApiService {

    @FormUrlEncoded
    @POST(NetworkConstants.LOGIN)
    suspend fun login(@FieldMap hashMap: HashMap<String, String>): Response<ApiResponse<LoginResponse>>

    @FormUrlEncoded
    @POST(NetworkConstants.NOTIFY_CALL)
    suspend fun sendCallRequest(@FieldMap hashMap: HashMap<String, Any>): Response<ApiResponse<Any>>


    @POST(NetworkConstants.AGORA)
    suspend fun getVoiceToken(@Body hashMap: HashMap<String, Any>): Response<AgoraData>

    @POST(NetworkConstants.ADD_TO_WALLET)
    suspend fun addMoney(
        @Path("id") id: Int,
        @Path("amount") amount: String
    ): Response<ApiResponse<Any>>

    @POST(NetworkConstants.REQUESTS + "{id}/{status}")
    suspend fun acceptRequest(
        @Path("id") id: Int,
        @Path("status") status: String
    ): Response<ApiResponse<ConsultationRequest>>



    @GET(NetworkConstants.REQUESTS + GET_REQUEST_PATH)
    suspend fun getMyRequests(
        @Path("doctor_id") doctorId: Int,
        @Path("type") type: String
    ): Response<ApiResponse<ArrayList<ConsultationRequest>>>

    @Multipart
    @POST(NetworkConstants.UPDATE)
    suspend fun update(@PartMap hashMap: HashMap<String, RequestBody>): Response<ApiResponse<LoginResponse>>

    @Multipart
    @POST(NetworkConstants.REGISTER)
    suspend fun register(
        @PartMap hashMap: HashMap<String, RequestBody>,
        @Part image: MultipartBody.Part?
    ): Response<ApiResponse<Any>>
    @GET(NetworkConstants.TRANSACTIONS)
    suspend fun getTransactions(@Path("userId") id: Int): Response<ApiResponse<ArrayList<Transaction>>>

    @Multipart
    @POST(NetworkConstants.UPLOAD_FILE)
    suspend fun uploadFile(
        @Part image: MultipartBody.Part?
    ): Response<ApiResponse<ImageResponse>>

    @Multipart
    @POST(NetworkConstants.UPDATE)
    suspend fun update(
        @PartMap hashMap: HashMap<String, RequestBody>,
        @Part image: MultipartBody.Part?
    ): Response<ApiResponse<LoginResponse>>


    @GET(NetworkConstants.DOCTORS + "/{id}")
    suspend fun getDoctors(@Path("id") id: Int): Response<ApiResponse<ArrayList<LoginResponse>>>

    @GET(NetworkConstants.CONVERSATIONS + "/{id}")
    suspend fun getConversations(@Path("id") id: Int): Response<ApiResponse<ArrayList<Conversation>>>

    @GET(NetworkConstants.CHATS + "/{id}")
    suspend fun getChat(@Path("id") id: Int): Response<ApiResponse<ArrayList<ChatMessage>>>

    @GET(NetworkConstants.USER + "/{id}")
    suspend fun getUser(
        @Path("id") id: Int,
        @Query("user_id") userId: Int
    ): Response<ApiResponse<Conversation>>

    @GET(NetworkConstants.HOME)
    suspend fun getHomeData(): Response<ApiResponse<HomeResponse>>

    @GET(NetworkConstants.CLIENT + "/{user_id}")
    suspend fun getClient(@Path("user_id") id: Int): Response<ApiResponse<Client>>


}