package hostunit.net.endpoint

import hostunit.net.classes.Address
import hostunit.net.logic.generateCode
import hostunit.net.logic.isInvalid
import hostunit.net.logic.isInvalidCode
import hostunit.net.repository.findAddressByCode
import hostunit.net.repository.insert
import hostunit.net.repository.replace
import io.quarkus.mongodb.reactive.ReactiveMongoClient
import jakarta.annotation.security.DenyAll
import jakarta.annotation.security.PermitAll
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.Dependent
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.jwt.JsonWebToken
import java.util.*

@DenyAll
@Path("")
class AddressEndpoint {

    @Inject
    var jwt: JsonWebToken? = null

    @Inject
    lateinit var db: ReactiveMongoClient

    //Get existing address
    @PermitAll
    @GET
    @Path("/address/{code}")
    open suspend fun get(@PathParam("code") code: String): Response {
        if (code.isInvalidCode()) return Response.status(400).build()
        val address = findAddressByCode(db, code) ?: return Response.status(404).build()
        return Response.ok(address).build()
    }

    //Create new address
    //@RolesAllowed("edit")
    @PermitAll
    @POST
    @Path("/address")
    open suspend fun post(address: Address): Response {
        //Generate code for addresses without one
        if (address.code.isBlank()) address.generateCode(db, address.temporary) ?: return Response.serverError().build()

        //Force uppercase and check if address with that code already exists
        else {
            address.code = address.code.uppercase(Locale.getDefault())
            if (findAddressByCode(db, address.code) != null) return Response.status(400).build()
        }

        //Validate address
        if (address.isInvalid()) return Response.status(418).build()

        //Insert address
        address.insert(db)

        //Return generated code
        return Response.ok(address.code).build()
    }

    //Edit existing address
    @RolesAllowed("edit")
    @PUT
    @Path("/address/{code}")
    open suspend fun put(address: Address, @PathParam("code") code: String): Response {
        //Check if codes match
        if (address.code != code) return Response.status(400).build()

        //Validate address
        if (address.isInvalid()) return Response.status(418).build()

        //Check if address with that code exists
        if (findAddressByCode(db, address.code) == null) return Response.status(400).build()

        //Merge address
        address.replace(db)

        return Response.ok().build()
    }
}