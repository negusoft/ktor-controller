package com.negusoft.ktor.controller.sample

import io.ktor.application.*
import io.ktor.features.*
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
    install(CallLogging)
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/demo") {
            throw Exception("asdf")
        }
    }
}