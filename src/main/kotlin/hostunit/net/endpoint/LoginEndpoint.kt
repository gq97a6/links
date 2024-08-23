package hostunit.net.endpoint

import hostunit.net.logic.generateToken
import jakarta.annotation.security.DenyAll
import jakarta.annotation.security.PermitAll
import jakarta.ws.rs.*
import jakarta.ws.rs.core.NewCookie
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.config.inject.ConfigProperty

@DenyAll
@Path("")
class LoginEndpoint {

    @ConfigProperty(name = "server.domain")
    var domain = "localhost"

    @ConfigProperty(name = "jwt.token.primary.expire")
    var primaryTokenExpiry = 0L

    @ConfigProperty(name = "jwt.token.refresh.expire")
    var refreshTokenExpiry = 0L

    @ConfigProperty(name = "mp.jwt.token.cookie")
    var tokenCookieName = "token"

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
        //TODO: Check if allowed

        val primaryToken = generateToken("user", listOf("edit"), primaryTokenExpiry, domain)
        val refreshToken = generateToken("user", listOf("edit"), refreshTokenExpiry, domain)

        val primaryTokenCookie = NewCookie.Builder(tokenCookieName)
            .value(primaryToken)
            .domain(".$domain")
            .path("/")
            .maxAge(primaryTokenExpiry.toInt())
            .secure(false)
            .httpOnly(false)
            .build()

        val refreshTokenCookie = NewCookie.Builder("refreshToken")
            .value(refreshToken)
            .domain(".$domain")
            .path("/")
            .maxAge(refreshTokenExpiry.toInt())
            .secure(false)
            .httpOnly(false)
            .build()

        return Response
            .ok()
            .cookie(primaryTokenCookie)
            .cookie(refreshTokenCookie)
            .build()
    }
}