package com.example.dto

import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    val content: String,
    /*val likesCount: String,
    val dislikesCount: String,
    val imageUrl: String,
    val dateTime: String,*/
)
