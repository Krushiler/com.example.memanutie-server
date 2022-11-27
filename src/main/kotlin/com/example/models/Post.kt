package com.example.models

import org.jetbrains.exposed.sql.Table

data class Post(
    val id: Int,
    val content: String?,
    val attachments: List<Attachment>
)

object Posts : Table() {
    val id = integer("id")
        .autoIncrement()

    val content = largeText("content").nullable()

    override val primaryKey = PrimaryKey(id)
}