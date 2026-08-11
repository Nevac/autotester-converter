package ch.cagatay.classrooms.submission

data class ExerciseResult(
    val name: String,
    val submissionText: String,
    val llmFeedback: String,
    val isIgnored: Boolean,
)