package ch.cagatay.classrooms.extractors

@JvmRecord
data class SubmissionExtractorResult(
    val submission: Map<String, SubmissionExerciseExctatorResult>?,
    val isIgnored: Boolean
) {
    companion object {
        fun ok(submission: Map<String, SubmissionExerciseExctatorResult>): SubmissionExtractorResult {
            return SubmissionExtractorResult(
                submission,
                false
            )
        }

        fun ignore(): SubmissionExtractorResult {
            return SubmissionExtractorResult(
                emptyMap(),
                true
            )
        }
    }
}
