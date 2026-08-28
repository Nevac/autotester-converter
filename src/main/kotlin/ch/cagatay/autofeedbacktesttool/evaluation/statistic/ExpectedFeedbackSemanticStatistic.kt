package ch.cagatay.autofeedbacktesttool.evaluation.statistic

import ch.cagatay.autofeedbacktesttool.attempt.FeedbackMetric
import org.bson.Document

data class ExpectedFeedbackSemanticStatistic(
    val id: String,
    val metric: FeedbackMetric,
    val sentence: String,
    val scores: List<ExpectedFeedbackSemanticScore>
) {
    fun toDocument(): Document =
        Document("id", id)
            .append("metric", metric)
            .append("sentence", sentence)
            .append("scores", scores.map { it.toDocument() })
}
