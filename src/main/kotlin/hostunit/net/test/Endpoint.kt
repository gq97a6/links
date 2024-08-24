package hostunit.net.test

import io.quarkus.mongodb.reactive.ReactiveMongoClient
import jakarta.annotation.security.PermitAll
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import org.eclipse.microprofile.jwt.JsonWebToken

@Path("")
class Endpoint {

    @Inject
    var token: JsonWebToken? = null

    @Inject
    lateinit var db: ReactiveMongoClient

    @RolesAllowed("edit")
    @GET
    @Path("/test1")
    open suspend fun userEndpoint(): String {
        return "ok"
    }

    @RolesAllowed("admin")
    @GET
    @Path("/test2")
    open suspend fun adminEndpoint(): String {
        return "ok"
    }

    @PermitAll
    @GET
    @Path("/test3")
    open suspend fun allEndpoint(): String {
        return "ok"
    }
}