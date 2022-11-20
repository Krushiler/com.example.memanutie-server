package com.example.database.dao

import com.example.database.models.Post
import com.example.database.models.Posts
import io.ktor.utils.io.core.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

interface IMemeDao : Closeable {
    fun init()
    fun createPost(content: String? = null, imageUrl: String? = null)
    fun getPost(id: Int): Post?
    fun getAllPosts(): List<Post>
    fun deletePost(id: Int)
}

class RealMemeDao(private val db: Database) : IMemeDao {
    private fun ResultRow.createPost(): Post {
        return Post(
            this[Posts.id], this[Posts.content], this[Posts.imageUrl]
        )
    }

    override fun init() = transaction(db) {
        SchemaUtils.create(Posts)
    }

    override fun createPost(content: String?, imageUrl: String?) = transaction(db) {
        Posts.insert {
            it[Posts.content] = content
            it[Posts.imageUrl] = imageUrl
        }
        Unit
    }

    override fun getPost(id: Int): Post? = transaction(db) {
        Posts.select { Posts.id eq id }.map {
            it.createPost()
        }.singleOrNull()
    }

    override fun getAllPosts(): List<Post> = transaction(db) {
        Posts.selectAll().map {
            it.createPost()
        }
    }

    override fun deletePost(id: Int) = transaction {
        Posts.deleteWhere {
            this.id eq id
        }
        Unit
    }

    override fun close() {}
}