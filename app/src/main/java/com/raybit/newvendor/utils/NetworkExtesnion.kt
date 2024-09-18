package com.raybit.newvendor.utils


import com.raybit.newvendor.data.models.ApiResponse
import com.raybit.newvendor.data.models.Resource
import com.raybit.newvendor.data.models.Status
import com.raybit.newvendor.utils.GenericErrors.ERROR_UNKNOWN
import com.raybit.newvendor.utils.NetworkErrors.NETWORK_ERROR
import com.raybit.newvendor.utils.NetworkErrors.NETWORK_ERROR_TIMEOUT
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.UnknownHostException

suspend fun <T : Any> apiRequest(
    dispatcher: CoroutineDispatcher,
    call: suspend () -> Response<ApiResponse<T>>
): Resource<T>? {

    return withContext(dispatcher) {
        createResource(call)
    }


}

suspend fun <T : Any> apiRequestDirection(
    dispatcher: CoroutineDispatcher,
    call: suspend () -> Response<T>
): T? {

    return withContext(dispatcher) {
        createResourceDirection(call)
    }


}

suspend fun <T : Any> createResourceDirection(call: suspend () -> Response<T>): T? {

    var resource: T? = null
    val response = call.invoke()
    if (response.isSuccessful) {
        response.body()?.let {
            resource = it
        }
    }
    return resource

}

suspend fun <T : Any> createResource(call: suspend () -> Response<ApiResponse<T>>): Resource<T>? {

    var resource: Resource<T>? = null

    try {

        val response = call.invoke()
        if (response.isSuccessful) {
            response.body()?.let {
                resource =
                    if (response.code() == 200 && response.body()?.status == true)
                        Resource.Success(it.data, it.msg, Status.SUCCESS)
                    else if (response.code() == 200 && response.body()?.status == false)
                        Resource.Error(
                            message = it.msg ?: ERROR_UNKNOWN,
                            status = Status.UNAUTHROZIED
                        )
                    else
                        Resource.Error(message = it.msg ?: ERROR_UNKNOWN, status = Status.ERROR)

            }
        } else {
            val error = response.errorBody()?.string()

            val message = StringBuilder()
            error?.let {
                resource = try {
                    message.append(JSONObject(it).getString("message"))
                    if (response.code() == 401) {
                        Resource.Error(
                            message = message.toString(),
                            status = Status.UNAUTHROZIED
                        )
                    } else if (response.code() == 404) {
                        Resource.Error(
                            message = message.toString(),
                            status = Status.NOTFOUND
                        )
                    } else {
                        Resource.Error(message = message.toString(), status = Status.ERROR)
                    }
                } catch (e: JSONException) {

                    message.append(error)
                    if (response.code() == 401) {
                        Resource.Error(
                            message = message.toString(),
                            status = Status.UNAUTHROZIED
                        )
                    } else
                        Resource.Error(response.message(), Status.ERROR)

                }
            }
        }

    } catch (throwable: Throwable) {
        throwable.printStackTrace()
        resource = when (throwable) {
            is TimeoutCancellationException -> {
                Resource.Error(NETWORK_ERROR_TIMEOUT, Status.ERROR)
            }
            is IOException -> {
                Resource.NetworkError(NETWORK_ERROR, Status.ERROR)
            }

            is UnknownHostException -> {
                Resource.NetworkError(NETWORK_ERROR, Status.ERROR)
            }

            is HttpException -> {
                val errorResponse = convertErrorBody(throwable)
                Resource.Error(errorResponse ?: "Something went wrong", Status.ERROR)
            }
            else -> {
                //   cLog(NETWORK_ERROR_UNKNOWN)
                Resource.Error("Something went wrong", Status.ERROR)
            }
        }
    }
    return resource

}


private fun convertErrorBody(throwable: HttpException): String? {
    return try {
        throwable.response()?.errorBody()?.string()
    } catch (exception: Exception) {
        ERROR_UNKNOWN
    }
}
