package net.hostunit.endpoint

import io.quarkus.mongodb.reactive.ReactiveMongoClient
import jakarta.annotation.security.PermitAll
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Response
import net.hostunit.classes.Address
import net.hostunit.logic.generateCode
import net.hostunit.logic.isInvalid
import net.hostunit.logic.isInvalidCode
import net.hostunit.repository.deleteAddressByCode
import net.hostunit.repository.findAddressByCode
import net.hostunit.repository.insert
import net.hostunit.repository.replace
import java.util.*

@Path("")
class AddressEndpoint {

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
    @RolesAllowed("edit")
    @POST
    @Path("/address")
    open suspend fun post(address: Address): Response {
        //Generate code for addresses without one
        if (address.code.isBlank()) address.generateCode(db, address.permanent) ?: return Response.serverError().build()

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

        //Address must have id
        if (address.id.length != 24) return Response.status(400).build()

        //Merge address
        address.replace(db)

        return Response.ok().build()
    }

    @RolesAllowed("edit")
    @DELETE
    @Path("/address/{code}")
    open suspend fun delete(@PathParam("code") code: String): Response {
        if (code.isInvalidCode()) return Response.status(400).build()
        deleteAddressByCode(db, code) ?: return Response.status(404).build()
        return Response.ok().build()
    }
}