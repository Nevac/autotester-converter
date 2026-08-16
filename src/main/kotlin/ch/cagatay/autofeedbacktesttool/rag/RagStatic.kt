package ch.cagatay.autofeedbacktesttool.rag

import org.bson.types.ObjectId
import java.time.LocalDateTime

data class RagStatic(
    val _id: ObjectId = ObjectId(),
    val name: String,
    val exerciseRagDocuments: Map<String, Array<String>>,
    val attemptRagDocuments: Map<String, Array<String>>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)