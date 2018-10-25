package com.negusoft.ktor.controller.reflect

import com.negusoft.ktor.controller.Get
import com.negusoft.ktor.controller.RouteController
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.pipeline.PipelineInterceptor
import io.ktor.routing.*
import kotlin.reflect.*
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.createType
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
            val paramMappings = func.getParameterMappings(owner)
            val handler = func.toFunctionCall(owner, paramMappings)
            return FunctionMapping(path, HttpMethod.Get) { handle(handler) }
        }

        private fun <R> KFunction<R>.toFunctionCall(owner: Any, paramMappings: List<ParamMapping>): PipelineInterceptor<Unit, ApplicationCall> {
            return if (isSuspend) {
                { _ ->
                    val params = paramMappings.map { it(call) }.toTypedArray()
                    callSuspend(*params)
                }
            } else {
                { _ ->
                    val params = paramMappings.map { it(call) }.toTypedArray()
                    call(*params)
                }
            }
        }

        private fun <R> KFunction<R>.getParameterMappings(owner: Any): List<ParamMapping> {
            return parameters.mapIndexed<KParameter, ParamMapping> { i, param ->
                // The first param is the call receiver
                if (i == 0)
                    return@mapIndexed { owner }

                // Detect the parameters using the provided detectors
                for (detector in defaultParamDetectors) {
                    detector(param)?.let { return@mapIndexed it }
                }

                // When no detectors recognise the parameter
                error("Param not '${param.name}' supported")
            }
        }
    }
}

private val defaultParamDetectors = listOf<ParamDetector>(
        ParamDetectors.callDetector()
)