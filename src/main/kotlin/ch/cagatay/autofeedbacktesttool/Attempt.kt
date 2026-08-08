package ch.cagatay.autofeedbacktesttool

import java.time.LocalDateTime

data class Attempt(
    val _id: String,
    val name: String,
    val exercise: Exercise,
    val attempt: String,
    val expectedFeedback: ExpectedFeedback,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val complexity: AttemptComplexity
)