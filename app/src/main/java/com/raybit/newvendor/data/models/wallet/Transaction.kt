package com.raybit.newvendor.data.models.wallet

data class Transaction(
    val amount: String,
    val created_at: String,
    val id: Int,
    val reason: String,
    val type: String,
    val updated_at: String,
    val wallet_id: Int
)