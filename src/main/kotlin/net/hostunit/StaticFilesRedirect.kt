package net.hostunit

import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.container.PreMatching
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.UriBuilder
import jakarta.ws.rs.ext.Provider

@Provider
@PreMatching
class StaticFilesRedirect : ContainerRequestFilter {
    override fun filter(context: ContainerRequestContext) {
        println(context.uriInfo.requestUri.path)
        if (context.uriInfo.requestUri.path.startsWith("/api")) return
        println(1)
        if (context.uriInfo.requestUri.path.count { it == '/' } <= 1) return
        println(2)

        val requestUri = context.uriInfo.requestUri
        val filename = requestUri.path.substringAfterLast("/")

        if (isStaticFile(filename)) {
            println(3)
            val newUri = UriBuilder.fromUri(requestUri).replacePath("/$filename").build()
            context.abortWith(Response.seeOther(newUri).build())
        }
    }

    private fun isStaticFile(path: String): Boolean {
        return listOf(
            ".wasm",
            ".xml",
            ".js",
            ".map",
            ".html",
            ".css",
        ).any { path.endsWith(it) }
    }
}