package com.example.database.models

import org.jetbrains.exposed.sql.Table

data class Post(
    val id: Long,
    val content: String
)

object Posts : Table() {
    val id = long("id").autoIncrement()
    val content = varchar("content", 500)

    override val primaryKey = PrimaryKey(id)
}