package ch.cagatay.git

import org.gitlab4j.api.GitLabApiException
import java.net.URI
import java.nio.file.Path

class GitlabService private constructor() {
    val gitlabApiFactory = GitlabApiFactory.instance

    companion object {
        val instance = GitlabService()
    }

    fun checkoutLatest(projectId: String): Path {
        val gitlabRepository = GitLabRepository(
            getHttpRepositoryURI(projectId).toString(),
            System.getenv("GITLAB_USER")
                ?: error("GITLAB_USER is not configured"),
            System.getenv("GITLAB_TOKEN")
                ?: error("GITLAB_USER is not configured")
        )
        return gitlabRepository.checkoutLatest()
    }

    fun checkout(projectId: String, commitHash: String): Path {
        val gitlabRepository = GitLabRepository(
            getHttpRepositoryURI(projectId).toString(),
            System.getenv("GITLAB_USER")
                ?: error("GITLAB_USER is not configured"),
            System.getenv("GITLAB_TOKEN")
                ?: error("GITLAB_USER is not configured")
        )
        return gitlabRepository.checkout(commitHash)
    }

    @Throws(GitLabApiException::class)
    fun getHttpRepositoryURI(projectId: String): URI {
        this.gitlabApiFactory.api().use { userGitlabApi ->
            return URI.create(userGitlabApi.projectApi.getProject(projectId).httpUrlToRepo)
        }
    }
}