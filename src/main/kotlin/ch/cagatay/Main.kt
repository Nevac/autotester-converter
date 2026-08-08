package ch.cagatay

import ch.cagatay.autofeedbacktesttool.Exercise
import ch.cagatay.autofeedbacktesttool.ExerciseDifficulty
import ch.cagatay.autofeedbacktesttool.ExerciseRepository
import ch.cagatay.classrooms.AssignmentService
import ch.cagatay.databases.Databases
import java.time.LocalDateTime
import java.util.UUID

fun greeting(name: String = "World") = "Hello, $name!"

fun main() {
    val databases = Databases.instance
    databases.verifyConnections()

    convertAssignments()

    databases.close()
}

fun convertAssignments() {
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