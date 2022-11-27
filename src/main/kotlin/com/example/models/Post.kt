package com.example.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.Date

data class Post(
    val id: Int,
    val content: String?,
    val attachments: List<Attachment>,
    val comments: List<Comment>,
    val date: Date
)

object Posts : Table() {
    val id = integer("id")
        .autoIncrement()

    val content = largeText("content").nullable()
    val date = datetime("date").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(id)
}