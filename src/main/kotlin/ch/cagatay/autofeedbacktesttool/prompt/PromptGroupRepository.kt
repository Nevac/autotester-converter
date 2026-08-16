package ch.cagatay.autofeedbacktesttool.prompt

import ch.cagatay.converter.databases.Databases
import com.mongodb.client.model.Filters.eq


class PromptGroupRepository private constructor(databases: Databases) {
    val mongoDb = databases.autofeedbackTestToolDatabase
    val collection = mongoDb.getCollection("promptgroups")

    companion object {
        val instance = PromptGroupRepository(Databases.instance)
    }

    fun findByName(name: String): PromptGroup? {
        return collection
            .find(eq("name", name))
            .map(PromptGroup::fromDocument)
            .first()
    }
}