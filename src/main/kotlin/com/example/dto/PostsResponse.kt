package com.example.dto

import kotlinx.serialization.Serializable

@Serializable
data class PostsResponse(
    val posts: List<PostDto>,
    val postsCount: Int
)