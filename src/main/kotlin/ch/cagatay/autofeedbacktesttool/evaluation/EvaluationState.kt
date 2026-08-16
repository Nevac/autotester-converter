package ch.cagatay.autofeedbacktesttool.evaluation

enum class EvaluationState(val value: String) {
    INITIATED("INITIATED"),
    RUNNING("RUNNING"),
    FAILURE("FAILURE"),
    DONE("DONE")
}