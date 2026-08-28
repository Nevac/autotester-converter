package ch.cagatay.autofeedbacktesttool.rag

import org.bson.Document
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date

data class Rag(
    val _id: String,
    val name: String,
    val apiId: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    fun toDocument(): Document =
        Document("_id", _id)
            .append("name", name)
            .append("apiId", apiId)
            .append(
                "createdAt",
                Date.from(createdAt.toInstant(ZoneOffset.UTC))
            )
            .append(
                "updatedAt",
                Date.from(updatedAt.toInstant(ZoneOffset.UTC))
            )
}
