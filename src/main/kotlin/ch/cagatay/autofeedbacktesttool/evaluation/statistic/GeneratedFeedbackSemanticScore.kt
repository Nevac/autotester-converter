package ch.cagatay.autofeedbacktesttool.evaluation.statistic

import org.bson.Document

data class GeneratedFeedbackSemanticScore(
    val id: String,
    val sentence: String,
    val score: Double
) {
    fun toDocument(): Document =
        Document("id", id)
            .append("sentence", sentence)
            .append("scores", score)
}
