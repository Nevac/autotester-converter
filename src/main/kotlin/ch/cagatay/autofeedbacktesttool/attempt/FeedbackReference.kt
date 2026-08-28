package ch.cagatay.autofeedbacktesttool.attempt

import org.bson.Document

data class FeedbackReference(
    val _id: String,
    val id: String,
    val references: List<String>,
    val metric: FeedbackMetric
) {
    companion object {
        fun fromDocument(document: Document): FeedbackReference {
            return FeedbackReference(
                requireNotNull(document.getObjectId("_id").toString()),
                requireNotNull(document.getString("id")),
                requireNotNull(document.getList("references", String::class.java)),
                FeedbackMetric.valueOf(requireNotNull(document.getString("metric"))),
            )
        }
    }

    fun toDocument(): Document =
        Document("_id", _id)
            .append("id", id)
            .append("references", references)
            .append("metric", metric.value)
}