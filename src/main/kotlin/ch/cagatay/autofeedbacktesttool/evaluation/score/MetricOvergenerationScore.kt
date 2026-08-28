package ch.cagatay.autofeedbacktesttool.evaluation.score

import org.bson.Document

data class MetricOvergenerationScore(
    val score: Double,
    val overgenerations: List<Overgeneration>
) {
    companion object {
        fun zero(): MetricOvergenerationScore {
            return MetricOvergenerationScore(
                0.0,
                listOf()
            )
        }
    }

    fun toDocument(): Document =
        Document("score", score)
            .append("overgenerations", overgenerations.map { it.toDocument() })
}
