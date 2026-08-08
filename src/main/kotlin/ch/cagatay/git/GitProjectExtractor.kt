package ch.cagatay.git

import java.nio.file.Path

class GitProjectExtractor {
    companion object {
        fun checkoutRepository(repoConfig: Repository): Path {
            val repo = GitLabRepository(repoConfig.url.toString(), repoConfig.user, repoConfig.token)
            return repo.checkout(repoConfig.commit)
        }
    }
}