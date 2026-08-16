package ch.cagatay.converter.submission

import ch.cagatay.autofeedback.PromptBuilder
import ch.cagatay.autofeedbacktesttool.attempt.Attempt
import ch.cagatay.autofeedbacktesttool.attempt.AttemptComplexity
import ch.cagatay.autofeedbacktesttool.attempt.AttemptFs26Repository
import ch.cagatay.autofeedbacktesttool.attempt.AttemptRepository
import ch.cagatay.autofeedbacktesttool.attempt.ExpectedFeedback
import ch.cagatay.autofeedbacktesttool.attempt.Fs26Feedback
import ch.cagatay.autofeedbacktesttool.exercise.ExerciseRepository
import ch.cagatay.classrooms.submission.SubmissionFetcherService
import ch.cagatay.classrooms.submission.SubmissionResult
import ch.cagatay.converter.evaluation.EvaluationContainer
import ch.cagatay.converter.evaluation.EvaluationContainerService
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
    val fullEvaluationCsvPath = "C:\\Users\\Cagatay\\Documents\\Studium\\MSE\\P9\\full-evaluations-3.csv"
    val fullCleanedEvaluationCsvPath = "C:\\Users\\Cagatay\\Documents\\Studium\\MSE\\P9\\full-cleaned-evaluations-3.csv"
    val randomSamplePath = "C:\\Users\\Cagatay\\Documents\\Studium\\MSE\\P9\\random-sample.csv"
    val attemptFs26Repository: AttemptFs26Repository = AttemptFs26Repository.Companion.instance

    companion object {
        val instance = SubmissionCommands()
    }

    fun generateSubmissionFeedbackTable() {
        val attempts = generateAttempts(
            EvaluationContainerService.instance.getAllEvaluationContainers()
        )
        attemptFs26Repository.upsertMany(attempts)
        File(fullEvaluationCsvPath)
            .outputStream()
            .use { it.writeCsv(attempts) }
        println("Generating table done")
    }

    fun transferSelectedSubmissions(attemptIds: List<SubmissionTransferKey>) {
        // Put attempts you wanna transfer here  by evaluation id
        val attemptsToTransfer = attemptIds
        val attemptsMap = attemptIds.associateBy { it.evaluationId }
        val attempts = generateAttempts(
            EvaluationContainerService.instance.getEvaluationContainersByIds(attemptsToTransfer.map { it.evaluationId })
        ).filter {
            if(it.fs26Feedback != null) {
                val evaluationId = UUID.fromString(it.fs26Feedback.autofeedbackEvaluationId)
                if(attemptsMap.containsKey(evaluationId)) {
                    attemptsMap[evaluationId]!!.exercises.contains(it.fs26Feedback.exerciseName)
                } else false
            } else false
        }

        AttemptRepository.instance.upsertMany(
            attempts
        )
        println("Submissions transferred to Attempts")
    }

    fun createRandomSample(
        input: Path,
        output: Path
    ) {
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

    fun mergeCsv() {
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

    fun mergeCsvFiles(
        inputFiles: List<Path>,
        outputFile: Path
    ) {
        require(inputFiles.isNotEmpty()) {
            "At least one input CSV is required"
        }

        val normalizedOutput = outputFile.toAbsolutePath().normalize()
        val normalizedInputs = inputFiles.map {
            it.toAbsolutePath().normalize()
        }

        require(normalizedOutput !in normalizedInputs) {
            "The output file cannot also be an input file"
        }

        val inputFormat = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .get()

        Files.newBufferedWriter(
            outputFile,
            StandardCharsets.UTF_8
        ).use { writer ->
            CSVFormat.DEFAULT.print(writer).use { printer ->
                var expectedHeader: List<String>? = null

                for (inputFile in inputFiles) {
                    Files.newBufferedReader(
                        inputFile,
                        StandardCharsets.UTF_8
                    ).use { reader ->
                        inputFormat.parse(reader).use { parser ->
                            val currentHeader = parser.headerNames.toList()

                            if (expectedHeader == null) {
                                expectedHeader = currentHeader
                                printer.printRecord(currentHeader)
                            } else {
                                require(currentHeader == expectedHeader) {
                                    "Header mismatch in $inputFile.\n" +
                                            "Expected: $expectedHeader\n" +
                                            "Found: $currentHeader"
                                }
                            }

                            for (record in parser) {
                                require(record.size() == expectedHeader!!.size) {
                                    "Invalid column count in $inputFile, " +
                                            "record ${record.recordNumber}"
                                }

                                printer.printRecord(record.toList())
                            }
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
        val submissionResults =
            runBlocking { SubmissionFetcherService.instance.generateSubmissionResults(evaluationContainers) }
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
                        templates[it.key]!! != it.value.llmFeedback
                    } else true
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
                            exerciseResult.llmFeedback,
                            sR.templates[it.key],
                            it.key
                        )
                    )
                }
        }
    }

    private fun OutputStream.writeCsv(attempts: List<Attempt>) {
        bufferedWriter(StandardCharsets.UTF_8).use { writer ->
            writer.appendLine(
                listOf("Attempt Id", "Evaluation Id", "Exercise Name", "Prompt", "Feedback")
                    .joinToString(",") { csvCell(it) }
            )

            attempts
                .filter { it.fs26Feedback != null }
                .forEach { attempt ->
                    val feedback = attempt.fs26Feedback!!

                    writer.appendLine(
                        listOf(
                            attempt.name,
                            feedback.autofeedbackEvaluationId,
                            attempt.exercise.name,
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