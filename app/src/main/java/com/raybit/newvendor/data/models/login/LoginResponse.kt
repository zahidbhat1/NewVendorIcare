package com.raybit.newvendor.data.models.login

import com.raybit.newvendor.data.models.chat.Conversation
import com.raybit.newvendor.data.models.service.Category

data class LoginResponse(
    val created_at: String,
    val email: String,
    val email_verified_at: Any,
    val id: Int,
    val name: String,
    val wallet_balance: String,
    val role_id: Int,
    val online_status: Int,
    val category: Category,
    val conversation: Conversation,
    val user_qualifications: List<Qualifications>,
    val updated_at: String,
    val user_profile: UserProfile
)