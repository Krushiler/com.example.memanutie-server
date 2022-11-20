package com.example

import com.example.database.dao.IMemeDao
import com.example.database.dao.RealMemeDao
import com.example.plugins.configureRouting
import com.example.util.getServerUrl
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.jetbrains.exposed.sql.Database

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
    val memeDao = initDatabase()
    val serverUrl = "http://127.0.0.1:8080/"
    configureRouting(memeDao, serverUrl)
}

fun initDatabase(): IMemeDao {
    val driverClassName = "org.h2.Driver"
    val jdbcUrl = "jdbc:h2:file:"
    val dbFilePath = "./build/db"

    return RealMemeDao(Database.connect(url = jdbcUrl + dbFilePath, driver = driverClassName)).apply { init() }
}