package hostunit.net.endpoint

import hostunit.net.logic.generateToken
import jakarta.annotation.security.DenyAll
import jakarta.annotation.security.PermitAll
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.NewCookie
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.jwt.JsonWebToken

@DenyAll
@Path("")
class LoginEndpoint {

    @ConfigProperty(name = "server.domain")
    var domain = "localhost"

    @Inject
    var jwt: JsonWebToken? = null

    data class LoginData(
        val username: String?,
        val password: String?
    )

    @PermitAll
    @GET
    @Path("/login/{code}")
    open suspend fun indexRedirect(@PathParam("code") code: String) = index(code)

    @PermitAll
    @GET
    @Path("/login")
    open suspend fun index(@HeaderParam("code") code: String?) = "login: $code"

    @PermitAll
    @POST
    @Path("/login")
    open suspend fun login(): Response {
        //suspend fun login(data: LoginData): Response {
        //if (data.username.isNullOrBlank() || data.password.isNullOrBlank())
        //return Response.status(Response.Status.BAD_REQUEST).build()
        //TODO: Check if allowed

        val token = generateToken("user", listOf("edit"), 100L, domain)
        val tokenCookie = NewCookie.Builder("token")
            .value(token)
            .domain(".$domain")
            .path("/")
            .maxAge(600)
            .secure(false)
            .httpOnly(false)
            .build()

        return Response.ok().cookie(tokenCookie).entity(token).build()
    }
}