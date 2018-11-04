package com.negusoft.ktor.controller

import io.ktor.routing.Route
import io.ktor.routing.route
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions

/**
 * Setup the given controller: class annotated with @RouteController.
 * It reads the objects annotations to determine the path to be and the methods to set up.
 */
fun <T : Any> Route.setupController(controller: T) {
    val kclass = controller::class
    val controllerAnnotation = kclass.findAnnotation<RouteController>()
            ?: error("Controller must be annotated with @RouteController")
    setupFunctions(controllerAnnotation.path, controller, kclass)
}

/**
 * Setup the given controller: class annotated with @RouteController in the given path.
 * It reads the objects annotations to determine the path to be and the methods to set up.
 */
fun <T : Any> Route.setupController(path: String, controller: T) {
    val kclass = controller::class
    val controllerAnnotation = kclass.findAnnotation<RouteController>()
            ?: error("Controller must be annotated with @RouteController")
    route(path) { setupFunctions(controllerAnnotation.path, controller, kclass) }
}

private fun <T : Any> Route.setupFunctions(path: String, controller: Any, kclass: KClass<out T>) {
    if (path.isNotEmpty()) {
        route(path) { setupFunctions(controller, kclass) }
    } else {
        setupFunctions(controller, kclass)
    }
}

private fun <T : Any> Route.setupFunctions(controller: Any, kclass: KClass<out T>) {
    for (function in kclass.functions) {
        for (detector in FunctionDetectors.AllDetectors) {
            val functionMapping = detector.detect(controller, function, defaultParamDetectors, defaultResultDetectors)
            if (functionMapping != null) {
                functionMapping.invoke(this)
                break
            }
        }
    }
}

private val defaultParamDetectors = listOf(
        ParamDetectors.CallDetector,
        ParamDetectors.PathParamDetector,
        ParamDetectors.QueryParamDetector
)

private val defaultResultDetectors = listOf(
        ResultDetectors.UnitDetector,
        ResultDetectors.StringDetector,
        ResultDetectors.ObjectDetector
)