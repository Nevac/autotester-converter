package ch.cagatay.autofeedback

import ch.cagatay.databases.Databases
import com.example.jooq.autofeedback.tables.references.EVALUATION
import com.example.jooq.autofeedback.tables.references.RESULT
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.UUID

class ResultRepository private constructor(databases: Databases) {
    val dsl = databases.autofeedbackDsl
    val mapper = jacksonObjectMapper()

    companion object {
        val instance = ResultRepository(Databases.instance)
    }

    fun findByEvaluationIdsLlmResults(evaluationIds: List<UUID>): Map<UUID, Result> {
        return dsl.selectFrom(RESULT)
            .where(
                RESULT.EVALUATION_ID.`in`(evaluationIds)
                    .and(RESULT.TYPE.eq("ch.fhnw.autofeedback.Backend.eval.step.llm.LlmResult"))
            )
            .fetchMap(RESULT.EVALUATION_ID, Result::class.java)
            .mapKeys { (id, _) -> requireNotNull(id) }
    }

    fun findAllLlmResults(): Map<UUID, Result> {
        return dsl.selectFrom(RESULT)
            .where(RESULT.TYPE.eq("ch.fhnw.autofeedback.Backend.eval.step.llm.LlmResult"))
            .fetchMap(RESULT.EVALUATION_ID, Result::class.java)
            .mapKeys { (id, _) -> requireNotNull(id) }
    }
}