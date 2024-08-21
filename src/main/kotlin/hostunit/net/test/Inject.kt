package hostunit.net.test

import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty

@ApplicationScoped
object Test {
    @ConfigProperty(name = "server.origin")
    var origin: String? = null
}