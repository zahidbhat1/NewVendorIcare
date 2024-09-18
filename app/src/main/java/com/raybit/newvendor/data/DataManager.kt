package com.raybit.newvendor.data

import com.raybit.newvendor.data.dataStore.DataStoreHelper

interface DataManager : DataStoreHelper {

    suspend fun setUserAsLoggedOut()

    fun updateUserInf(): HashMap<String, String>

    fun updateApiHeader(userId: Long?, accessToken: String)
}
