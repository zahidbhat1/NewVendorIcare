package com.raybit.newvendor.data.models

import java.io.Serializable

data class FirebaseConsultationRequest(
    val id: String,
    val consultationCount: Int,
    val status: String,
    val timeStamp: Long,
    val userId:Int
):Serializable