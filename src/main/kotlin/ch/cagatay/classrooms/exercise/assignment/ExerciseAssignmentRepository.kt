package ch.cagatay.classrooms.exercise.assignment

import ch.cagatay.classrooms.ClassroomsDsl
import ch.cagatay.classrooms.evaluation.AutofeedbackEvaluation
import com.example.jooq.classrooms.tables.references.AUTO_FEEDBACK_EVALUATION
import com.example.jooq.classrooms.tables.references.EXERCISE_ASSIGNMENT
import java.util.UUID

class ExerciseAssignmentRepository private constructor() {
    val dsl = ClassroomsDsl.Companion.instance.dsl;

    companion object {
        val instance = ExerciseAssignmentRepository()
    }

    fun findById(id: UUID) : List<ExerciseAssignment> {
        return dsl.select(*EXERCISE_ASSIGNMENT.fields())
            .from(EXERCISE_ASSIGNMENT)
            .where(EXERCISE_ASSIGNMENT.ID.eq(id))
            .fetchInto(ExerciseAssignment::class.java)
    }

    fun findByIds(ids: List<UUID>) : Map<UUID, ExerciseAssignment> {
        return dsl.select(*EXERCISE_ASSIGNMENT.fields())
            .from(EXERCISE_ASSIGNMENT)
            .where(EXERCISE_ASSIGNMENT.ID.`in`(ids))
            .fetchMap(EXERCISE_ASSIGNMENT.ID, ExerciseAssignment::class.java)
            .mapKeys { (id, _) -> requireNotNull(id) }
    }

    fun findAll() : Map<UUID, ExerciseAssignment> {
        return dsl.select(*EXERCISE_ASSIGNMENT.fields())
            .from(EXERCISE_ASSIGNMENT)
            .fetchMap(EXERCISE_ASSIGNMENT.ID, ExerciseAssignment::class.java)
            .mapKeys { (id, _) -> requireNotNull(id) }
    }
}