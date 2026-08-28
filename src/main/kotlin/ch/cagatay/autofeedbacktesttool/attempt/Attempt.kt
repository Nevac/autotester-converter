package ch.cagatay.autofeedbacktesttool.attempt

import ch.cagatay.autofeedbacktesttool.exercise.Exercise
import ch.cagatay.autofeedbacktesttool.exercise.ExerciseDifficulty
import ch.cagatay.autofeedbacktesttool.util.requiredDocument
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
    companion object {
        fun fromDocument(document: Document): Attempt =
            Attempt(
                _id = requireNotNull(document.getObjectId("_id")),
                name = requireNotNull(document.getString("name")),
                exercise = Exercise.fromDocument(document.requiredDocument("exercise")),
                attempt = requireNotNull(document.getString("attempt")),
                expectedFeedback = ExpectedFeedback.fromDocument(document.requiredDocument("expectedFeedback")),
                complexity = AttemptComplexity.valueOf(
                    requireNotNull(document.getString("complexity"))
                ),
                fs26Feedback = Fs26Feedback.fromDocument(document.requiredDocument("fs26Feedback")),
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