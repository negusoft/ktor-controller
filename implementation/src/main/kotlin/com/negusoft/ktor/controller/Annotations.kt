package com.negusoft.ktor.controller

/**
 * API marked with this annotation is experimental and is not guaranteed to be stable.
 */
@Experimental(level = Experimental.Level.WARNING)
annotation class KtorExperimentalControllersAPI

@KtorExperimentalControllersAPI
@Target(AnnotationTarget.CLASS)
annotation class RouteController(val path: String = "")

// Methods
@Target(AnnotationTarget.FUNCTION)
annotation class Get(val path: String = "")
@Target(AnnotationTarget.FUNCTION)
annotation class Post(val path: String = "")
@Target(AnnotationTarget.FUNCTION)
annotation class Put(val path: String = "")
@Target(AnnotationTarget.FUNCTION)
annotation class Patch(val path: String = "")
@Target(AnnotationTarget.FUNCTION)
annotation class Delete(val path: String = "")
@Target(AnnotationTarget.FUNCTION)
annotation class Head(val path: String = "")
@Target(AnnotationTarget.FUNCTION)
annotation class Options(val path: String = "")

// Parameters
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class PathParam(val name: String = "")
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class QueryParam(val name: String = "")