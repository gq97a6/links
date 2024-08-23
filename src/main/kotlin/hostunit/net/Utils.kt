package hostunit.net

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.mindrot.jbcrypt.BCrypt
import java.time.Instant

val epochNow: Long
    get() = Instant.now().epochSecond

var objectMapper: ObjectMapper = ObjectMapper()
    .findAndRegisterModules()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

fun String.hash() = BCrypt.hashpw(this, BCrypt.gensalt())