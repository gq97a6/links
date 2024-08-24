package hostunit.net.endpoint

import jakarta.annotation.security.RolesAllowed
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam

@Path("")
class EditEndpoint {

    @RolesAllowed("edit")
    @GET
    @Path("/edit")
    open suspend fun index() = "edit index page"

    @RolesAllowed("edit")
    @GET
    @Path("/edit/{code}")
    open suspend fun code(@PathParam("code") code: String) = "edit code page: $code"
}