package hostunit.net.test

import hostunit.net.logic.refreshToken
import io.quarkus.security.identity.CurrentIdentityAssociation
import jakarta.annotation.security.DenyAll
import jakarta.annotation.security.PermitAll
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Response

@DenyAll
@Path("")
class Endpoint {

    @Inject
    var cia: CurrentIdentityAssociation? = null

    @PermitAll
    @GET
    @Path("/test")
    open suspend fun test(): Response {
        return Response.ok().refreshToken(cia).build()
    }
}