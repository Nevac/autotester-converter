package ch.cagatay.autofeedbacktesttool.attempt

import ch.cagatay.autofeedbacktesttool.util.documentList
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

        fun fromDocument(document: Document): ExpectedFeedback {
            return ExpectedFeedback(
                document.documentList("correctness", FeedbackReference::fromDocument),
                document.documentList("suggestion", FeedbackReference::fromDocument),
                document.documentList("codeStyle", FeedbackReference::fromDocument),
            )
        }
    }

    fun toDocument(): Document =
        Document("correctness", correctness.map { it.toDocument() })
            .append("suggestion", suggestion.map { it.toDocument() })
            .append("codeStyle", codeStyle.map { it.toDocument() })
}