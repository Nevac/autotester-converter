package ch.cagatay.classrooms.evaluation

import java.time.LocalDateTime
import java.util.UUID

data class AutofeedbackEvaluation (
    val id: UUID,
    val studentName: String,
    val assignmentId: UUID,
    val commit: String,
    val creationDate: LocalDateTime
)