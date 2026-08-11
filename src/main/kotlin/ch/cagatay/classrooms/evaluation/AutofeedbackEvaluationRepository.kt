package ch.cagatay.classrooms.evaluation

import ch.cagatay.classrooms.ClassroomsDsl
import com.example.jooq.classrooms.tables.references.AUTO_FEEDBACK_EVALUATION
import java.util.UUID

class AutofeedbackEvaluationRepository private constructor() {
    val dsl = ClassroomsDsl.Companion.instance.dsl;

    companion object {
        val instance = AutofeedbackEvaluationRepository()
    }

    fun findById(id: UUID) : List<AutofeedbackEvaluation> {
        return dsl.select(*AUTO_FEEDBACK_EVALUATION.fields())
            .from(AUTO_FEEDBACK_EVALUATION)
            .where(AUTO_FEEDBACK_EVALUATION.ID.eq(id))
            .fetchInto(AutofeedbackEvaluation::class.java)
    }

    fun findByIds(ids: List<UUID>) : Map<UUID, AutofeedbackEvaluation> {
        return dsl.select(*AUTO_FEEDBACK_EVALUATION.fields())
            .from(AUTO_FEEDBACK_EVALUATION)
            .where(AUTO_FEEDBACK_EVALUATION.ID.`in`(ids))
            .fetchMap(AUTO_FEEDBACK_EVALUATION.ID, AutofeedbackEvaluation::class.java)
            .mapKeys { (id, _) -> requireNotNull(id) }
    }

    fun findAll() : Map<UUID, AutofeedbackEvaluation> {
        return dsl.select(*AUTO_FEEDBACK_EVALUATION.fields())
            .from(AUTO_FEEDBACK_EVALUATION)
            .fetchMap(AUTO_FEEDBACK_EVALUATION.ID, AutofeedbackEvaluation::class.java)
            .mapKeys { (id, _) -> requireNotNull(id) }
    }
}