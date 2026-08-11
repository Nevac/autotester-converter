package ch.cagatay.classrooms.extractors

@JvmRecord
data class SubmissionExerciseExctatorResult(
    val submission: String?,
    val isIgnored: Boolean,
    val isUnchanged: Boolean
) {
    companion object {
        fun evaluate(submission: String?): SubmissionExerciseExctatorResult {
            return SubmissionExerciseExctatorResult(
                submission,
                false,
                false
            )
        }


        fun ignore(): SubmissionExerciseExctatorResult {
            return SubmissionExerciseExctatorResult(
                "",
                true,
                false
            )
        }

        fun unchanged(): SubmissionExerciseExctatorResult {
            return SubmissionExerciseExctatorResult(
                "",
                false,
                true
            )
        }
    }
}