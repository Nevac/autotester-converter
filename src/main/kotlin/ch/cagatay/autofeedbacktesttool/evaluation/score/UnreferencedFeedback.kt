package ch.cagatay.autofeedbacktesttool.evaluation.score

data class UnreferencedFeedback(
    val generatedFeedbackIndex: Int,
    val generatedSentence: String,
    val ignore: Boolean,
)
