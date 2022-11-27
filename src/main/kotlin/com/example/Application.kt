package com.example

import com.example.data.dao.IMemeDao
import com.example.data.dao.RealMemeDao
import com.example.plugins.configureRouting
import com.example.util.getServerUrl
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.jetbrains.exposed.sql.Database
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
    }
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
    Files.createDirectories(Paths.get(staticFilesFolder))
    val memeDao = initDatabase()
    val serverUrl = "http://213.183.51.109:8080/"
    configureRouting(memeDao, serverUrl)
}

fun initDatabase(): IMemeDao {
    val driverClassName = "org.h2.Driver"
    val jdbcUrl = "jdbc:h2:file:"
    val dbFilePath = "./build/db"

    return RealMemeDao(Database.connect(url = jdbcUrl + dbFilePath, driver = driverClassName)).apply { init() }
}