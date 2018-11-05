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

class PathTest {

    fun <R> withPathTestApplication(test: TestApplicationEngine.() -> R): R
            = withTestApplication({
        install(Controllers)
        routing {
            setupController(ControllerNoPath())
            setupController(ControllerWithPath())
            setupController("setup", ControllerNoPath())
            setupController("setup", ControllerWithPath())
        }
    }, test)

    @RouteController
    class ControllerNoPath {
        @Get("/method") fun method(): String = "nopath"
    }

    @RouteController("controller")
    class ControllerWithPath {
        @Get("/method") fun method(): String = "withpath"
    }

    @Test
    fun shouldCallSimple() {
        withPathTestApplication {
            handleRequest(HttpMethod.Get, "/method").apply {
                assertEquals(200, response.status()?.value)
                assertEquals(response.content, "nopath")
            }
        }
    }

    @Test
    fun shouldCallWithControllerPath() {
        withPathTestApplication {
            handleRequest(HttpMethod.Get, "/controller/method").apply {
                assertEquals(200, response.status()?.value)
                assertEquals(response.content, "withpath")
            }
        }
    }

    @Test
    fun shouldCallWithSetupPath() {
        withPathTestApplication {
            handleRequest(HttpMethod.Get, "/setup/method").apply {
                assertEquals(200, response.status()?.value)
                assertEquals(response.content, "nopath")
            }
        }
    }

    @Test
    fun shouldCallWithDoublePath() {
        withPathTestApplication {
            handleRequest(HttpMethod.Get, "/setup/controller/method").apply {
                assertEquals(200, response.status()?.value)
                assertEquals(response.content, "withpath")
            }
        }
    }
}