package com.negusoft.ktor.controller.sample

import com.negusoft.ktor.controller.Get
import com.negusoft.ktor.controller.PathParam
import com.negusoft.ktor.controller.QueryParam
import com.negusoft.ktor.controller.RouteController
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get

/**
 * Defines the API endpoint implementations.
 */
@RouteController
class HelloController {

    @Get("/{name}")
    fun getHello(@PathParam name: String) : String {
        return "[HelloController] Hello $name!"
    }

    @Get("object/{name}")
    fun getHelloObject(@PathParam name: String) : HelloResponse {
        return HelloResponse("[HelloController] Hello $name!")
    }

    @Get("suspend/{name}")
    suspend fun getHelloWithCall(call: ApplicationCall, @PathParam name: String, @QueryParam query: String) {
        call.respondText("[HelloController] Hello $name! by $query")
    }

}

data class HelloResponse(val greeting: String)

/**
 * This extension should ideally be generated at compile time.
 */
fun Routing.setup(controller: HelloController) {
    get("/controller/{name}") {
        controller.getHelloWithCall(call, call.parameters["name"] ?: "", "Negusoft")
    }
}