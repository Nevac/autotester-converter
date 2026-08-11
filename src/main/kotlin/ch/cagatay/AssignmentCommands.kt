package ch.cagatay

import ch.cagatay.autofeedbacktesttool.exercise.Exercise
import ch.cagatay.autofeedbacktesttool.exercise.ExerciseDifficulty
import ch.cagatay.autofeedbacktesttool.exercise.ExerciseRepository
import ch.cagatay.classrooms.assignment.AssignmentService
import java.time.LocalDateTime
import java.util.UUID

class AssignmentCommands {

    companion object {
        val instance = AssignmentCommands()
    }

    fun transferAssignments() {
        val exercises = AssignmentService.instance.generateAssignmentTextsForClassroom(
            UUID.fromString(
                System.getenv("CLASSROOM_ID")
                    ?: error("CLASSROOM_ID is not configured")
            )
        ).entries.flatMap { entry ->
            val assignmentInfo = entry.value
            assignmentInfo.exercises.map {
                Exercise(
                    name = assignmentInfo.assignment.name + "-" + it.key,
                    task = it.value,
                    difficulty = ExerciseDifficulty.EASY,
                    solution = assignmentInfo.solutions[it.key],
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
            }
        }

        val results = ExerciseRepository.instance.upsertMany(exercises)
        println("Assignments transferred to Exercises")
    }
}