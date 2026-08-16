package ch.cagatay.autofeedbacktesttool.evaluation

import org.bson.Document
import org.bson.types.ObjectId

data class EvaluationAst(
    val _id: ObjectId = ObjectId(),
    val enabled: Boolean,
    val constructs: Array<String>
) {
    companion object {
        fun disabled(): EvaluationAst {
            return EvaluationAst(
                enabled = false,
                constructs = emptyArray()
            )
        }
    }

    fun toDocument(): Document =
        Document("_id", _id)
            .append("enabled", enabled)
            .append("constructs", constructs)
}