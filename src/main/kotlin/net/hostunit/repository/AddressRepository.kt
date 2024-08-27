package net.hostunit.repository

import com.fasterxml.jackson.databind.ObjectMapper
import net.hostunit.classes.Address
import net.hostunit.logic.collection
import net.hostunit.logic.findBy
import net.hostunit.logic.insert
import net.hostunit.logic.replace
import net.hostunit.objectMapper
import io.quarkus.mongodb.reactive.ReactiveMongoClient

suspend fun Address.insert(db: ReactiveMongoClient, mapper: ObjectMapper = net.hostunit.objectMapper): String? {
    return db.collection("address").insert(this, mapper)
}

suspend fun Address.replace(db: ReactiveMongoClient, mapper: ObjectMapper = net.hostunit.objectMapper): Long {
    return db.collection("address").replace(this, mapper)
}

suspend fun findAddressByCode(db: ReactiveMongoClient, code: String, mapper: ObjectMapper = objectMapper): Address? {
    return db.collection("address").findBy("code", code, mapper)
}