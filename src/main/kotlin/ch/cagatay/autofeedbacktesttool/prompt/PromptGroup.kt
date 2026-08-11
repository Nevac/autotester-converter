package ch.cagatay.autofeedbacktesttool.prompt

import java.time.LocalDateTime

data class PromptGroup (
    val _id: String,
    val prompts: List<String>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)