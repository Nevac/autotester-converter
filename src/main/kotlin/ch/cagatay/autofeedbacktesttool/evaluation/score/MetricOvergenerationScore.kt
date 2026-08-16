package ch.cagatay.autofeedbacktesttool.evaluation.score

data class MetricOvergenerationScore(
    val score: Double,
    val overgenerations: Array<Overgeneration>
)
