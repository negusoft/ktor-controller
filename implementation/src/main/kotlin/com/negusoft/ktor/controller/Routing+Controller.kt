package com.negusoft.ktor.controller

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
        for (detector in FunctionDetectors.AllDetectors) {
            val functionMapping = detector.detect(controller, function, defaultParamDetectors, defaultResultDetectors)
            if (functionMapping != null) {
                functionMapping.invoke(this)
                break
            }
        }
        FunctionDetectors.GetDetector.detect(controller, function, defaultParamDetectors, defaultResultDetectors)?.invoke(this)
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