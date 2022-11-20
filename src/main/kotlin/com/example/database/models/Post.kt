package com.example.database.models

import org.jetbrains.exposed.sql.Table

data class Post(
    val id: Int,
    val content: String?,
    val imageUrl: String?
)

object Posts : Table() {
    val id = integer("id").autoIncrement()
    val content = largeText("content").nullable()
    val imageUrl = varchar("imageUrl", 128).nullable()

    override val primaryKey = PrimaryKey(id)
}