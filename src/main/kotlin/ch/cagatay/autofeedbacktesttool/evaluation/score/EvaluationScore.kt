package ch.cagatay.autofeedbacktesttool.evaluation.score

import org.bson.Document

data class EvaluationScore (
    val total: Double,
    val correctness: MetricScore,
    val suggestion: MetricScore,
    val codeStyle: MetricScore,
    val overgeneration: MetricOvergenerationScore,
    val confusion: Boolean
) {
    companion object {
        fun zero(): EvaluationScore {
            return EvaluationScore(
                0.0,
                MetricScore.zero(),
                MetricScore.zero(),
                MetricScore.zero(),
                MetricOvergenerationScore.zero(),
                false
            )
        }
    }

    fun toDocument(): Document =
        Document("total", total)
            .append("correctness", correctness.toDocument())
            .append("suggestion", suggestion.toDocument())
            .append("codeStyle", codeStyle.toDocument())
            .append("overgeneration", overgeneration.toDocument())
            .append("confusion", confusion)
}