package ch.cagatay.autofeedbacktesttool

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