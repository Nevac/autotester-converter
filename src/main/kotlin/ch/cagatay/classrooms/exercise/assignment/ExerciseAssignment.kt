package ch.cagatay.classrooms.exercise.assignment

import java.util.UUID

data class ExerciseAssignment(
    val id: UUID,
    val gitlabRepositoryTemplateId: String?,
    val gitlabGroupId: Int?,
)