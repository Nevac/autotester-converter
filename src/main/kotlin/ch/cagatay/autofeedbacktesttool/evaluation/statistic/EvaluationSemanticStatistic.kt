package ch.cagatay.autofeedbacktesttool.evaluation.statistic

import org.bson.Document

data class EvaluationSemanticStatistic(
    val expectedFeedback: Array<ExpectedFeedbackSemanticStatistic>,
    val generatedFeedback: Array<GeneratedFeedbackSemanticStatistic>,
) {
    companion object {
        fun empty(): EvaluationSemanticStatistic {
            return EvaluationSemanticStatistic(
                expectedFeedback = arrayOf(),
                generatedFeedback = arrayOf(),
            )
        }
    }

    fun toDocument(): Document =
        Document("expectedFeedback", expectedFeedback.map { it.toDocument() })
            .append("generatedFeedback", generatedFeedback.map { it.toDocument() })
}
