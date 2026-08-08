package ch.cagatay.classrooms

import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class ExerciseExtractor {
    @Throws(IOException::class)
    fun extractAll(path: Path): MutableMap<String, String> {
        val codeMap = HashMap<String, StringBuilder>()
        val utils: ExtractorUtils = ExtractorUtils()

        val zipFile = Zipper.zipFolder(path)

        ZipInputStream(FileInputStream(zipFile.toFile())).use { zis ->
            var entry: ZipEntry?
            while ((zis.getNextEntry().also { entry = it }) != null) {
                val entryName = entry!!.getName()
                if (utils.isDirectoryValid(entryName)) {
                    val exerciseName = utils.extractExerciseName(entryName)
                    val sb = utils.getOrCreateSbMapEntry(exerciseName, codeMap)

                    if (utils.isJavaFile(entryName)) {
                        sb.append(utils.extractFileName(entryName))
                            .append(System.lineSeparator())
                            .append(utils.readContent(entryName, zis))
                            .append(System.lineSeparator())
                            .append(System.lineSeparator())
                    } else if (utils.isExerciseDescription(entryName)) {
                        val sbExercise = StringBuilder()
                        sbExercise.append(utils.readContent(entryName, zis))
                            .append(System.lineSeparator())
                            .append(System.lineSeparator())
                            .append("Exercise Code:")
                            .append(System.lineSeparator())
                        codeMap.put(exerciseName, sbExercise.append(sb))
                    }
                }
            }
        }
        return utils.convertMapToString(codeMap)
    }

    companion object {
        @Throws(IOException::class)
        fun extract(path: Path): MutableMap<String, String> {
            return ExerciseExtractor().extractAll(path)
        }
    }
}
