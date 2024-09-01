package net.hostunit.repository

import com.fasterxml.jackson.databind.ObjectMapper
import net.hostunit.classes.Address
import net.hostunit.objectMapper
import io.quarkus.mongodb.reactive.ReactiveMongoClient
import net.hostunit.logic.*

suspend fun Address.insert(db: ReactiveMongoClient, mapper: ObjectMapper = objectMapper): String? {
    return db.collection("address").insert(this, mapper)
}

suspend fun Address.replace(db: ReactiveMongoClient, mapper: ObjectMapper = objectMapper): Long {
    return db.collection("address").replace(this, mapper)
}

suspend fun findAddressByCode(db: ReactiveMongoClient, code: String, mapper: ObjectMapper = objectMapper): Address? {
    return db.collection("address").findBy("code", code, mapper)
}

suspend fun deleteAddressByCode(db: ReactiveMongoClient, code: String, mapper: ObjectMapper = objectMapper): Boolean? {
    return db.collection("address").deleteBy("code", code, mapper)
}