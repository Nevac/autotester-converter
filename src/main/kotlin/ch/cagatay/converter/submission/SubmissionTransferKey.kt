package ch.cagatay.converter.submission

import java.util.UUID

data class SubmissionTransferKey(
    val evaluationId: UUID,
    val exercises: Set<String>,
) {
    companion object {
        fun from(
            evaluationId: String,
            exercises: Set<String>
        ): SubmissionTransferKey {
            return SubmissionTransferKey(
                UUID.fromString(evaluationId),
                exercises
            )
        }
    }
}