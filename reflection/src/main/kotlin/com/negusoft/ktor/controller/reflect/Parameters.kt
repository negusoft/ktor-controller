package com.negusoft.ktor.controller.reflect

import com.negusoft.ktor.controller.PathParam
import com.negusoft.ktor.controller.QueryParam
import io.ktor.application.ApplicationCall
import kotlin.reflect.KParameter
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation

interface ParamDetector {
    /**
     * Extracts a ParamMapping from the KParamter, typically by checking its' annotations or the type.
     * It is called once during the route mapping, not for each request call.
     * Returns null if it didn't detect the supported param.
     */
    fun detect(param: KParameter): ParamMapping?
}

/*
 * Maps the call representing the request to a parameter.
 * It is called for every request.
 */
typealias ParamMapping = (ApplicationCall) -> Any?


/****************    Detector implementations    **************/

object ParamDetectors {

    object CallDetector : ParamDetector {
        override fun detect(param: KParameter): ParamMapping? {
            return if (param.type == ApplicationCall::class.createType()) {
                { it }
            } else {
                null
            }
        }
    }

    object PathParamDetector : ParamDetector {
        override fun detect(param: KParameter): ParamMapping? {
            val annotation = param.findAnnotation<PathParam>() ?: return null
            val name = annotation.name.takeIf { it.isNotEmpty() } ?: param.name ?: error("Unexpected error: Unnamed property $param")
            return { it.parameters[name] }
        }
    }

    object QueryParamDetector : ParamDetector {
        override fun detect(param: KParameter): ParamMapping? {
            val annotation = param.findAnnotation<QueryParam>() ?: return null
            val name = annotation.name.takeIf { it.isNotEmpty() } ?: param.name ?: error("Unexpected error: Unnamed property $param")
            return { it.request.queryParameters[name] }
        }
    }
}