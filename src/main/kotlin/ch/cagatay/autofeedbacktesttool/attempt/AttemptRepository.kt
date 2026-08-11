package ch.cagatay.autofeedbacktesttool.attempt

import ch.cagatay.databases.Databases
import com.mongodb.bulk.BulkWriteResult
import com.mongodb.client.model.BulkWriteOptions
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.UpdateOneModel
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.WriteModel
import org.bson.Document

class AttemptRepository private constructor(databases: Databases) {
    val mongoDb = databases.autofeedbackTestToolDatabase
    val collection = mongoDb.getCollection("attempts")

    companion object {
        val instance = AttemptRepository(Databases.instance)
    }

    fun upsertMany(attempts: List<Attempt>): BulkWriteResult? {
        val documents = attempts.map { it.toDocument() }

        val operations: List<WriteModel<Document>> =
            documents.map { document ->
                val keyValue = requireNotNull(document["_id"])

                val fieldsToSet = Document(document).apply {
                    remove("_id")
                }

                UpdateOneModel(
                    eq("_id", keyValue),
                    Document("\$set", fieldsToSet),
                    UpdateOptions().upsert(true)
                )
            }

        return collection.bulkWrite(
            operations,
            BulkWriteOptions().ordered(false)
        )
    }
}