package ch.cagatay.autofeedbacktesttool.exercise

import org.bson.Document
import org.bson.types.ObjectId
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date

data class Exercise(
    val _id: ObjectId = ObjectId(),
    val name: String,
    val task: String,
    val difficulty: ExerciseDifficulty,
    val solution: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun fromDocument(document: Document): Exercise =
            Exercise(
                _id = requireNotNull(document.getObjectId("_id")),
                name = requireNotNull(document.getString("name")),
                task = requireNotNull(document.getString("task")),
                difficulty = ExerciseDifficulty.valueOf(
                    requireNotNull(document.getString("difficulty"))
                ),
                solution = document.getString("solution"),
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
            .append("task", task)
            .append("difficulty", difficulty.name)
            .append("solution", solution)
            .append(
                "createdAt",
                Date.from(createdAt.toInstant(ZoneOffset.UTC))
            )
            .append(
                "updatedAt",
                Date.from(updatedAt.toInstant(ZoneOffset.UTC))
            )
}