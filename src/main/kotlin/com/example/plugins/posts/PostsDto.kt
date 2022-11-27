package com.example.plugins.posts

import com.example.models.Attachment
import com.example.models.Comment
import com.example.models.Post
import kotlinx.serialization.Serializable
import java.text.DateFormat
import java.text.SimpleDateFormat

@Serializable
data class PostDto(
    val id: Int,
    val content: String?,
    val attachments: List<AttachmentDto>,
    val attachmentsCount: Int,
    val comments: List<CommentDto>,
    val commentsCount: Int,
    val date: String
) {
    companion object {
        fun fromPost(post: Post, serverUrl: String = "") = PostDto(
            id = post.id,
            content = post.content,
            attachments = post.attachments.map {
                AttachmentDto.toDto(it, serverUrl)
            },
            attachmentsCount = post.attachments.size,
            comments = post.comments.map {
                CommentDto.toDto(it)
            },
            commentsCount = post.comments.size,
            date = post.date.run {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
                dateFormat.format(this)
            }
        )
    }
}

@Serializable
data class AttachmentDto(val id: Int, val postId: Int, val path: String) {
    companion object {
        fun toDto(attachment: Attachment, serverUrl: String) = AttachmentDto(
            attachment.id,
            attachment.postId,
            serverUrl + attachment.path
        )
    }
}

@Serializable
data class CommentDto(val id: Int, val postId: Int, val content: String) {
    companion object {
        fun toDto(comment: Comment) = CommentDto(
            comment.id,
            comment.postId,
            comment.content
        )
    }
}

@Serializable
data class PostsResponse(
    val posts: List<PostDto>, val postsCount: Int,
)

@Serializable
data class DeletePostRequest(
    val id: Int,
)

@Serializable
data class CommentPostRequest(
    val postId: Int,
    val content: String,
)