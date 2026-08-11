package ch.cagatay.autofeedback

data class LlmResult(
    val status: String,
    val feedback: String,
)