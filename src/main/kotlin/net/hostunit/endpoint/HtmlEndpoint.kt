package net.hostunit.endpoint

import jakarta.annotation.security.PermitAll
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("")
class HtmlEndpoint {

    @PermitAll
    @GET
    @Path("{path:.*}")
    @Produces(MediaType.TEXT_HTML)
    open suspend fun catchThemAll() = getIndexPageContent()

    private fun getIndexPageContent(): String {
        val resourcePath = "META-INF/resources/index.html"
        val inputStream = javaClass.classLoader.getResourceAsStream(resourcePath)
        return inputStream?.bufferedReader()?.use { it.readText() }
            ?: throw IllegalStateException("Resource not found: $resourcePath")
    }

    @PermitAll
    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_HTML)
    open suspend fun test() = "ok"
}