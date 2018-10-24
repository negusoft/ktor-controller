package com.negusoft.ktor.controller

@Target(AnnotationTarget.CLASS)
annotation class RouteController(val path: String = "")

// Methods
@Target(AnnotationTarget.FUNCTION)
annotation class Get(val path: String = "")