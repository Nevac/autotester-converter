package ch.cagatay.classrooms.exercise.assignment

import java.util.UUID

data class ExerciseAssignment(
    val id: UUID,
    val gitLabRepositoryTemplateId: String?,
    val gitLabGroupId: Int?,
)