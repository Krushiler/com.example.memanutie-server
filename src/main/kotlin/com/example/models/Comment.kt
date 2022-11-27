package com.example.models

import org.jetbrains.exposed.sql.Table

data class Comment(
    val id: Int,
    val postId: Int,
    val content: String
)

object Comments : Table() {
    val id = integer("id")
        .autoIncrement()
    val postId = reference("postId", Posts.id)
    val content = largeText("content")

    override val primaryKey = PrimaryKey(id)
}
