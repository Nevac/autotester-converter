package ch.cagatay.classrooms

import ch.cagatay.classrooms.Zipper.zipFolder
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Path
import java.util.*
import java.util.Collections.emptyMap
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class SolutionExtractor {
    @Throws(IOException::class)
    fun extractAll(path: Path): MutableMap<String, String> {
        val zipFile = zipFolder(path)
        val codeMap = HashMap<String, StringBuilder>()
        val utils = ExtractorUtils()

        ZipInputStream(FileInputStream(zipFile.toFile())).use { zis ->
            var entry: ZipEntry?
            while ((zis.getNextEntry().also { entry = it }) != null) {
                val entryName = entry!!.getName()

                if (utils.isDirectoryValid(entryName) && utils.isJavaFile(entryName)) {
                    val exerciseName = utils.extractExerciseName(entryName)
                    val sb = utils.getOrCreateSbMapEntry(exerciseName, codeMap)

                    sb.append(utils.extractFileName(entryName))
                        .append(System.lineSeparator())
                        .append(utils.readContent(entryName, zis))
                        .append(System.lineSeparator())
                        .append(System.lineSeparator())
                }
            }
        }
        return utils.convertMapToString(codeMap)
    }

    companion object {
        @Throws(IOException::class)
        fun extract(path: Path?): MutableMap<String, String> {
            if (path != null) {
                return SolutionExtractor().extractAll(path)
            } else {
                return emptyMap()
            }
        }
    }
}
