package ch.cagatay.autofeedbacktesttool.evaluation.score

import org.bson.Document

data class Overgeneration(
    val generatedFeedbackIndex: Int,
    val sentence: String,
    val validity: OvergenerationValidity
) {
    fun toDocument(): Document =
        Document("generatedFeedbackIndex", generatedFeedbackIndex)
            .append("sentence", sentence)
            .append("validity", validity.value)
}
