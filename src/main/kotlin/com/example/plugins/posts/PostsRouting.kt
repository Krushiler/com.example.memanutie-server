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
    route("posts") {
        get {
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

                memeDao.createPost(
                    content = content,
                    attachments = files
                )
                call.respondText { "success" }
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
    }
}