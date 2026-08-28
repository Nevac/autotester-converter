package ch.cagatay.autofeedbacktesttool.evaluation.score

import org.bson.Document

data class MetricScore(
    val score: Double,
    val referenceAddressings: List<ReferenceAddressing>,
    val similarityScore: List<UnreferencedFeedback>
) {
    companion object {
        fun zero(): MetricScore {
            return MetricScore(
                0.0,
                listOf(),
                listOf()
            )
        }
    }

    fun toDocument(): Document =
        Document("score", score)
            .append("referenceAddressings", referenceAddressings.map { it.toDocument() })
            .append("similarityScore", similarityScore.map { it.toDocument() })
}
