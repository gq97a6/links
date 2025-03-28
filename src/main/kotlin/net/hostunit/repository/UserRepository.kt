package net.hostunit.repository

import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.mongodb.reactive.ReactiveMongoDatabase
import net.hostunit.classes.User
import net.hostunit.logic.findBy
import net.hostunit.logic.insert
import net.hostunit.logic.replace
import net.hostunit.objectMapper
import org.bson.types.ObjectId

suspend fun User.insert(db: ReactiveMongoDatabase, mapper: ObjectMapper = objectMapper): String? {
    return db.getCollection("user").insert(this, mapper)
}

suspend fun User.replace(db: ReactiveMongoDatabase, mapper: ObjectMapper = objectMapper): Long {
    return db.getCollection("user").replace(this, mapper)
}

suspend fun findUserByName(db: ReactiveMongoDatabase, name: String, mapper: ObjectMapper = objectMapper): User? {
    return db.getCollection("user").findBy("name", name, mapper)
}

suspend fun findUserById(db: ReactiveMongoDatabase, id: String, mapper: ObjectMapper = objectMapper): User? {
    return db.getCollection("user").findBy("_id", ObjectId(id), mapper)
}