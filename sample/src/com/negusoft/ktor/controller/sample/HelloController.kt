package com.negusoft.ktor.controller.sample

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get

/**
 * Defines the API endpoint implementations.
 */
class HelloController {

    fun getHello(name: String): String {
        return "Hello $name!"
    }

    suspend fun getHelloWithCall(call: ApplicationCall) {
        val name = call.parameters["name"] ?: "world"
        call.respondText("Hello $name!")
    }

}

/**
 * This extension should ideally be generated at compile time.
 */
fun Routing.setup(controller: HelloController) {
    get("/controller/{name}") {
        controller.getHelloWithCall(call)
    }
}