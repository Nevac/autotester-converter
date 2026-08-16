package ch.cagatay.autofeedbacktesttool.evaluation.score

enum class OvergenerationValidity(val value: String) {
    VALID("valid"),
    IGNORE("ignore"),
    CODE_STYLE("codeStyle")
}