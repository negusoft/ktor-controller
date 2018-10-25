package com.negusoft.ktor.controller.reflect

import io.ktor.application.ApplicationCall
import kotlin.reflect.KParameter
import kotlin.reflect.full.createType

/**
 * Extracts a ParamMapping from the KParamter, typically by checking its' annotations or the type.
 * It is called once during the route mapping, not for each request call.
 * Returns null if it didn't detect the supported param.
 */
typealias ParamDetector = (KParameter) -> ParamMapping?

/*
 * Maps the call representing the request to a parameter.
 * It is called for every request.
 */
typealias ParamMapping = (ApplicationCall) -> Any?


/****************    Detector implementations    **************/

object ParamDetectors {

    fun callDetector() : ParamDetector = { param ->
        if (param.type == ApplicationCall::class.createType()) {
            { it }
        } else {
            null
        }
    }
}