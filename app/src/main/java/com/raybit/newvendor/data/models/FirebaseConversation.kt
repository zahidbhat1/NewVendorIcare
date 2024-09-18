package com.raybit.newvendor.data.models

import java.io.Serializable

data class FirebaseConversation(
    val id: String,
    val lastMessageBy: Int,
    val read: Boolean,
    val updatedAt: String,
    val lastMessageSent: String,
    val membersIds: List<Int>,
    val membersNames: List<String>,
    val membersProfiles: List<String>
):Serializable