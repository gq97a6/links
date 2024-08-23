package hostunit.net.logic

import hostunit.net.epochNow
import io.quarkus.security.identity.CurrentIdentityAssociation
import io.smallrye.jwt.build.Jwt
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.ws.rs.core.Response.ResponseBuilder
import org.eclipse.microprofile.jwt.JsonWebToken
import java.time.Duration

//Generate new JWT token
fun generateToken(
    username: String,
    roles: List<String>,
    expiresIn: Long,
    domain: String
): String = Jwt
    .issuer(domain)
    .upn(username) //required to be valid
    .expiresIn(Duration.ofSeconds(expiresIn))
    .groups(HashSet(roles))
    .sign()
//.innerSign()
//.encrypt()

//Issue new token if current one is in grace period
suspend fun ResponseBuilder.refreshToken(cia: CurrentIdentityAssociation?): ResponseBuilder {
    val token = cia?.deferredIdentity?.awaitSuspending()?.principal as JsonWebToken?
    println(epochNow - (token?.issuedAtTime ?: 0))

//    val gracePeriodEnd = expirationTime.plusSeconds(GRACE_PERIOD_SECONDS)
//
//    return if (currentTime.isAfter(expirationTime) && currentTime.isBefore(gracePeriodEnd)) {
//        // Token is in grace period, generate a new token
//        val newToken = generateNewToken(token)
//        this.addCookie("token", newToken)
//    } else {
//        // Token is not in grace period, return the ResponseBuilder as is
//        this
//    }

    return this
}

//iss (Issuer): Identifies the principal that issued the JWT (e.g., a web server).
//sub (Subject): Identifies the principal that is the subject of the JWT (the user).
//aud (Audience): Identifies the recipients that the JWT is intended for.
//exp (Expiration Time): Identifies the expiration time on or after which the JWT must not be accepted for processing.
//nbf (Not Before): Identifies the time before which the JWT must not be accepted for processing.
//iat (Issued At): Identifies the time at which the JWT was issued.
//jti (JWT ID): Provides a unique identifier for the JWT.
