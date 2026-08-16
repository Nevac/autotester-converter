package ch.cagatay.autofeedbacktesttool.evaluation.group

enum class EvaluationGroupState(val value: String) {
    INITIATED("INITIATED"),
    RUNNING("RUNNING"),
    FAILURE("FAILURE"),
    DONE("DONE")
}