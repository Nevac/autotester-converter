package ch.cagatay.autofeedbacktesttool.evaluation.score

data class ReferenceAddressing(
    val id: String,
    val ignore: Boolean,
    val addressed: Boolean,
    val expectedSentence: String,
    val generatedSentence: String,
    val similarityScore: Double
)
