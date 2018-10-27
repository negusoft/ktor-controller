package com.negusoft.ktor.controller.reflect

import com.negusoft.ktor.controller.RouteController
import io.ktor.routing.Routing
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions

/**
 * Setup the given controller: class annotated with @RouteController.
 * It reads the objects annotations to determine the path to be and the methods to set up.
 */
fun <T : Any> Routing.setupController(controller: T) {
    val kclass = controller::class
    val controllerAnnotation = kclass.findAnnotation<RouteController>()
    val path = controllerAnnotation?.path ?: ""

    // TODO apply path
    setupFunctions(controller, kclass)
}

private fun <T : Any> Routing.setupFunctions(controller: Any, kclass: KClass<out T>) {
    for (function in kclass.functions) {
        FunctionDetectors.GetDetector.detect(controller, function, defaultParamDetectors)?.invoke(this)
    }
}

private val defaultParamDetectors = listOf<ParamDetector>(
        ParamDetectors.CallDetector
)