package com.raybit.newvendor.data.models.chat

import com.raybit.newvendor.data.models.login.LoginResponse

data class Conversation(
    val created_at: String,
    var id: Int,
    val updated_at: String,
    var user: LoginResponse,
    val last_message: ChatMessage,
    val user_1: Int,
    val user_2: Int
)