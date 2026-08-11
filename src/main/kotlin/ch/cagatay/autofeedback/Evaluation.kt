package ch.cagatay.autofeedback

import java.time.LocalDateTime
import java.util.UUID

data class Evaluation(
    val id: UUID,
    val status: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime?,
    val logs: String?
)