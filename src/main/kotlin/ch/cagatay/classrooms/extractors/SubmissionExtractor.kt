package ch.cagatay.classrooms.extractors

import ch.cagatay.classrooms.Zipper
import ch.cagatay.classrooms.submission.PreviousExerciseFeedbacks
import java.io.FileInputStream
import java.io.IOException
import java.lang.StringBuilder
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class SubmissionExtractor {
    @Throws(IOException::class)
    fun extractAll(
        submission: Path,
        previousExerciseFeedbacks: PreviousExerciseFeedbacks
    ): SubmissionExtractorResult {
        val zipFile = Zipper.zipFolder(submission)
        val utils = ExtractorUtils()
        val ignoredExercises = mutableSetOf<String>()

        val exerciseCodes = mutableMapOf<String, StringBuilder>()


        ZipInputStream(FileInputStream(zipFile.toFile())).use { zis ->
            while (true) {
                val entry = zis.nextEntry ?: break
                val entryName = entry.name

                if (isRootIgnoreFile(entryName)) {
                    return SubmissionExtractorResult.ignore()
                }

                if (utils.isDirectoryValid(entryName)) {
                    val exerciseName = utils.extractExerciseName(entryName)
                    val sb = utils.getOrCreateSbMapEntry(exerciseName, exerciseCodes)

                    if (isExerciseIgnoreFile(entryName)) {
                        ignoredExercises.add(exerciseName)
                    } else if (
                        canFileBeAddedToExercise(
                            exerciseName,
                            entryName,
                            ignoredExercises
                        )
                    ) {
                        sb.append(utils.extractFileName(entryName))
                            .appendLine()
                            .append(utils.readContent(entryName, zis))
                            .appendLine()
                            .appendLine()
                    }
                }

                zis.closeEntry()
            }
        }
        return SubmissionExtractorResult.ok(
            convertMapToResults(
                exerciseCodes,
                ignoredExercises,
                previousExerciseFeedbacks
            )
        )

    }

    private fun canFileBeAddedToExercise(
        exerciseName: String,
        entryName: String,
        ignoredExercises: MutableSet<String>
    ): Boolean {
        val utils = ExtractorUtils()
        return utils.isJavaFile(entryName) && !isExerciseIgnored(exerciseName, ignoredExercises)
    }

    private fun isExerciseIgnored(
        exerciseName: String,
        ignoredExercises: Set<String>
    ): Boolean {
        return ignoredExercises.contains(exerciseName)
    }

    private fun isExerciseUnchanged(
        exerciseName: String,
        previousExerciseFeedbacks: PreviousExerciseFeedbacks
    ): Boolean {
        return previousExerciseFeedbacks.isPreviousLlmResultExisting &&
                previousExerciseFeedbacks.unchangedExerciseFeedback.containsKey(exerciseName)
    }

    private fun isRootIgnoreFile(entryName: String): Boolean {
        return entryName == ".llmignore"
    }

    private fun isExerciseIgnoreFile(entryName: String): Boolean {
        return entryName.endsWith(".llmignore")
    }

    private fun convertMapToResults(
        stringBuilderMap: MutableMap<String, StringBuilder>,
        ignoredExercises: MutableSet<String>,
        previousExerciseFeedbacks: PreviousExerciseFeedbacks
    ): Map<String, SubmissionExerciseExctatorResult> {
        return stringBuilderMap.mapValues {
            if (isExerciseIgnored(it.key, ignoredExercises)) {
                SubmissionExerciseExctatorResult.ignore()
            }
            else if (isExerciseUnchanged(it.key, previousExerciseFeedbacks)) {
                SubmissionExerciseExctatorResult.unchanged()
            } else {
                SubmissionExerciseExctatorResult.evaluate(it.value.toString())
            }
        }
    }

    companion object {
        @Throws(IOException::class)
        fun extract(
            submission: Path,
            previousExerciseFeedbacks: PreviousExerciseFeedbacks
        ): SubmissionExtractorResult {
            return SubmissionExtractor().extractAll(
                submission,
                previousExerciseFeedbacks
            )
        }
    }
}
