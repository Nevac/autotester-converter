package ch.cagatay.autofeedback

import ch.cagatay.databases.Databases

class AutofeedbackRepository private constructor(databases: Databases) {
    val dsl = databases.autofeedbackDsl

    companion object {
        val instance = AutofeedbackRepository(Databases.instance)
    }


}