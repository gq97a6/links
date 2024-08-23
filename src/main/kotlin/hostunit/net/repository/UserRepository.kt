package hostunit.net.repository

import com.fasterxml.jackson.databind.ObjectMapper
import hostunit.net.classes.Address
import hostunit.net.classes.User
import hostunit.net.logic.*
import hostunit.net.objectMapper
import io.quarkus.mongodb.reactive.ReactiveMongoClient

suspend fun User.insert(db: ReactiveMongoClient, mapper: ObjectMapper = objectMapper): String? {
    return db.collection("user").insert(this, mapper)
}

suspend fun User.replace(db: ReactiveMongoClient, mapper: ObjectMapper = objectMapper): Long {
    return db.collection("user").replace(this, mapper)
}

suspend fun findUserByName(db: ReactiveMongoClient, name: String, mapper: ObjectMapper = objectMapper): Address? {
    return db.collection("user").findBy("name", name, mapper)
}

suspend fun findUserById(db: ReactiveMongoClient, id: String, mapper: ObjectMapper = objectMapper): Address? {
    return db.collection("user").findBy("_id", id, mapper)
}