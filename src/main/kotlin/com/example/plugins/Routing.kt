package com.example.plugins

import com.example.data.dao.IMemeDao
import com.example.plugins.posts.postsRouting
import com.example.staticFilesFolder
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import java.io.File

fun Application.configureRouting(
    memeDao: IMemeDao,
    serverUrl: String
) {
    routing {
        static {
            staticRootFolder = File(environment?.rootPath)
            files(staticFilesFolder)
        }
        route("api") {
            postsRouting(memeDao, serverUrl)
        }
    }
}