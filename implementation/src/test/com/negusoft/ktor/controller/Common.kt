package com.negusoft.ktor.controller

import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.routing.routing
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withTestApplication

/**
 * Shortcut for the common test application setup with the given controller.
 */
fun <C : Any, R> withControllerTestApplication(controller: C, test: TestApplicationEngine.() -> R): R
        = withTestApplication({
    install(ContentNegotiation) {
        gson()
    }
    routing {
        setupController(controller)
    }
}, test)