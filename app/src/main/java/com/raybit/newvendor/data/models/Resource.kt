package com.raybit.newvendor.data.models

sealed class Resource<T>(
    val data: T? = null,
    val msg: String? = null,
    val status: Status? = null
) {
    class Success<T>(data: T?, msg: String?, status: Status?) :
        Resource<T>(data = data, msg = msg, status = status)

    class Error<T>(message: String, status: Status) : Resource<T>(
        msg = message,
        status = status
    )

    class NetworkError<T>(message: String, status: Status) : Resource<T>(
        msg = message,
        status = status
    )
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING,
    NOTFOUND,
    UNAUTHROZIED
}

