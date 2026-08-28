package ch.cagatay.autofeedbacktesttool.evaluation.score

import org.bson.Document

data class ReferenceAddressing(
    val id: String,
    val ignore: Boolean,
    val addressed: Boolean,
    val expectedSentence: String,
    val generatedSentence: String,
    val similarityScore: Double
) {
    fun toDocument(): Document =
        Document("id", id)
            .append("ignore", ignore)
            .append("addressed", addressed)
            .append("expectedSentence", expectedSentence)
            .append("generatedSentence", generatedSentence)
            .append("similarityScore", similarityScore)
}
