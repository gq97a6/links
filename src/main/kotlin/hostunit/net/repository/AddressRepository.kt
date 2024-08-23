package hostunit.net.repository

import com.fasterxml.jackson.databind.ObjectMapper
import hostunit.net.classes.Address
import hostunit.net.logic.collection
import hostunit.net.logic.findBy
import hostunit.net.logic.insert
import hostunit.net.logic.replace
import hostunit.net.objectMapper
import io.quarkus.mongodb.reactive.ReactiveMongoClient

suspend fun Address.insert(db: ReactiveMongoClient, mapper: ObjectMapper = objectMapper): String? {
    return db.collection("address").insert(this, mapper)
}

suspend fun Address.replace(db: ReactiveMongoClient, mapper: ObjectMapper = objectMapper): Long {
    return db.collection("address").replace(this, mapper)
}

suspend fun findAddressByCode(db: ReactiveMongoClient, code: String, mapper: ObjectMapper = objectMapper): Address? {
    return db.collection("address").findBy("code", code, mapper)
}