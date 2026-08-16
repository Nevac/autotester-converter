package ch.cagatay.autofeedbacktesttool.attempt

import org.bson.Document

data class Fs26Feedback (
    val autofeedbackEvaluationId: String,
    val llmFeedback: String?,
    val template: String?,
    val exerciseName: String?,
) {
    fun toDocument(): Document =
        Document("autofeedbackEvaluationId", autofeedbackEvaluationId)
            .append("llmFeedback", llmFeedback)
            .append("template", template)
            .append("exerciseName", exerciseName)
}