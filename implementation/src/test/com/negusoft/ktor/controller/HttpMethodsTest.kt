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
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class HttpMethodsTest {

    fun <R> withControllerTestApplication(test: TestApplicationEngine.() -> R): R
        = withControllerTestApplication(Controller(), test)

    @RouteController
    class Controller {
        @Get("/method") fun get(): String = "get"
        @Post("/method") fun post(): String = "post"
        @Put("/method") fun put(): String = "put"
        @Patch("/method") fun patch(): String = "patch"
        @Delete("/method") fun delete(): String = "delete"
        @Head("/method") fun head(): String = "head"
        @Options("/method") fun options(): String = "options"
    }

    @Test
    fun shouldCallGet() {
        withControllerTestApplication {
            handleRequest(HttpMethod.Get, "/method").apply {
                assertEquals(200, response.status()?.value)
                assertEquals(response.content, "get")
            }
        }
    }

    @Test
    fun shouldCallPost() {
        withControllerTestApplication {
            handleRequest(HttpMethod.Post, "/method").apply {
                assertEquals(200, response.status()?.value)
                assertEquals(response.content, "post")
            }
        }
    }

    @Test
    fun shouldCallPut() {
        withControllerTestApplication {
            handleRequest(HttpMethod.Put, "/method").apply {
                assertEquals(200, response.status()?.value)
                assertEquals(response.content, "put")
            }
        }
    }

    @Test
    fun shouldCallPatch() {
        withControllerTestApplication {
            handleRequest(HttpMethod.Patch, "/method").apply {
                assertEquals(200, response.status()?.value)
                assertEquals(response.content, "patch")
            }
        }
    }

    @Test
    fun shouldCallDelete() {
        withControllerTestApplication {
            handleRequest(HttpMethod.Delete, "/method").apply {
                assertEquals(200, response.status()?.value)
                assertEquals(response.content, "delete")
            }
        }
    }

    @Test
    fun shouldCallHead() {
        withControllerTestApplication {
            handleRequest(HttpMethod.Head, "/method").apply {
                assertEquals(200, response.status()?.value)
                assertEquals(response.content, "head")
            }
        }
    }

    @Test
    fun shouldCallOptions() {
        withControllerTestApplication {
            handleRequest(HttpMethod.Options, "/method").apply {
                assertEquals(200, response.status()?.value)
                assertEquals(response.content, "options")
            }
        }
    }
}