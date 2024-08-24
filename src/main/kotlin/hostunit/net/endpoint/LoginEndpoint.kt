package hostunit.net.endpoint

import hostunit.net.classes.User
import hostunit.net.logic.generateToken
import hostunit.net.repository.findUserById
import hostunit.net.repository.findUserByName
import io.quarkus.mongodb.reactive.ReactiveMongoClient
import io.smallrye.jwt.auth.principal.JWTParser
import jakarta.annotation.security.PermitAll
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.NewCookie
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.mindrot.jbcrypt.BCrypt


@Path("")
class LoginEndpoint {

    @Inject
    lateinit var db: ReactiveMongoClient

    @Inject
    var jwtParser: JWTParser? = null

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
    @Consumes(MediaType.APPLICATION_JSON)
    open suspend fun login(data: LoginData? = null, @CookieParam("refreshToken") token: String?): Response {
        if (data != null) return loginWithCredentials(data)
        if (token != null) return loginWithToken(token)
        return Response.status(400).build()
    }

    private suspend fun loginWithCredentials(data: LoginData): Response {
        if (data.username.isNullOrBlank() || data.password.isNullOrBlank())
            return Response.status(400).build()

        val user = findUserByName(db, data.username) ?: return Response.status(404).build()
        if (!BCrypt.checkpw(data.password, user.pass)) return Response.status(401).build()

        return generateResponse(user).build()
    }

    private suspend fun loginWithToken(token: String): Response {
        //Validate and parse token
        if (token.isBlank()) return Response.status(400).build()
        val jwt = jwtParser?.parse(token) ?: return Response.status(400).build()

        //Extract and validate user id
        val id = jwt.getClaim<String>("id")
        if (id.isNullOrBlank()) return Response.status(400).build()

        //Fetch user from database
        val user = findUserById(db, id) ?: return Response.status(404).build()

        //Abort if token was issued before the user record was altered
        if (jwt.issuedAtTime < user.valid) return Response.status(401).build()

        return generateResponse(user).build()
    }

    private suspend fun generateResponse(user: User): Response.ResponseBuilder {
        val primaryToken = generateToken(user.name, user.id, user.roles, primaryTokenExpiry, domain)
        val refreshToken = generateToken(user.name, user.id, user.roles, refreshTokenExpiry, domain)

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
    }

}