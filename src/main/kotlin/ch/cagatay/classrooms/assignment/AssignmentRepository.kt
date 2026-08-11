package ch.cagatay.classrooms.assignment

import ch.cagatay.classrooms.ClassroomsDsl
import com.example.jooq.classrooms.tables.references.ASSIGNMENT
import com.example.jooq.classrooms.tables.references.AUTO_FEEDBACK_ASSIGNMENT
import com.example.jooq.classrooms.tables.references.EXERCISE_ASSIGNMENT
import org.jooq.SelectOnConditionStep
import java.util.UUID

class AssignmentRepository private constructor() {
    val dsl = ClassroomsDsl.Companion.instance.dsl;

    companion object {
        val instance = AssignmentRepository()
    }

    fun findAssignmentsByClassroom(classroomId: UUID): List<Assignment> {
        return assignmentSelect()
            .where(ASSIGNMENT.CLASSROOM_ID.eq(classroomId))
            .fetchInto(Assignment::class.java)
    }

    fun findByIds(ids: List<UUID>): Map<UUID, Assignment> {
        return assignmentSelect()
            .where(ASSIGNMENT.ID.`in`(ids))
            .fetchMap(ASSIGNMENT.ID, Assignment::class.java)
            .mapKeys { (id, _) -> requireNotNull(id) }
    }

    fun assignmentSelect(): SelectOnConditionStep<*> {
        return dsl.select(*ASSIGNMENT.fields(), *EXERCISE_ASSIGNMENT.fields(), *AUTO_FEEDBACK_ASSIGNMENT.fields())
            .from(ASSIGNMENT)
            .join(EXERCISE_ASSIGNMENT)
            .on(ASSIGNMENT.ID.eq(EXERCISE_ASSIGNMENT.ID))
            .join(AUTO_FEEDBACK_ASSIGNMENT)
            .on(ASSIGNMENT.ID.eq(AUTO_FEEDBACK_ASSIGNMENT.ID))
    }
}