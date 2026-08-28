package ch.cagatay.autofeedbacktesttool.evaluation

import ch.cagatay.autofeedbacktesttool.evaluation.group.EvaluationGroup
import ch.cagatay.autofeedbacktesttool.exercise.Exercise
import ch.cagatay.converter.databases.Databases
import com.mongodb.bulk.BulkWriteResult
import com.mongodb.client.model.BulkWriteOptions
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.UpdateOneModel
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.WriteModel
import org.bson.Document

class EvaluationRepository private constructor(databases: Databases) {
    val mongoDb = databases.autofeedbackTestToolDatabase
    val collection = mongoDb.getCollection("evaluations")

    companion object {
        val instance = EvaluationRepository(Databases.instance)
    }

    fun upsertMany(exercises: List<Evaluation>): BulkWriteResult? {
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