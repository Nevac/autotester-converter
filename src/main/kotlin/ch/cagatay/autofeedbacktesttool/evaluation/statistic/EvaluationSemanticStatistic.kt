package ch.cagatay.autofeedbacktesttool.evaluation.statistic

import org.bson.Document

data class EvaluationSemanticStatistic(
    val expectedFeedback: List<ExpectedFeedbackSemanticStatistic>,
    val generatedFeedback: List<GeneratedFeedbackSemanticStatistic>,
) {
    companion object {
        fun empty(): EvaluationSemanticStatistic {
            return EvaluationSemanticStatistic(
                expectedFeedback = listOf(),
                generatedFeedback = listOf(),
            )
        }
    }

    fun toDocument(): Document =
        Document("expectedFeedback", expectedFeedback.map { it.toDocument() })
            .append("generatedFeedback", generatedFeedback.map { it.toDocument() })
}
