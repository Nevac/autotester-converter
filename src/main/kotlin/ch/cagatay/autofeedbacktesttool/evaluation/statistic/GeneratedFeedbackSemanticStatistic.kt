package ch.cagatay.autofeedbacktesttool.evaluation.statistic

import ch.cagatay.autofeedbacktesttool.attempt.FeedbackMetric
import org.bson.Document

data class GeneratedFeedbackSemanticStatistic(
    val index: Int,
    val sentence: String,
    val metric: FeedbackMetric,
    val scores: Array<GeneratedFeedbackSemanticScore>
) {
    fun toDocument(): Document =
        Document("index", index)
            .append("sentence", sentence)
            .append("metric", metric)
            .append("scores", scores.map { it.toDocument() })
}
