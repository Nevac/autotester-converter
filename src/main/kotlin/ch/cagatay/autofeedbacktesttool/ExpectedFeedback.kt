package ch.cagatay.autofeedbacktesttool

data class ExpectedFeedback (
    val correctness: List<FeedbackReference>,
    val suggestion: List<FeedbackReference>,
    val codeStyle: List<FeedbackReference>,
)