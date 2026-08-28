package ch.cagatay.autofeedbacktesttool.evaluation.group

import org.bson.Document

data class EvaluationGroupLlmScore(
    val total: Double,
    val correctness: Double,
    val suggestion: Double,
    val codeStyle: Double,
    val overgeneration: Double
) {
    companion object {
        fun zero(): EvaluationGroupLlmScore {
            return EvaluationGroupLlmScore(
                0.0,
                0.0,
                0.0,
                0.0,
                0.0
            )
        }

        fun fromDocument(document: Document): EvaluationGroupLlmScore {
            return EvaluationGroupLlmScore(
                requireNotNull(document.getDouble("total")),
                requireNotNull(document.getDouble("correctness")),
                requireNotNull(document.getDouble("suggestion")),
                requireNotNull(document.getDouble("codeStyle")),
                requireNotNull(document.getDouble("overgeneration")),
                )
        }
    }

    fun toDocument(): Document =
        Document("total", total)
            .append("correctness", correctness)
            .append("suggestion", suggestion)
            .append("codeStyle", codeStyle)
            .append("overgeneration", overgeneration)
}
