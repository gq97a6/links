package net.hostunit.logic

import io.smallrye.jwt.build.Jwt
import java.time.Duration

//Generate new JWT token
fun generateToken(
    username: String,
    userId: String,
    roles: List<String>,
    expireIn: Long,
    domain: String
): String = Jwt
    .issuer(domain)
    .upn(username) //required to be valid
    .expiresIn(Duration.ofSeconds(expireIn))
    .groups(HashSet(roles))
    .claim("id", userId)
    .innerSign()
    .encrypt()