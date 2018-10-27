package com.negusoft.ktor.controller.reflect

import com.negusoft.ktor.controller.Get
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.pipeline.PipelineInterceptor
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.method
import io.ktor.routing.route
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.findAnnotation

interface FunctionDetector {
    /**
     * Detects how a given function has to be mapped, looking at it's annotations.
     * It is called once during the route mapping, not for each request call.
     * Returns null if it can't handle the given function.
     */
    fun detect(owner: Any, func: KFunction<*>, paramMappings: List<ParamDetector>): FunctionMapping?
}

/*
 * Registers the function in the routing mechanism.
 * It is called once during the route mapping, not for each request call.
 */
typealias FunctionMapping = Routing.() -> Unit

fun functionMapping(path: String?, method: HttpMethod, call: Route.() -> Unit): FunctionMapping = {
    if (path != null) {
        route(path, method, call)
    } else {
        method(method, call)
    }
}

//class FunctionMapping2(
//        val path: String?,
//        val method: HttpMethod,
//        val call: Route.() -> Unit
//) {
//
//    fun map(routing: Routing) {
//        if (path != null) {
//            routing.route(path, method, call)
//        } else {
//            routing.method(method, call)
//        }
//    }
//}


/****************    Detector implementations    **************/

object FunctionDetectors {

    object GetDetector : FunctionDetector {
        override fun detect(owner: Any, func: KFunction<*>, paramDetectors: List<ParamDetector>): FunctionMapping? {
            val getAnnotation = func.findAnnotation<Get>() ?: return null

            val path = getAnnotation.path
            val paramMappings = func.getParameterMappings(owner, paramDetectors)
            val handler = func.toFunctionCall(paramMappings)
            return functionMapping(path, HttpMethod.Get) { handle(handler) }
        }
    }

    private fun <R> KFunction<R>.toFunctionCall(paramMappings: List<ParamMapping>): PipelineInterceptor<Unit, ApplicationCall> {
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

        private fun <R> KFunction<R>.getParameterMappings(owner: Any, paramDetectors: List<ParamDetector>): List<ParamMapping> {
            return parameters.mapIndexed<KParameter, ParamMapping> { i, param ->
                // The first param is the call receiver
                if (i == 0)
                    return@mapIndexed { owner }

                // Detect the parameters using the provided detectors
                for (detector in paramDetectors) {
                    detector.detect(param)?.let { return@mapIndexed it }
                }

                // When no detectors recognise the parameter
                error("Param not '${param.name}' supported")
            }
        }
}