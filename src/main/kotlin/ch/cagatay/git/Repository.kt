package ch.cagatay.git

import java.net.URI

@JvmRecord
data class Repository(
    val url: URI, val commit: String, val user: String, val token: String
)
