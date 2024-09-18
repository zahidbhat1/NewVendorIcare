package com.raybit.newvendor.data.models

import com.raybit.newvendor.data.models.login.UserProfile

data class  ConsultationRequest(
    val approved_at: Any,
    val category_id: Int,
    val created_at: String,
    val doctor_id: Int,
    val id: Int,
    val rejected_at: Any,
    var status: String,
    val type: String,
    val updated_at: String,
    val user_id: Int,
    val user_profile: UserProfile
)