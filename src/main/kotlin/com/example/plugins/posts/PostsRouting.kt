package com.example.plugins.posts

import com.example.database.dao.IMemeDao
import com.example.util.extension
import com.example.util.generateFilename
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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
                        files.add(generateFilename("upload", part.originalFileName!!.extension()))
                        val file = File("files/${files.last()}")
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
        }
        post("/delete") {
            val postData = call.receive<DeletePostRequest>()
            memeDao.deletePost(postData.id)
            call.respond(DeletePostResponse(postData.id))
        }
    }
}