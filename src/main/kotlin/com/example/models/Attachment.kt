package com.example.models

import org.jetbrains.exposed.sql.Table

data class Attachment(
    val id: Int,
    val postId: Int,
    val path: String,
)

object Attachments : Table() {
    val id = integer("id")
        .autoIncrement()
    val postId = reference("postId", Posts.id)
    val path = varchar("path", 1024).nullable()

    override val primaryKey = PrimaryKey(id)
}