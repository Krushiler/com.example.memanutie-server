package com.example.database.dao

import com.example.database.models.Post
import com.example.database.models.Posts
import io.ktor.utils.io.core.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

interface IMemeDao: Closeable {
    fun init()
    fun createPost(content: String)
    fun getPost(id: Long) : Post?
    fun getAllPosts() : List<Post>
}

class RealMemeDao(private val db: Database): IMemeDao {
    override fun init() = transaction(db) {
        SchemaUtils.create(Posts)
    }

    override fun createPost(content: String) = transaction(db) {
        Posts.insert {
            it[Posts.content] = content
        }
        Unit
    }

    override fun getPost(id: Long): Post? = transaction(db) {
        Posts.select { Posts.id eq id }.map {
            Post(
                it[Posts.id], it[Posts.content]
            )
        }.singleOrNull()
    }

    override fun getAllPosts(): List<Post> = transaction(db) {
        Posts.selectAll().map {
            Post(
                it[Posts.id], it[Posts.content]
            )
        }
    }

    override fun close() {}
}