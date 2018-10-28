package com.negusoft.ktor.controller.reflect

import com.negusoft.ktor.controller.*
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.pipeline.PipelineInterceptor
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.method
import io.ktor.routing.route
import kotlin.reflect.*
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.findAnnotation

interface FunctionDetector {
    /**
     * Detects how a given function has to be mapped, looking at it's annotations.
     * It is called once during the route mapping, not for each request call.
     * Returns null if it can't handle the given function.
     */
    fun detect(owner: Any, func: KFunction<*>, paramDetectors: List<ParamDetector>): FunctionMapping?
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

/****************    Detector implementations    **************/

object FunctionDetectors {
    val GetDetector = functionDetector<Get>(HttpMethod.Get) { it.path}
    val PostDetector = functionDetector<Post>(HttpMethod.Post) { it.path}
    val PutDetector = functionDetector<Put>(HttpMethod.Put) { it.path}
    val PatchDetector = functionDetector<Patch>(HttpMethod.Patch) { it.path}
    val DeleteDetector = functionDetector<Delete>(HttpMethod.Delete) { it.path}
    val HeadDetector = functionDetector<Head>(HttpMethod.Head) { it.path}
    val OptionsDetector = functionDetector<Options>(HttpMethod.Options) { it.path}

    val AllDetectors = listOf(
            GetDetector, PostDetector, PutDetector, PatchDetector,
            DeleteDetector, HeadDetector, OptionsDetector
    )
}

private inline fun <reified T : Annotation> functionDetector(method: HttpMethod, crossinline path: (T) -> String?) : FunctionDetector
    = object : FunctionDetector {
        override fun detect(owner: Any, func: KFunction<*>, paramDetectors: List<ParamDetector>): FunctionMapping? {
            val annotation = func.findAnnotation<T>() ?: return null
            val thePath = path(annotation)

            val paramMappings = func.getParameterMappings(owner, paramDetectors)
            val handler = func.toFunctionCall(paramMappings)
            return functionMapping(thePath, method) { handle(handler) }
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