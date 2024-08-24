package hostunit.net.endpoint

import jakarta.annotation.security.PermitAll
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam

@Path("")
class RootEndpoint {

    @PermitAll
    @GET
    @Path("/")
    open suspend fun index() = "root index page"

    @PermitAll
    @GET
    @Path("/{code}")
    open suspend fun code(@PathParam("code") code: String) = "root code page: $code"
}