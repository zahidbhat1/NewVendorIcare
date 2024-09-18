package com.raybit.newvendor.data.network

import com.raybit.newvendor.data.dataStore.DataStoreConstants
import com.raybit.newvendor.data.dataStore.DataStoreHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthenticationInterceptor
@Inject
constructor(
    private val mDataStoreHelper: DataStoreHelper
) : Interceptor {


    var userLoggedIn = false
    var accessToken: String? = null
    var cartId: String? = null

    init {

        GlobalScope.launch {
            mDataStoreHelper.getCurrentUserLoggedIn().collectLatest {
                userLoggedIn = it
            }
        }


        GlobalScope.launch {
            mDataStoreHelper.getKeyValue(DataStoreConstants.ACCESS_TOKEN).collectLatest {
                accessToken = it?.toString()
            }
        }


        GlobalScope.launch {
            mDataStoreHelper.getKeyValue(DataStoreConstants.CART_ID).collectLatest {
                cartId = it?.toString()
            }
        }

    }

    @Volatile
    var secret_key: String? = null


    fun setSecret(secret_key: String): String? {
        this.secret_key = secret_key
        return this.secret_key
    }


    override fun intercept(chain: Interceptor.Chain): Response = chain.request().let {


        val requestBuilder = chain.request().newBuilder()

//        if (userLoggedIn || !accessToken.isNullOrEmpty()) {
////            Log.e("ACCESS :: ", accessToken!!)
//            requestBuilder.addHeader(AUTHORIZATION, "Bearer ${accessToken!!}")
//            cartId?.let { it1 -> requestBuilder.addHeader(AppConstants.CART_USER_ID, it1) }
//        }


        return chain.proceed(requestBuilder.build())
    }
}