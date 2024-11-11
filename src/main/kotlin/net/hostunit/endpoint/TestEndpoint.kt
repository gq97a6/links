package net.hostunit.endpoint

import jakarta.annotation.security.PermitAll
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType

@Path("/api")
class TestEndpoint {
    @PermitAll
    @GET
    @Path("/test")
    @Consumes(MediaType.TEXT_PLAIN)
    open suspend fun test() = "ok"
}