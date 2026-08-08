package ch.cagatay.autofeedbacktesttool

import ch.cagatay.databases.Databases
import com.mongodb.bulk.BulkWriteResult
import com.mongodb.client.model.BulkWriteOptions
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.UpdateOneModel
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.WriteModel
import org.bson.Document

class ExerciseRepository private constructor(databases: Databases) {
    val mongoDb = databases.autofeedbackTestToolDatabase
    val collection = mongoDb.getCollection("exercises")

    companion object {
        val instance = ExerciseRepository(Databases.instance)
    }

    fun upsertMany(exercises: List<Exercise>): BulkWriteResult? {
        val documents = exercises.map { it.toDocument() }

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