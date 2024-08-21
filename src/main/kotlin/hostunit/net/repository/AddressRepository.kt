package hostunit.net.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.mongodb.client.model.ReplaceOptions
import hostunit.net.classes.Address
import hostunit.net.collection
import hostunit.net.objectMapper
import io.quarkus.mongodb.FindOptions
import io.quarkus.mongodb.reactive.ReactiveMongoClient
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.bson.Document

suspend fun Address.insert(db: ReactiveMongoClient, mapper: ObjectMapper = objectMapper) {
    val document = Document.parse(mapper.writeValueAsString(this))
    db.collection().insertOne(document).awaitSuspending()
}

suspend fun Address.replace(db: ReactiveMongoClient, m: ObjectMapper = objectMapper) {
    val filterDocument = Document().apply { put("code", code) }
    val document = Document.parse(m.writeValueAsString(this))
    val options = ReplaceOptions().apply { upsert(false) }

    db.collection().replaceOne(filterDocument, document, options).awaitSuspending()
}

suspend fun findAddressByCode(
    db: ReactiveMongoClient,
    code: String,
    mapper: ObjectMapper = objectMapper
): Address? {
    val document = Document().apply { put("code", code) }
    val options = FindOptions().apply { limit(1) }

    return db.collection().find(document, options).toUni().awaitSuspending()?.let {
        mapper.readValue(it.toJson(), Address::class.java)
    }
}