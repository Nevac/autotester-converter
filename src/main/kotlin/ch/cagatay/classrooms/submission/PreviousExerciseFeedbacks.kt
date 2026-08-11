package ch.cagatay.classrooms.submission

@JvmRecord
data class PreviousExerciseFeedbacks(
    val isPreviousLlmResultExisting: Boolean,
    val unchangedExerciseFeedback: MutableMap<String, String>
) {
    fun isExerciseUnchanged(exerciseName: String?): Boolean {
        return this.unchangedExerciseFeedback.containsKey(exerciseName)
    }

    companion object {
        fun withPreviousLlmResult(changedExercises: MutableMap<String, String>): PreviousExerciseFeedbacks {
            return PreviousExerciseFeedbacks(
                true,
                changedExercises
            )
        }

        fun noPreviousLlmResult(): PreviousExerciseFeedbacks {
            return PreviousExerciseFeedbacks(
                false,
                mutableMapOf()
            )
        }
    }
}
