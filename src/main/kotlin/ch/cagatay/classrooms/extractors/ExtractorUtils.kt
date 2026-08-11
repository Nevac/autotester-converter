package ch.cagatay.classrooms.extractors

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.regex.Pattern
import java.util.zip.ZipInputStream

class ExtractorUtils {
    private val isDirectoryValidRegex: Pattern = Pattern.compile("^src/main/java/[^/]+/[^/]+/[^/]+/exercise/[^/]+/")

    fun isDirectoryValid(entryName: String): Boolean {
        return isDirectoryValidRegex.matcher(entryName).find()
    }

    fun isJavaFile(entryName: String): Boolean {
        return entryName.endsWith(".java")
    }

    private val isExerciseDescriptionRegex: Pattern =
        Pattern.compile("^src/main/java/[^/]+/[^/]+/[^/]+/exercise/[^/]+/exercise\\.adoc$")

    fun isExerciseDescription(entryName: String): Boolean {
        return isExerciseDescriptionRegex.matcher(entryName).find()
    }

    fun extractExerciseName(entryName: String): String {
        val path: Array<String> = entryName.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        if (path[6] == "exercise") {
            return path[7]
        }

        throw IllegalArgumentException(
            "Invalid structure, exercise root folder should be placed the following way: src/main/java/{domain_ending}/{domain_name}/{project_name}/exercise"
        )
    }

    fun extractFileName(entryName: String): String {
        return entryName.substring(entryName.lastIndexOf("/") + 1)
    }

    fun convertMapToString(stringBuilderMap: MutableMap<String, StringBuilder>): MutableMap<String, String> {
        return stringBuilderMap.mapValues { it.value.toString() }.toMutableMap()
    }

    fun getOrCreateSbMapEntry(exerciseName: String, codeMap: MutableMap<String, StringBuilder>): StringBuilder {
        var sb: StringBuilder = StringBuilder()
        if (codeMap.containsKey(exerciseName)) {
            sb = codeMap[exerciseName]!!
        } else {
            codeMap[exerciseName] = sb
        }
        return sb
    }

    @Throws(IOException::class)
    fun readContent(entryName: String?, zis: ZipInputStream): String? {
        val baos = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var len: Int
        while ((zis.read(buffer).also { len = it }) > 0) {
            baos.write(buffer, 0, len)
        }
        return baos.toString("UTF-8")
    }
}