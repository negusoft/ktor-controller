package com.negusoft.ktor.controller.reflect

import com.negusoft.ktor.controller.Get
import com.negusoft.ktor.controller.RouteController
import io.ktor.application.call
import io.ktor.routing.Routing
import io.ktor.routing.get
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions

/**
 * Setup the given, annotated with @RouteController.
 * It reads the objects annotations to determine the path to be and the methods to set up.
 * 
 */
fun <T : Any> Routing.setupController(controller: T) {
    val kclass = controller::class
    val controllerAnnotation = kclass.findAnnotation<RouteController>()
    val path = controllerAnnotation?.path ?: ""

    for (function in kclass.functions) {
        val getAnnotation = function.findAnnotation<Get>() ?: continue
        val path = getAnnotation.path
        get(path) {
            function.callSuspend(controller, call)
        }
    }
}