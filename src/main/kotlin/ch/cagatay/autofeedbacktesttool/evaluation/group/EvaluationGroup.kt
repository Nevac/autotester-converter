package ch.cagatay.autofeedbacktesttool.evaluation.group

import ch.cagatay.autofeedbacktesttool.Llm
import ch.cagatay.autofeedbacktesttool.attempt.Attempt
import ch.cagatay.autofeedbacktesttool.prompt.PromptGroup
import ch.cagatay.autofeedbacktesttool.rag.Rag
import ch.cagatay.autofeedbacktesttool.rag.RagStatic
import org.bson.Document
import org.bson.types.ObjectId
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date

data class EvaluationGroup(
    val _id: ObjectId = ObjectId(),
    val name: String,
    val promptGroup: PromptGroup,
    val attempts: List<Attempt>,
    val llms: Map<Llm, EvaluationGroupLlm>,
    val state: EvaluationGroupState,
    val astEnabled: Boolean,
    val rag: Rag? = null,
    val ragStatic: RagStatic? = null,
    val bestLlm: Llm? = null,
    val bestScore: Double? = null,
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
                llms = llmsFromDocument(
                    requireNotNull(document.get("llms", Document::class.java)) {
                        "Missing document field: llms"
                    }
                ),
                state = requireNotNull(document.get("state", EvaluationGroupState::class.java)),
                rag = document.get("ragStatic", Rag::class.java),
                ragStatic = document.get("ragStatic", RagStatic::class.java),
                astEnabled = requireNotNull(document.getBoolean("astEnabled")),
                bestLlm = document.get("bestLlm", Llm::class.java),
                bestScore = document.getDouble("bestScore"),
                createdAt = requireNotNull(document.getDate("createdAt"))
                    .toInstant()
                    .atOffset(ZoneOffset.UTC)
                    .toLocalDateTime(),
                updatedAt = requireNotNull(document.getDate("updatedAt"))
                    .toInstant()
                    .atOffset(ZoneOffset.UTC)
                    .toLocalDateTime()
            )

        private fun llmsFromDocument(
            document: Document
        ): Map<Llm, EvaluationGroupLlm> =
            document.entries.associate { (key, value) ->
                val valueDocument = value as? Document
                    ?: error("Expected llms.$key to be a document")

                Llm.valueOf(key) to
                        EvaluationGroupLlm.fromDocument(valueDocument)
            }
    }

    fun toDocument(): Document =
        Document("_id", _id)
            .append("name", name)
            .append("promptGroup", promptGroup.toDocument())
            .append("attempts", attempts.map { it.toDocument() })
            .append("llms", llmsToDocument(llms))
            .append("state", state)
            .append("rag", rag)
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

    private fun llmsToDocument(
        llms: Map<Llm, EvaluationGroupLlm>
    ): Document =
        Document().apply {
            llms.forEach { (llm, evaluationGroup) ->
                append(llm.value, evaluationGroup.toDocument())
            }
        }
}