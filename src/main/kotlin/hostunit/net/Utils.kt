package hostunit.net

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.mongodb.reactive.ReactiveMongoClient
import io.quarkus.mongodb.reactive.ReactiveMongoCollection
import org.bson.Document

var objectMapper: ObjectMapper = ObjectMapper()
    .findAndRegisterModules()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

fun ReactiveMongoClient.collection(
    collection: String = "address",
    database: String = "link"
): ReactiveMongoCollection<Document> = this.getDatabase(database).getCollection(collection)