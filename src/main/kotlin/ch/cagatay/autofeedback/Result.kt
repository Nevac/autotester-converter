package ch.cagatay.autofeedback

import java.util.UUID

data class Result(
    val id: UUID,
    val value: LlmResult,
    val type: String,
    val evaluationId: String
)