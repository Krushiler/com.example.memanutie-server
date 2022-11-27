package com.example.data.dao

import com.example.models.Attachment
import com.example.models.Attachments
import com.example.models.Post
import com.example.models.Posts
import io.ktor.utils.io.core.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

interface IMemeDao : Closeable {
    fun init()
    fun createPost(content: String? = null, attachments: List<String>? = null): Int
    fun getPost(id: Int): Post?
    fun getAllPosts(): List<Post>
    fun deletePost(id: Int)
}

class RealMemeDao(private val db: Database) : IMemeDao {

    private fun ResultRow.toPost() =
        Post(
            id = this[Posts.id],
            content = this[Posts.content],
            attachments = getPostAttachments(this[Posts.id])
        )

    private fun ResultRow.toAttachment() =
        Attachment(
            id = this[Attachments.id],
            postId = this[Attachments.postId],
            path = this[Attachments.path] ?: ""
        )


    override fun init() = transaction(db) {
        SchemaUtils.create(Posts)
        SchemaUtils.create(Attachments)
    }

    override fun createPost(content: String?, attachments: List<String>?) = transaction(db) {
        val newPostId = Posts.insert { postRow ->
            postRow[Posts.content] = content
        } get Posts.id

        attachments?.forEach { attachmentPath ->
            Attachments.insert { fileRow ->
                fileRow[postId] = newPostId
                fileRow[path] = attachmentPath
            }
        }

        return@transaction newPostId
    }

    override fun getPost(id: Int): Post? = transaction(db) {
        Posts.select { Posts.id eq id }.map { row ->
            row.toPost()
        }.singleOrNull()
    }

    override fun getAllPosts(): List<Post> = transaction(db) {
        Posts.selectAll().map { row ->
            row.toPost()
        }
    }

    override fun deletePost(id: Int) = transaction {
        Attachments.deleteWhere {
            this.postId eq id
        }
        Posts.deleteWhere {
            this.id eq id
        }
        Unit
    }

    private fun getPostAttachments(postId: Int): List<Attachment> = transaction(db) {
        Attachments.select { Attachments.postId eq postId }.map { row ->
            row.toAttachment()
        }
    }

    override fun close() {}
}