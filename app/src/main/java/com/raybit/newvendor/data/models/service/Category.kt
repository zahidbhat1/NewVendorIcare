package com.raybit.newvendor.data.models.service

data class Category(
    val banner: Icon,
    val banner_content_type: String,
    val banner_file_name: String,
    val banner_file_size: Int,
    var layout: Int=0,
    val banner_updated_at: String,
    var banner_url: String,
    val created_at: String,
    val icon: Icon,
    val icon_content_type: String,
    val icon_file_name: String,
    val icon_file_size: Int,
    val icon_updated_at: String,
    val icon_url: String,
    val id: Int,
    val title: String,
    val updated_at: String
)