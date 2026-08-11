package ch.cagatay.classrooms.student.exercise.assignment

import java.util.UUID

data class AssignmentStudentKey(
    val assignmentId: UUID,
    val studentName: String,
)