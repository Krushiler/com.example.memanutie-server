package com.example.database.dao

import com.example.database.models.Attachment
import com.example.database.models.Attachments
import com.example.database.models.Post
import com.example.database.models.Posts
import io.ktor.utils.io.core.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

interface IMemeDao : Closeable {
    fun init()
    fun createPost(content: String? = null, attachments: List<String>? = null)
    fun getPost(id: Int): Post?
    fun getAllPosts(): List<Post>
    fun deletePost(id: Int)
}

class RealMemeDao(private val db: Database) : IMemeDao {

    override fun init() = transaction(db) {
        SchemaUtils.create(Posts)
        SchemaUtils.create(Attachments)
    }

    override fun createPost(content: String?, attachments: List<String>?) = transaction(db) {
        val postId = Posts.insert { postRow ->
            postRow[Posts.content] = content
        } get Posts.id

        attachments?.forEach { attachmentPath ->
            Attachments.insert { fileRow ->
                fileRow[Attachments.postId] = postId
                fileRow[Attachments.path] = attachmentPath
            }
        }
        Unit
    }

    override fun getPost(id: Int): Post? = transaction(db) {
        Posts.select { Posts.id eq id }.map { row->
            Post(
                id = row[Posts.id],
                content = row[Posts.content],
                attachments = getPostAttachments(row[Posts.id])
            )
        }.singleOrNull()
    }

    override fun getAllPosts(): List<Post> = transaction(db) {
        Posts.selectAll().map { row ->
            Post(
                id = row[Posts.id],
                content = row[Posts.content],
                attachments = getPostAttachments(row[Posts.id])
            )
        }
    }

    override fun deletePost(id: Int) = transaction {
        Posts.deleteWhere {
            this.id eq id
        }
        Unit
    }

    private fun getPostAttachments(postId: Int): List<Attachment> = transaction(db) {
        Attachments.select { Attachments.postId eq postId }.map { row ->
            Attachment(
                id = row[Attachments.id],
                postId = row[Attachments.postId],
                path = row[Attachments.path] ?: ""
            )
        }
    }

    override fun close() {}
}