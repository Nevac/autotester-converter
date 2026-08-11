package ch.cagatay

import ch.cagatay.databases.Databases

fun greeting(name: String = "World") = "Hello, $name!"

fun main() {
    val databases = Databases.instance
    databases.verifyConnections()

    //SubmissionCommands.instance.transferSelectedSubmissions()
    //SubmissionCommands.instance.generateSubmissionFeedbackTable()
    //AssignmentCommands.instance.transferAssignments()

    databases.close()
}