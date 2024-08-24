package hostunit.net.endpoint

import jakarta.annotation.security.PermitAll
import jakarta.annotation.security.RolesAllowed
import jakarta.ws.rs.GET
import jakarta.ws.rs.HeaderParam
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam

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
    open suspend fun loginIndexPage() = "login page"

    @RolesAllowed("edit")
    @GET
    @Path("/edit")
    open suspend fun editIndexPage() = "edit index page"

    @RolesAllowed("edit")
    @GET
    @Path("/edit/{code}")
    open suspend fun editCodePage(@PathParam("code") code: String) = "edit code page: $code"
}