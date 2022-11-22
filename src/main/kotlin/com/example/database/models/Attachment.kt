package com.example.database.models

import org.jetbrains.exposed.sql.Table

data class Attachment(
    val id: Int,
    val postId: Int,
    val path: String,
)

object Attachments: Table() {
    val id = integer("id").autoIncrement()
    val postId = integer("postId")
    val path = varchar("path", 1024).nullable()
}