package com.negusoft.ktor.controller

import com.google.gson.Gson
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.HttpMethod
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class FunctionTypeTest {

    fun <R> withControllerTestApplication(test: TestApplicationEngine.() -> R): R
        = withControllerTestApplication(Controller(), test)

    @RouteController
    class Controller {
        @Get("/regular") fun regular(): String = "regular"
        @Get("/suspend") suspend fun suspend(): String {
            return withContext(Dispatchers.IO) {
                "suspend"
            }
        }
    }

    @Test
    fun shouldCallGet() {
        withControllerTestApplication {
            handleRequest(HttpMethod.Get, "/regular").apply {
                assertEquals(200, response.status()?.value)
                assertEquals(response.content, "regular")
            }
        }
    }

    @Test
    fun shouldCallPost() {
        withControllerTestApplication {
            handleRequest(HttpMethod.Get, "/suspend").apply {
                assertEquals(200, response.status()?.value)
                assertEquals(response.content, "suspend")
            }
        }
    }
}