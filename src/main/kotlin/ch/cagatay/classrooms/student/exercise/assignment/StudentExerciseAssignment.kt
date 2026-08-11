package ch.cagatay.classrooms.student.exercise.assignment

import java.util.UUID

data class StudentExerciseAssignment(
    val id: UUID,
    val gitlabProjectId: Int,
    val assignmentId: UUID,
    val studentName: String,
)