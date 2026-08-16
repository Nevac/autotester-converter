package ch.cagatay.autofeedbacktesttool.evaluation.score

data class MetricScore(
    val score: Int,
    val referenceAddressings: Array<ReferenceAddressing>,
    val unreferencedFeedbacks: Array<UnreferencedFeedback>
)
