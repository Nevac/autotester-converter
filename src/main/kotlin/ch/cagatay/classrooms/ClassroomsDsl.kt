package ch.cagatay.classrooms

import ch.cagatay.converter.databases.Databases

class ClassroomsDsl private constructor(databases: Databases) {
    val dsl = databases.classroomsDsl

    companion object {
        val instance = ClassroomsDsl(Databases.instance)
    }
}