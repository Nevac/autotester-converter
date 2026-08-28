package ch.cagatay.autofeedbacktesttool.evaluation.group

import ch.cagatay.autofeedbacktesttool.Llm
import ch.cagatay.autofeedbacktesttool.evaluation.EvaluationState
import ch.cagatay.autofeedbacktesttool.util.requiredDocument
import org.bson.Document

data class EvaluationGroupLlm(
    val llm: Llm,
    val state: EvaluationState,
    val score: EvaluationGroupLlmScore
) {
    companion object {
        fun fromDocument(document: Document): EvaluationGroupLlm =
            EvaluationGroupLlm(
                llm = Llm.valueOf(requireNotNull(document.getString("llm"))),
                state = EvaluationState.valueOf(requireNotNull(document.getString("state"))),
                score = EvaluationGroupLlmScore.fromDocument(requireNotNull(document.requiredDocument("score"))),
            )
    }

    fun toDocument(): Document =
        Document("llm", llm.value)
            .append("state", state.value)
            .append("score", score.toDocument())
}
