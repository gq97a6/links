package hostunit.net.endpoint

import jakarta.annotation.security.DenyAll
import jakarta.annotation.security.PermitAll
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import org.eclipse.microprofile.jwt.JsonWebToken

@DenyAll
@Path("")
class RootEndpoint {

    @Inject
    var jwt: JsonWebToken? = null

    @PermitAll
    @GET
    @Path("/")
    open suspend fun index() = "root index page"

    @PermitAll
    @GET
    @Path("/{code}")
    open suspend fun code(@PathParam("code") code: String) = "root code page: $code"
}