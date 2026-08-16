package ch.cagatay.autofeedbacktesttool.attempt

import ch.cagatay.autofeedbacktesttool.exercise.Exercise
import org.bson.Document
import org.bson.types.ObjectId
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date

data class Attempt(
    val _id: ObjectId = ObjectId(),
    val name: String,
    val exercise: Exercise,
    val attempt: String,
    val expectedFeedback: ExpectedFeedback,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val complexity: AttemptComplexity,
    val fs26Feedback: Fs26Feedback?,
) {
    fun toDocument(): Document =
        Document("_id", _id)
            .append("name", name)
            .append("exercise", exercise.toDocument())
            .append("attempt", attempt)
            .append("expectedFeedback", expectedFeedback.toDocument())
            .append(
                "createdAt",
                Date.from(createdAt.toInstant(ZoneOffset.UTC))
            )
            .append(
                "updatedAt",
                Date.from(updatedAt.toInstant(ZoneOffset.UTC))
            )
            .append(
                "complexity", complexity
            )
            .append("fs26Feedback", fs26Feedback?.toDocument())
}