package ch.cagatay.autofeedbacktesttool.evaluation

import ch.cagatay.autofeedbacktesttool.Llm
import ch.cagatay.autofeedbacktesttool.attempt.Attempt
import ch.cagatay.autofeedbacktesttool.evaluation.group.EvaluationGroupLlm
import ch.cagatay.autofeedbacktesttool.evaluation.score.EvaluationScore
import ch.cagatay.autofeedbacktesttool.evaluation.statistic.EvaluationSemanticStatistic
import ch.cagatay.autofeedbacktesttool.prompt.PromptGroup
import org.bson.Document
import org.bson.types.ObjectId
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date

data class Evaluation(
    val _id: ObjectId = ObjectId(),
    val name: String,
    val evaluationGroup: String,
    val attempt: Attempt,
    val promptGroup: PromptGroup,
    val llm: Llm,
    val generatedFeedback: String,
    val state: EvaluationState,
    val score: EvaluationScore,
    val semanticStatic: EvaluationSemanticStatistic,
    val ast: EvaluationAst,
    val ragDocuments: List<Any>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun fromDocument(document: Document): Evaluation =
            Evaluation(
                _id = requireNotNull(document.getObjectId("_id")),
                name = requireNotNull(document.getString("name")),
                evaluationGroup = requireNotNull(document.getString("promptGroup")),
                attempt = requireNotNull(document.get("attempt", Attempt::class.java)),
                promptGroup = requireNotNull(document.get("promptGroup", PromptGroup::class.java)),
                llm = requireNotNull(document.get("llm", Llm::class.java)),
                generatedFeedback = requireNotNull(document.getString("generatedFeedback")),
                state = requireNotNull(document.get("state", EvaluationState::class.java)),
                score = requireNotNull(document.get("score", EvaluationScore::class.java)),
                semanticStatic = requireNotNull(document.get("semanticStatic", EvaluationSemanticStatistic::class.java)),
                ast = requireNotNull(document.get("ast", EvaluationAst::class.java)),
                ragDocuments = requireNotNull(document.getList("ragDocuments", String::class.java)),
                createdAt = requireNotNull(document.getDate("createdAt"))
                    .toInstant()
                    .atOffset(ZoneOffset.UTC)
                    .toLocalDateTime(),
                updatedAt = requireNotNull(document.getDate("updatedAt"))
                    .toInstant()
                    .atOffset(ZoneOffset.UTC)
                    .toLocalDateTime()
            )
    }

    fun toDocument(): Document =
        Document("_id", _id)
            .append("name", name)
            .append("evaluationGroup", evaluationGroup)
            .append("attempt", attempt.toDocument())
            .append("promptGroup", promptGroup.toDocument())
            .append("llm", llm.value)
            .append("generatedFeedback", generatedFeedback)
            .append("state", state.value)
            .append("score", score.toDocument())
            .append("semanticStatic", semanticStatic.toDocument())
            .append("ast", ast.toDocument())
            .append("ragDocuments", ragDocuments)
            .append(
                "createdAt",
                Date.from(createdAt.toInstant(ZoneOffset.UTC))
            )
            .append(
                "updatedAt",
                Date.from(updatedAt.toInstant(ZoneOffset.UTC))
            )
}