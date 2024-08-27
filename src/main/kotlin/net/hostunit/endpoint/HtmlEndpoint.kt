package net.hostunit.endpoint

import jakarta.annotation.security.PermitAll
import jakarta.annotation.security.RolesAllowed
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType

@Path("")
class HtmlEndpoint {

    @PermitAll
    @GET
    @Path("/")
    open suspend fun indexPage() = "index page"

    @PermitAll
    @GET
    @Path("/{code}")
    open suspend fun codePage(@PathParam("code") code: String) = "code page: $code"

    @PermitAll
    @GET
    @Path("/login/{code}")
    open suspend fun loginIndexPage(@PathParam("code") code: String) = loginIndexPage()

    @PermitAll
    @GET
    @Path("/login")
    @Produces(MediaType.TEXT_HTML)
    open suspend fun loginIndexPage() = getIndexPageContent()

    @RolesAllowed("edit")
    @GET
    @Path("/edit")
    @Produces(MediaType.TEXT_HTML)
    open suspend fun editIndexPage() = getIndexPageContent()

    @RolesAllowed("edit")
    @GET
    @Path("/edit/{code}")
    open suspend fun editCodePage(@PathParam("code") code: String) = "edit code page: $code"

    private fun getIndexPageContent(): String {
        val resourcePath = "META-INF/resources/index.html"
        val inputStream = javaClass.classLoader.getResourceAsStream(resourcePath)
        return inputStream?.bufferedReader()?.use { it.readText() }
            ?: throw IllegalStateException("Resource not found: $resourcePath")
    }
}