package ch.cagatay

import ch.cagatay.autofeedback.PromptBuilder
import ch.cagatay.autofeedbacktesttool.attempt.Attempt
import ch.cagatay.autofeedbacktesttool.attempt.AttemptComplexity
import ch.cagatay.autofeedbacktesttool.attempt.AttemptRepository
import ch.cagatay.autofeedbacktesttool.attempt.ExpectedFeedback
import ch.cagatay.autofeedbacktesttool.attempt.Fs26Feedback
import ch.cagatay.autofeedbacktesttool.exercise.ExerciseRepository
import ch.cagatay.classrooms.submission.SubmissionFetcherService
import ch.cagatay.classrooms.submission.SubmissionResult
import ch.cagatay.evaluation.EvaluationContainer
import ch.cagatay.evaluation.EvaluationContainerService
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.util.UUID

class SubmissionCommands {

    companion object {
        val instance = SubmissionCommands()
    }

    fun generateSubmissionFeedbackTable() {
        val attempts = generateAttempts(
            EvaluationContainerService.instance.getAllEvaluationContainers()
        )
        File("C:\\Users\\Cagatay\\Documents\\Studium\\MSE\\P9\\full-evaluations.csv")
            .outputStream()
            .use { it.writeCsv(attempts) }
        println("Generating table done")
    }

    fun transferSelectedSubmissions() {
        // Put attempts you wanna transfer here  by evaluation id
        val attemptsToTransfer = listOf(UUID.fromString("00165803-d890-43de-9825-130e46f1b28a"))
        AttemptRepository.instance.upsertMany(
            generateAttempts(
                EvaluationContainerService.instance.getEvaluationContainersByIds(attemptsToTransfer)
            )
        )
        println("Submissions transferred to Attempts")
    }

    private fun generateAttempts(evaluationContainers: List<EvaluationContainer>): List<Attempt> {
        val submissionResults = runBlocking { SubmissionFetcherService.instance.generateSubmissionResults(evaluationContainers) }
        val exercises = ExerciseRepository.instance.findManyByNames(
            extractExerciseNames(submissionResults.values.toList())
        )

        return submissionResults.entries.flatMap { entry ->
            val sR = entry.value
            sR.exercises.filter { !it.value.isIgnored }.map {
                val exerciseName = sR.assignment.name + "-" + it.key
                val exerciseResult = it.value
                Attempt(
                    name = exerciseResult.name,
                    exercise = exercises[exerciseName]!!,
                    attempt = exerciseResult.submissionText,
                    expectedFeedback = ExpectedFeedback.create(),
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now(),
                    complexity = AttemptComplexity.EASY,
                    fs26Feedback = Fs26Feedback(
                        sR.evaluationId.toString(),
                        exerciseResult.llmFeedback
                    )
                )
            }
        }
    }

    private fun OutputStream.writeCsv(attempts: List<Attempt>) {
        bufferedWriter(StandardCharsets.UTF_8).use { writer ->
            writer.appendLine(
                listOf("Evaluation Id", "Prompt", "Feedback")
                    .joinToString(",") { csvCell(it) }
            )

            attempts
                .filter { it.fs26Feedback != null }
                .forEach { attempt ->
                    val feedback = attempt.fs26Feedback!!

                    writer.appendLine(
                        listOf(
                            feedback.autofeedbackEvaluationId,
                            PromptBuilder.build(attempt),
                            feedback.llmFeedback
                        ).joinToString(",") { csvCell(it) }
                    )
                }
        }
    }

    private fun csvCell(value: Any?): String {
        val escaped = value?.toString()?.replace("\"", "\"\"") ?: ""
        return "\"$escaped\""
    }

    private fun extractExerciseNames(submissionResults: List<SubmissionResult>): List<String> {
        return submissionResults.flatMap { sR ->
            val assignmentName = sR.assignment.name
            sR.exercises.keys.map {
                "$assignmentName-$it"
            }
        }
    }
}