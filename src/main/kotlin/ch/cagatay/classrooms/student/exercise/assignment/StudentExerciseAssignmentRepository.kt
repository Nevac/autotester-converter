package ch.cagatay.classrooms.student.exercise.assignment

import ch.cagatay.classrooms.ClassroomsDsl
import com.example.jooq.classrooms.tables.references.STUDENT_EXERCISE_ASSIGNMENT
import org.jooq.impl.DSL.row
import java.util.UUID

class StudentExerciseAssignmentRepository private constructor(){
    val dsl = ClassroomsDsl.instance.dsl

    companion object {
        val instance = StudentExerciseAssignmentRepository()
    }

    fun findByStudentAssignmentIds(ids: List<UUID>): Map<UUID, StudentExerciseAssignment> {
        return dsl.selectFrom(STUDENT_EXERCISE_ASSIGNMENT)
            .where(STUDENT_EXERCISE_ASSIGNMENT.ASSIGNMENT_ID.`in`(ids))
            .fetchMap(STUDENT_EXERCISE_ASSIGNMENT.ASSIGNMENT_ID, StudentExerciseAssignment::class.java)
            .mapKeys { (id, _) -> requireNotNull(id) }
    }

    fun findByAssignmentAndStudentKeys(
        keys: List<AssignmentStudentKey>,
    ): Map<AssignmentStudentKey, StudentExerciseAssignment> {
        if (keys.isEmpty()) return emptyMap()

        val requestedRows = keys.map {
            row(it.assignmentId, it.studentName)
        }

        return dsl.selectFrom(STUDENT_EXERCISE_ASSIGNMENT)
            .where(
                row(
                    STUDENT_EXERCISE_ASSIGNMENT.ASSIGNMENT_ID,
                    STUDENT_EXERCISE_ASSIGNMENT.STUDENT_NAME,
                ).`in`(requestedRows)
            )
            .fetchMap(
                { record ->
                    AssignmentStudentKey(
                        assignmentId = requireNotNull(record.assignmentId),
                        studentName = requireNotNull(record.studentName),
                    )
                },
                { record ->
                    record.into(StudentExerciseAssignment::class.java)
                },
            )
    }
}