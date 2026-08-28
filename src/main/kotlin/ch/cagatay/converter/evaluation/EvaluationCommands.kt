package ch.cagatay.converter.evaluation

import ch.cagatay.autofeedbacktesttool.Llm
import ch.cagatay.autofeedbacktesttool.attempt.AttemptRepository
import ch.cagatay.autofeedbacktesttool.evaluation.Evaluation
import ch.cagatay.autofeedbacktesttool.evaluation.EvaluationAst
import ch.cagatay.autofeedbacktesttool.evaluation.EvaluationRepository
import ch.cagatay.autofeedbacktesttool.evaluation.EvaluationState
import ch.cagatay.autofeedbacktesttool.evaluation.group.EvaluationGroup
import ch.cagatay.autofeedbacktesttool.evaluation.group.EvaluationGroupLlm
import ch.cagatay.autofeedbacktesttool.evaluation.group.EvaluationGroupLlmScore
import ch.cagatay.autofeedbacktesttool.evaluation.group.EvaluationGroupRepository
import ch.cagatay.autofeedbacktesttool.evaluation.group.EvaluationGroupState
import ch.cagatay.autofeedbacktesttool.evaluation.score.EvaluationScore
import ch.cagatay.autofeedbacktesttool.evaluation.statistic.EvaluationSemanticStatistic
import ch.cagatay.autofeedbacktesttool.prompt.PromptGroupRepository
import java.time.LocalDateTime

class EvaluationCommands private constructor() {

    companion object {
        val instance = EvaluationCommands()
    }

    fun prepareEvaluationGroup() {
        val pg = PromptGroupRepository.instance.findByName("fs26")!!
        val attempts = AttemptRepository.instance.findAllFs26()

        val evaluationGroup = EvaluationGroup(
            name = "FS26Eval",
            promptGroup = pg,
            attempts = attempts,
            llms = mapOf(Llm.GPT_5_2 to EvaluationGroupLlm(
                Llm.GPT_5_2,
                EvaluationState.DONE,
                EvaluationGroupLlmScore.zero()
            )),
            state = EvaluationGroupState.DONE,
            astEnabled = false,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val evaluations = attempts.map {
            Evaluation(
                name = "${Llm.GPT_5_2.value}-${it.name}",
                evaluationGroup = evaluationGroup._id.toString(),
                attempt = it,
                promptGroup = evaluationGroup.promptGroup,
                llm = Llm.GPT_5_2,
                generatedFeedback = it.fs26Feedback!!.llmFeedback!!,
                state = EvaluationState.DONE,
                score = EvaluationScore.zero(),
                semanticStatic = EvaluationSemanticStatistic.empty(),
                ast = EvaluationAst.disabled(),
                ragDocuments = emptyList(),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        }

        EvaluationGroupRepository.instance.upsertMany(listOf(evaluationGroup))
        EvaluationRepository.instance.upsertMany(evaluations)
    }
}