package ch.cagatay.autofeedback

import ch.cagatay.converter.databases.Databases
import com.example.jooq.autofeedback.tables.references.EVALUATION
import java.util.UUID

class EvaluationRepository private constructor(databases: Databases) {
    val dsl = databases.autofeedbackDsl

    companion object {
        val instance = EvaluationRepository(Databases.instance)
    }

    fun findByIds(ids: List<UUID>): Map<UUID, Evaluation> {
        return dsl.selectFrom(EVALUATION)
            .where(EVALUATION.ID.`in`(ids))
            .fetchMap(EVALUATION.ID, Evaluation::class.java)
            .mapKeys { (id, _) -> requireNotNull(id) }
    }

    fun findAllSuccessful(): Map<UUID, Evaluation>  {
        return dsl.selectFrom(EVALUATION)
            .where(EVALUATION.STATUS.eq("SUCCESS"))
            .orderBy(EVALUATION.ID.asc())
            .offset(1705)
            .limit(5000)
            .fetchMap(EVALUATION.ID, Evaluation::class.java)
            .mapKeys { (id, _) -> requireNotNull(id) }
    }
}