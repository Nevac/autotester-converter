package ch.cagatay.autofeedbacktesttool.evaluation.score

data class EvaluationScore (
    val total: Int,
    val correctness: MetricScore,
    val suggestion: MetricScore,
    val codeStyle: MetricScore,
    val overgeneration: MetricOvergenerationScore,
    val confusion: Boolean
)