package ch.cagatay.autofeedbacktesttool.attempt

import org.bson.Document

data class Fs26Feedback (
    val autofeedbackEvaluationId: String,
    val llmFeedback: String?,
    val template: String?,
    val exerciseName: String?,
) {
    companion object {
        fun fromDocument(document: Document): Fs26Feedback {
            return Fs26Feedback(
                requireNotNull(document.getString("autofeedbackEvaluationId")),
                document.getString("llmFeedback"),
                document.getString("template"),
                document.getString("exerciseName")
            )
        }
    }

    fun toDocument(): Document =
        Document("autofeedbackEvaluationId", autofeedbackEvaluationId)
            .append("llmFeedback", llmFeedback)
            .append("template", template)
            .append("exerciseName", exerciseName)
}