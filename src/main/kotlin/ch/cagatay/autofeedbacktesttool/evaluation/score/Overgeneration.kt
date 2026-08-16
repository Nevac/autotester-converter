package ch.cagatay.autofeedbacktesttool.evaluation.score

data class Overgeneration(
    val generatedFeedbackIndex: Int,
    val sentence: String,
    val validity: OvergenerationValidity
)
