package com.raybit.newvendor.utils

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/*
 * Created by Ankit jindal on 19/12/2015.
 */
object RetrofitUtils {
    fun textToRequestBody(text: String): RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), text)

    fun imageToRequestBody(imageFile: File): RequestBody =
            RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)

    fun imageToRequestBodyKey(parameterName: String, fileName: String): String =
            "$parameterName\"; filename=\"$fileName"

    fun toImageRequestBody(file:File): RequestBody{
        return  file.asRequestBody("image/*".toMediaType())
    }


}