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
import org.apache.commons.csv.CSVFormat
import java.io.File
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import java.util.UUID

class SubmissionCommands {
    val fullEvaluationCsvPath = "C:\\Users\\Cagatay\\Documents\\Studium\\MSE\\P9\\full-evaluations-2.csv"
    val fullCleanedEvaluationCsvPath = "C:\\Users\\Cagatay\\Documents\\Studium\\MSE\\P9\\full-cleaned-evaluations.csv"
    val randomSamplePath = "C:\\Users\\Cagatay\\Documents\\Studium\\MSE\\P9\\random-sample.csv"

    companion object {
        val instance = SubmissionCommands()
    }

    fun generateSubmissionFeedbackTable() {
        val attempts = generateAttempts(
            EvaluationContainerService.instance.getAllEvaluationContainers()
        )
        File(fullEvaluationCsvPath)
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

    fun createRandomSample() {
        val input = Path.of(fullEvaluationCsvPath)
        val output = Path.of(randomSamplePath)

        val format = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .get()

        val (header, randomRows) =
            Files.newBufferedReader(input, StandardCharsets.UTF_8).use { reader ->
                format.parse(reader).use { parser ->
                    parser.headerNames.toList() to
                            parser.map { it.toList() }
                                .shuffled()
                                .take(10)
                }
            }

        Files.newBufferedWriter(output, StandardCharsets.UTF_8).use { writer ->
            CSVFormat.DEFAULT.print(writer).use { printer ->
                printer.printRecord(header)

                randomRows.forEach { row ->
                    printer.printRecord(row)
                }
            }
        }
    }

    fun cleanSubmissionFeedbackTable() {
        val input = Path.of(fullEvaluationCsvPath)
        val output = Path.of(fullCleanedEvaluationCsvPath)

        val inputFormat = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .get()

        Files.newBufferedReader(input, StandardCharsets.UTF_8).use { reader ->
            inputFormat.parse(reader).use { parser ->
                val header = parser.headerNames
                val feedbackIndex = header.indexOfFirst {
                    it.equals("Feedback", ignoreCase = true)
                }

                require(feedbackIndex >= 0) {
                    "CSV does not contain a Feedback column"
                }

                Files.newBufferedWriter(output, StandardCharsets.UTF_8).use { writer ->
                    CSVFormat.DEFAULT.print(writer).use { printer ->
                        printer.printRecord(header)

                        for (record in parser) {
                            val feedback = record.get(feedbackIndex)

                            if (isEmptyFeedbackTemplate(feedback)) {
                                continue
                            }

                            val cleanedRow = record.toList().toMutableList()
                            cleanedRow[feedbackIndex] = removeTrailingHash(feedback)

                            printer.printRecord(cleanedRow)
                        }
                    }
                }
            }
        }
    }

    private fun isEmptyFeedbackTemplate(feedback: String): Boolean {
        val lines = feedback
            .lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toList()

        val template1 = listOf(
            "### Correctness",
            "### Suggestion",
            "### Code Style"
        )
        val template2 = listOf(
            "### Correctness",
            "### Suggestion",
            "### Code Style"
        )
        val template3 = listOf(
            "1. Correctness",
            "2. Suggestion",
            "3. Code Style"
        )
        val template4 = listOf(
            "### 1. Correctness",
            "### 2. Suggestion",
            "### 3. Code Style"
        )

        return when (lines) {
            template1, template2, template3, template4 -> true
            else -> false
        }
    }

    private fun removeTrailingHash(feedback: String): String {
        return feedback
            .trimEnd()
            .removeSuffix("#")
            .trimEnd()
    }

    private fun generateAttempts(evaluationContainers: List<EvaluationContainer>): List<Attempt> {
        val submissionResults = runBlocking { SubmissionFetcherService.instance.generateSubmissionResults(evaluationContainers) }
        val exercises = ExerciseRepository.instance.findManyByNames(
            extractExerciseNames(submissionResults.values.toList())
        )

        return submissionResults.entries.flatMap { entry ->
            val sR = entry.value
            val templates = sR.templates
            sR.exercises
                .filter { !it.value.isIgnored }
                .filter {
                    if(templates.containsKey(it.key)) {
                        it.value.llmFeedback != templates[it.key]!!
                    } else false
                }
                .map {
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