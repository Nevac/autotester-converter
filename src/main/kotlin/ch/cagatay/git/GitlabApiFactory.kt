package ch.cagatay.git

import org.gitlab4j.api.GitLabApi

class GitlabApiFactory(val gitlabUrl: String, val gitlabToken: String) {

    companion object {
        val instance = GitlabApiFactory(
            System.getenv("GITLAB_URL")
                ?: error("GITLAB_URL is not configured"),
            System.getenv("GITLAB_TOKEN")
                ?: error("GITLAB_TOKEN is not configured"),
        )
    }

    fun api(): GitLabApi {
        return GitLabApi(
            gitlabUrl,
            gitlabToken
        )
    }


}