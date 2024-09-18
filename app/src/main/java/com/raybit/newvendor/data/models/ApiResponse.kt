package com.raybit.newvendor.data.models

import com.google.gson.annotations.SerializedName


class ApiResponse<T> {
    @SerializedName("success",alternate = ["statusCode"])
    val status: Boolean? = null
    @SerializedName("message",alternate = ["msg"])
    val msg: String? = null
    val data: T? = null
}