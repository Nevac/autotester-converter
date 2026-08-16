package ch.cagatay.autofeedbacktesttool.evaluation.statistic

import org.bson.Document

data class ExpectedFeedbackSemanticScore(
    val sentence: String,
    val score: Int
) {
    fun toDocument(): Document =
        Document("sentence", sentence)
            .append("score", score)
}
