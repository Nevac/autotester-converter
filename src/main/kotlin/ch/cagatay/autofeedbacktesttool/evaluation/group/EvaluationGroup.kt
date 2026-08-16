package ch.cagatay.autofeedbacktesttool.evaluation.group

import ch.cagatay.autofeedbacktesttool.Llm
import ch.cagatay.autofeedbacktesttool.attempt.Attempt
import ch.cagatay.autofeedbacktesttool.prompt.PromptGroup
import ch.cagatay.autofeedbacktesttool.rag.RagStatic
import org.bson.Document
import org.bson.types.ObjectId
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date

data class EvaluationGroup (
    val _id: ObjectId = ObjectId(),
    val name: String,
    val promptGroup: PromptGroup,
    val attempts: List<Attempt>,
    val llms: List<Llm>,
    val state: EvaluationGroupState,
    val ragStatic: RagStatic,
    val astEnabled: Boolean,
    val bestLlm: Llm,
    val bestScore: Double,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun fromDocument(document: Document): EvaluationGroup =
            EvaluationGroup(
                _id = requireNotNull(document.getObjectId("_id")),
                name = requireNotNull(document.getString("name")),
                promptGroup = requireNotNull(document.get("promptGroup", PromptGroup::class.java)),
                attempts = requireNotNull(document.getList("attempts", Attempt::class.java)),
                llms = requireNotNull(document.getList("llms", Llm::class.java)),
                state = requireNotNull(document.get("state", EvaluationGroupState::class.java)),
                ragStatic = requireNotNull(document.get("ragStatic", RagStatic::class.java)),
                astEnabled = requireNotNull(document.getBoolean("astEnabled")),
                bestLlm = requireNotNull(document.get("bestLlm", Llm::class.java)),
                bestScore = requireNotNull(document.getDouble("bestScore")),
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
            .append("promptGroup", promptGroup.toDocument())
            .append("attempts", attempts.map { it.toDocument() })
            .append("llms", llms)
            .append("state", state)
            .append("ragStatic", ragStatic)
            .append("astEnabled", astEnabled)
            .append("bestLlm", bestLlm)
            .append("bestScore", bestScore)
            .append(
                "createdAt",
                Date.from(createdAt.toInstant(ZoneOffset.UTC))
            )
            .append(
                "updatedAt",
                Date.from(updatedAt.toInstant(ZoneOffset.UTC))
            )
}