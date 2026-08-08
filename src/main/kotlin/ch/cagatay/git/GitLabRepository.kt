package ch.cagatay.git

import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider

class GitLabRepository(url: String?, user: String?, token: String) : GitRepository(url) {
    override val credentials: CredentialsProvider = UsernamePasswordCredentialsProvider(
        user,
        token
    )
}