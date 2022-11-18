package com.example.plugins

import com.example.database.dao.IMemeDao
import com.example.dto.CreatePostRequest
import com.example.dto.PostDto
import com.example.dto.PostsResponse
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*

fun Application.configureRouting(
    memeDao: IMemeDao
) {

    routing {
        get("/posts") {

            val posts = memeDao.getAllPosts().map {
                PostDto(it.content)
            }

            call.respond(
                PostsResponse(posts, posts.size)
            )
        }
        post("/post/create") {
            val postData = call.receive<CreatePostRequest>()
            memeDao.createPost(postData.content)
            call.respond("ВЫ НАПИСАЛИ ${postData.content}")
        }
    }
    routing {
    }
}