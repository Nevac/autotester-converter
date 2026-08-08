package ch.cagatay.git

@JvmRecord
data class GitFileChange(
    val type: ChangeType?,
    val oldPath: String?,
    val newPath: String?
) {
    enum class ChangeType {
        ADDED,
        MODIFIED,
        DELETED,
        RENAMED,
        COPIED
    }

    /**
     * The most relevant current path.
     * For deleted files, this returns the previous path.
     */
    fun path(): String? {
        return if (type == ChangeType.DELETED) oldPath else newPath
    }
}
