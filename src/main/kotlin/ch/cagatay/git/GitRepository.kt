package ch.cagatay.git

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Metrics
import io.micrometer.core.instrument.Timer
import lombok.extern.slf4j.Slf4j
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ResetCommand
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.util.io.DisabledOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

import org.eclipse.jgit.transport.CredentialsProvider

@Slf4j
abstract class GitRepository(private val url: String?) {
    private val meterRegistry: MeterRegistry?

    private var git: Git? = null

    init {
        this.meterRegistry = Metrics.globalRegistry
    }

    protected abstract val credentials: CredentialsProvider?

    /**
     * Checkout the exact `commit` of this repository.
     *
     * @return path of the local repository.
     */
    @Throws(GitRepositoryException::class)
    fun checkout(commit: String?): Path {

        ensureRepositoryFetched()

        try {
            git!!.reset()
                .setMode(ResetCommand.ResetType.HARD)
                .setRef(commit)
                .call()
            return git!!.getRepository().getWorkTree().toPath().toAbsolutePath()
        } catch (e: GitAPIException) {
            throw GitRepositoryException(e.message)
        }
    }

    fun checkoutLatest(): Path {
        ensureRepositoryFetched()
        try {
            git!!.reset()
                .setMode(ResetCommand.ResetType.HARD)
                .call()
            return git!!.repository
                .getWorkTree()
                .toPath()
                .toAbsolutePath()
        } catch (e: GitAPIException) {
            throw GitRepositoryException(e.message)
        }
    }

    @Throws(GitAPIException::class, IOException::class)
    private fun cloneGit(): Git? {
        val dir = Files.createTempDirectory("git_")
        return Git.cloneRepository()
            .setURI(url)
            .setNoCheckout(true)
            .setDirectory(dir.toFile())
            .setCredentialsProvider(this.credentials)
            .call()
    }

    /**
     * Compare two commits and return only where changes happened.
     * This does not modify the working tree and does not produce a textual diff.
     *
     * @param fromCommit older/base commit
     * @param toCommit   newer/target commit
     */
    @Synchronized
    @Throws(GitRepositoryException::class)
    fun compare(
        fromCommit: String,
        toCommit: String
    ): MutableList<GitFileChange?> {
        validateRevision(fromCommit)
        validateRevision(toCommit)

        ensureRepositoryFetched()

        val repository = git!!.getRepository()
        try {
            RevWalk(repository).use { revWalk ->
                DiffFormatter(DisabledOutputStream.INSTANCE).use { diffFormatter ->
                    val from = resolveCommit(
                        repository,
                        revWalk,
                        fromCommit
                    )
                    val to = resolveCommit(
                        repository,
                        revWalk,
                        toCommit
                    )

                    diffFormatter.setRepository(repository)
                    diffFormatter.setDetectRenames(true)
                    return diffFormatter
                        .scan(from.getTree(), to.getTree())
                        .stream()
                        .map { entry: DiffEntry? -> this.mapChange(entry!!) }
                        .toList()
                }
            }
        } catch (e: IOException) {
            throw GitRepositoryException(e.message)
        }
    }

    @Throws(IOException::class, GitRepositoryException::class)
    private fun resolveCommit(
        repository: Repository,
        revWalk: RevWalk,
        revision: String?
    ): RevCommit {
        var objectId = repository.resolve(revision + "^{commit}")

        // Convenient when callers pass "main" instead of
        // "refs/remotes/origin/main".
        if (objectId == null) {
            objectId = repository.resolve(
                "refs/remotes/origin/" + revision + "^{commit}"
            )
        }

        if (objectId == null) {
            throw GitRepositoryException(
                "Commit or revision not found: " + revision
            )
        }

        return revWalk.parseCommit(objectId)
    }

    private fun mapChange(entry: DiffEntry): GitFileChange {
        val oldPath = normalizePath(entry.getOldPath())
        val newPath = normalizePath(entry.getNewPath())

        val type: GitFileChange.ChangeType? = when (entry.getChangeType()) {
            DiffEntry.ChangeType.ADD -> GitFileChange.ChangeType.ADDED
            DiffEntry.ChangeType.MODIFY -> GitFileChange.ChangeType.MODIFIED
            DiffEntry.ChangeType.DELETE -> GitFileChange.ChangeType.DELETED
            DiffEntry.ChangeType.RENAME -> GitFileChange.ChangeType.RENAMED
            DiffEntry.ChangeType.COPY -> GitFileChange.ChangeType.COPIED
        }

        return GitFileChange(type, oldPath, newPath)
    }

    private fun normalizePath(path: String?): String? {
        return if (DiffEntry.DEV_NULL == path) null else path
    }

    @Throws(GitRepositoryException::class)
    private fun validateRevision(revision: String) {
        if (revision == null || revision.isBlank()) {
            throw GitRepositoryException(
                "Commit or revision must not be blank"
            )
        }
    }

    @Throws(GitRepositoryException::class)
    private fun ensureRepositoryFetched() {
        if (git == null) {
            try {
                git = cloneGit()
            } catch (e: GitAPIException) {
                throw GitRepositoryException(e.message)
            } catch (e: IOException) {
                throw GitRepositoryException(e.message)
            }
        }

        try {
            git!!.fetch()
                .setForceUpdate(true)
                .setCredentialsProvider(this.credentials)
                .call()
        } catch (e: GitAPIException) {
            throw GitRepositoryException(e.message)
        }
    }
}