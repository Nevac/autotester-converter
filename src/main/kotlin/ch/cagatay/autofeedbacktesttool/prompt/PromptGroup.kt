package ch.cagatay.autofeedbacktesttool.prompt

import ch.cagatay.autofeedbacktesttool.exercise.Exercise
import ch.cagatay.autofeedbacktesttool.exercise.ExerciseDifficulty
import org.bson.Document
import org.bson.types.ObjectId
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date

data class PromptGroup (
    val _id: ObjectId = ObjectId(),
    val name: String,
    val prompts: List<String>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun fromDocument(document: Document): PromptGroup =
            PromptGroup(
                _id = requireNotNull(document.getObjectId("_id")),
                name = requireNotNull(document.getString("name")),
                prompts = requireNotNull(document.getList("prompts", String::class.java)),
                createdAt = requireNotNull(document.getDate("createdAt"))
                    .toInstant()
                    .atOffset(ZoneOffset.UTC)
                    .toLocalDateTime(),
                updatedAt = requireNotNull(document.getDate("updatedAt"))
                    .toInstant()
                    .atOffset(ZoneOffset.UTC)
                    .toLocalDateTime()
            )
    }

    fun toDocument(): Document =
        Document("_id", _id)
            .append("name", name)
            .append("prompts", prompts)
            .append(
                "createdAt",
                Date.from(createdAt.toInstant(ZoneOffset.UTC))
            )
            .append(
                "updatedAt",
                Date.from(updatedAt.toInstant(ZoneOffset.UTC))
            )
}