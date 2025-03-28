package net.hostunit.logic

import com.fasterxml.jackson.databind.ObjectMapper
import com.mongodb.client.model.Aggregates.limit
import com.mongodb.client.model.DeleteOptions
import com.mongodb.client.model.ReplaceOptions
import io.quarkus.mongodb.FindOptions
import io.quarkus.mongodb.reactive.ReactiveMongoClient
import io.quarkus.mongodb.reactive.ReactiveMongoCollection
import io.quarkus.mongodb.reactive.ReactiveMongoDatabase
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import jakarta.inject.Inject
import net.hostunit.epochNow
import net.hostunit.objectMapper
import org.bson.Document
import org.bson.types.ObjectId
import org.eclipse.microprofile.config.inject.ConfigProperty

@ApplicationScoped
class MongoDatabaseProducer {

    @Inject
    lateinit var mongoClient: ReactiveMongoClient

    @ConfigProperty(name = "quarkus.mongodb.database")
    lateinit var databaseName: String

    @Produces
    fun produceReactiveMongoDatabase(): ReactiveMongoDatabase {
        return mongoClient.getDatabase(databaseName)
    }
}

suspend inline fun <reified T> ReactiveMongoCollection<Document>.findBy(
    field: String,
    value: Any,
    mapper: ObjectMapper = objectMapper
): T? {
    val document = Document().apply { put(field, value) }
    val options = FindOptions().apply { limit(1) }

    return this.find(document, options).toUni().awaitSuspending()?.let {
        it["id"] = it["_id"].toString()
        mapper.readValue(it.toJson(), T::class.java)
    }
}

suspend fun ReactiveMongoCollection<Document>.insert(obj: Any, mapper: ObjectMapper = objectMapper): String? {
    val document = Document.parse(mapper.writeValueAsString(obj))
    document.remove("id")
    document["lastChange"] = epochNow
    return this.insertOne(document).awaitSuspending().insertedId?.asObjectId()?.value?.toString()
}

suspend fun ReactiveMongoCollection<Document>.replace(obj: Any, mapper: ObjectMapper = objectMapper): Long {
    val document = Document.parse(mapper.writeValueAsString(obj))
    document["lastChange"] = epochNow
    val id = document.remove("id") ?: return 0L

    val filterDocument = Document().apply { put("_id", ObjectId(id as String)) }
    val replaceOptions = ReplaceOptions().apply { upsert(false) }

    return this.replaceOne(filterDocument, document, replaceOptions).awaitSuspending().modifiedCount
}

suspend inline fun ReactiveMongoCollection<Document>.deleteBy(
    field: String,
    value: Any
): Boolean? {
    val document = Document().apply { put(field, value) }
    val options = DeleteOptions().apply { limit(1) }

    return this.deleteOne(document, options).awaitSuspending()?.let {
        if (it.deletedCount == 1L) true else null
    }
}