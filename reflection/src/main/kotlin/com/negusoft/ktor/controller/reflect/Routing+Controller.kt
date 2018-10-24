package com.negusoft.ktor.controller.reflect

import com.negusoft.ktor.controller.Get
import com.negusoft.ktor.controller.RouteController
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.pipeline.PipelineInterceptor
import io.ktor.routing.*
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend
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
        FunctionMapping.fromFunction(controller, function)?.map(this)
//        get(path) {
//            function.callSuspend(controller, call)
//        }
    }
}

private class FunctionMapping(
        val path: String?,
        val method: HttpMethod,
        val call: Route.() -> Unit
) {

    fun map(routing: Routing) {
        if (path != null) {
            routing.route(path, method, call)
        } else {
            routing.method(method, call)
        }
    }

    companion object {
        /**
         * Get a method mapping for the given function. The function will return null if not annotated with one of the
         * following: @Get, @Post...
         *
         */
        fun <R> fromFunction(owner: Any, func: KFunction<R>): FunctionMapping? {
            val getAnnotation = func.findAnnotation<Get>() ?: return null
            val path = getAnnotation.path
            val handler = func.toFunctionCall(owner)
            return FunctionMapping(path, HttpMethod.Get) { handle(handler) }
        }

        private fun <R> KFunction<R>.toFunctionCall(owner: Any): PipelineInterceptor<Unit, ApplicationCall> {
            return if (isSuspend) {
                { callSuspend(owner, call) }
            } else {
                { call(owner, call) }
            }
        }
    }
}