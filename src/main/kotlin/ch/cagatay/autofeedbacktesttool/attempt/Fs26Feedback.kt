package ch.cagatay.autofeedbacktesttool.attempt

import org.bson.Document
import java.util.UUID

data class Fs26Feedback (
    val autofeedbackEvaluationId: String,
    val llmFeedback: String?
) {
    fun toDocument(): Document =
        Document("autofeedbackEvaluationId", autofeedbackEvaluationId)
            .append("llmFeedback", llmFeedback)
}