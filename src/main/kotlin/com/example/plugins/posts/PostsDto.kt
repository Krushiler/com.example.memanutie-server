package com.example.plugins.posts

import com.example.database.models.Post
import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    val id: Int, val content: String?, val image: String?
) {
    companion object {
        fun fromPost(post: Post, serverUrl: String = "") = PostDto(
            id = post.id,
            content = post.content,
            image = if (post.imageUrl != null) serverUrl + post.imageUrl else null
        )
    }
}

@Serializable
data class PostsResponse(
    val posts: List<PostDto>, val postsCount: Int
)

@Serializable
data class CreatePostRequest(
    val content: String?
)

@Serializable
data class DeletePostRequest(
    val id: Int
)

@Serializable
data class DeletePostResponse(
    val deletedId: Int
)