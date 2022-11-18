package com.example.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreatePostRequest(
    val content: String
)