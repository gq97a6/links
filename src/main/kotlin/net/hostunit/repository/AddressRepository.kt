package net.hostunit.repository

import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.mongodb.reactive.ReactiveMongoDatabase
import net.hostunit.classes.Address
import net.hostunit.logic.deleteBy
import net.hostunit.logic.findBy
import net.hostunit.logic.insert
import net.hostunit.logic.replace
import net.hostunit.objectMapper

suspend fun Address.insert(db: ReactiveMongoDatabase, mapper: ObjectMapper = objectMapper): String? {
    return db.getCollection("address").insert(this, mapper)
}

suspend fun Address.replace(db: ReactiveMongoDatabase, mapper: ObjectMapper = objectMapper): Long {
    return db.getCollection("address").replace(this, mapper)
}

suspend fun findAddressByCode(db: ReactiveMongoDatabase, code: String, mapper: ObjectMapper = objectMapper): Address? {
    return db.getCollection("address").findBy("code", code, mapper)
}

suspend fun deleteAddressByCode(db: ReactiveMongoDatabase, code: String): Boolean? {
    return db.getCollection("address").deleteBy("code", code)
}