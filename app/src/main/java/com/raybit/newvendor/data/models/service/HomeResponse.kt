package com.raybit.newvendor.data.models.service

import com.raybit.newvendor.data.models.login.UserProfile

data class HomeResponse(
    val categories: List<Category>,
    val banners: List<Category>,
    val popular_docs: List<UserProfile>
)