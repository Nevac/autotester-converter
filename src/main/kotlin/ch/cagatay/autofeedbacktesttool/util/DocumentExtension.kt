package ch.cagatay.autofeedbacktesttool.util

import org.bson.Document

fun Document.requiredDocument(name: String): Document =
    requireNotNull(get(name, Document::class.java)) {
        "Missing document field: $name"
    }

fun <T> Document.documentList(
    field: String,
    mapper: (Document) -> T
): List<T> =
    getList(field, Document::class.java)
        ?.map(mapper)
        .orEmpty()