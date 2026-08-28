package ch.cagatay.autofeedbacktesttool.evaluation.score

import org.bson.Document

data class UnreferencedFeedback(
    val generatedFeedbackIndex: Int,
    val generatedSentence: String,
    val ignore: Boolean,
) {
    fun toDocument(): Document =
        Document("generatedFeedbackIndex", generatedFeedbackIndex)
            .append("generatedSentence", generatedSentence)
            .append("ignore", ignore)
}
