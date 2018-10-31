package com.negusoft.ktor.controller.sample

import com.negusoft.ktor.controller.reflect.setupController
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.html.*
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.html.*

/**
 * Main method for the whole app. Configured in 'build.gradle' with the 'mainClassName' property
 */
fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}

/**
 * Main app module. Referenced from 'resources/application.conf'
 */
fun Application.main() {
    install(DefaultHeaders)
    install(StatusPages) {
        exception<Throwable> { e ->
            call.respondText(e.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
        }
    }
    install(ContentNegotiation) {
        gson()
    }
    install(CallLogging)
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/hello/{name}") {
            val name = call.parameters["name"] ?: "world"
            call.respondText("Hello $name!")
        }
        setupController(HelloController())
    }
}