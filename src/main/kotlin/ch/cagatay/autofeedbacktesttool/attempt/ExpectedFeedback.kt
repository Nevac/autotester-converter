package ch.cagatay.autofeedbacktesttool.attempt

import org.bson.Document

data class ExpectedFeedback (
    val correctness: List<FeedbackReference>,
    val suggestion: List<FeedbackReference>,
    val codeStyle: List<FeedbackReference>,
) {
    companion object {
        fun create(): ExpectedFeedback {
            return ExpectedFeedback(
                emptyList(),
                emptyList(),
                emptyList()
            )
        }
    }

    fun toDocument(): Document =
        Document("correctness", correctness)
            .append("suggestion", suggestion)
            .append("codeStyle", codeStyle)
}