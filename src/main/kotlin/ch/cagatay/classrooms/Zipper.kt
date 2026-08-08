package ch.cagatay.classrooms

import org.apache.commons.compress.archivers.zip.Zip64Mode
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import org.apache.commons.compress.utils.IOUtils
import java.io.IOException
import java.io.UncheckedIOException
import java.nio.file.Files
import java.nio.file.Path

object Zipper {
    @Throws(IOException::class)
    fun zipFolder(sourceDir: Path): Path {
        val zipFile = Files.createTempFile(
            sourceDir.getFileName().toString() + "_",
            ".zip"
        )

        ZipArchiveOutputStream(Files.newOutputStream(zipFile)).use { zos ->
            zos.setUseZip64(Zip64Mode.AsNeeded)
            Files.walk(sourceDir).forEach { path: Path? ->
                try {
                    // Optional: exclude .git directory
                    if (path!!.startsWith(sourceDir.resolve(".git"))) {
                        return@forEach
                    }

                    if (Files.isDirectory(path)) {
                        return@forEach  // directories are implied
                    }

                    val relativePath = sourceDir.relativize(path)
                    val entryName = relativePath.toString().replace("\\", "/")

                    val entry: ZipArchiveEntry =
                        ZipArchiveEntry(path.toFile(), entryName)

                    zos.putArchiveEntry(entry)

                    Files.newInputStream(path).use { `in` ->
                        IOUtils.copy(`in`, zos)
                    }
                    zos.closeArchiveEntry()
                } catch (e: IOException) {
                    throw UncheckedIOException(e)
                }
            }
        }
        return zipFile
    }
}
