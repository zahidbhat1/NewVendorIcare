package com.raybit.newvendor.data.models.login

import com.raybit.newvendor.data.models.service.Category

data class UserProfile(
    val address: String,
    val city: String,
    val country: String,
    val created_at: String,
    val dob: String,
    val email_verified_at: String,
    val fullname: String,
    val gender: String,
    val id: Int,
    val phone: String,
    val phone_verified_at: String,
    val state: String,
    val updated_at: String,
    var image_url: String,
    val category: Category,
    val user_qualifications: List<Qualifications>,
//    val image: Icon,
    val user_id: Int
)