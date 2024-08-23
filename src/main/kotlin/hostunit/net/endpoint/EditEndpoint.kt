package hostunit.net.endpoint

import jakarta.annotation.security.DenyAll
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import org.eclipse.microprofile.jwt.JsonWebToken

@DenyAll
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