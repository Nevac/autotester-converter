package ch.cagatay.autofeedbacktesttool

data class FeedbackReference(
    val _id: String,
    val id: String,
    val references: List<String>,
    val metric: FeedbackMetric
)