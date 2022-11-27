package com.example.plugins.posts

import com.example.data.dao.IMemeDao
import com.example.staticFilesFolder
import com.example.util.extension
import com.example.util.generateFilename
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.errors.*
import java.io.File

fun Route.postsRouting(memeDao: IMemeDao, serverUrl: String) {
    route("post") {
        get {
            val postId = call.request.queryParameters["id"]?.toInt()
            if (postId != null) {
                val post = memeDao.getPost(postId)
                if (post != null) call.respond(PostDto.fromPost(post, serverUrl))
                else call.respondText { "no such post" }
            }
            call.respondText { "failure" }
        }
        get("/list") {
            val posts = memeDao.getAllPosts().map {
                PostDto.fromPost(it, serverUrl = serverUrl)
            }
            call.respond(
                PostsResponse(posts, posts.size)
            )
        }
        post("/create") {
            try {
                val multipartBody = call.receiveMultipart()

                val files = mutableListOf<String>()
                var content: String? = null

                multipartBody.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            when (part.name) {
                                "content" -> {
                                    content = part.value
                                }
                            }
                        }

                        is PartData.FileItem -> {
                            files.add(generateFilename("upload", part.originalFileName.extension()))
                            val file = File("$staticFilesFolder/${files.last()}")
                            part.streamProvider().use { inputStream ->
                                file.outputStream().buffered().use { outputStream ->
                                    inputStream.copyTo(outputStream)
                                }
                            }
                        }

                        else -> {}
                    }
                }

                val createdPostId = memeDao.createPost(
                    content = content,
                    attachments = files
                )

                val createdPost = memeDao.getPost(createdPostId)
                if (createdPost != null) {
                    call.respond(PostDto.fromPost(createdPost, serverUrl = serverUrl))
                } else {
                    call.respondText { "failure" }
                }
            } catch (e: IOException) {
                call.respondText { e.localizedMessage }
            }
        }
        post("/delete") {
            val postData = call.receive<DeletePostRequest>()
            val postToDelete = memeDao.getPost(postData.id)

            if (postToDelete != null) {
                memeDao.deletePost(postToDelete.id)
                postToDelete.attachments.forEach {
                    val file = File("$staticFilesFolder/${it.path}")
                    if (file.exists()) file.delete()
                }
                call.respondText { "success" }
            } else {
                call.respondText { "failure" }
            }
        }
        post("/comment") {
            val commentData = call.receive<CommentPostRequest>()
            memeDao.addComment(commentData.postId, commentData.content)
            val post = memeDao.getPost(commentData.postId)
            if (post != null) {
                call.respond(PostDto.fromPost(post, serverUrl = serverUrl))
            } else {
                call.respondText { "failure" }
            }
        }
    }
}