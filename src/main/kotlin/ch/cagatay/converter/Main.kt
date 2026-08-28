package ch.cagatay.converter

import ch.cagatay.converter.databases.Databases
import ch.cagatay.converter.evaluation.EvaluationCommands


fun main() {
    val databases = Databases.instance
    databases.verifyConnections()

    //EvaluationCommands.instance.prepareEvaluationGroup()
    //SubmissionCommands.instance.transferSelectedSubmissions()
    //SubmissionCommands.instance.generateSubmissionFeedbackTable()
    //AssignmentCommands.instance.transferAssignments()
    //SubmissionCommands.instance.generateSubmissionFeedbackTable()
    //SubmissionCommands.instance.cleanSubmissionFeedbackTable()
//    SubmissionCommands.instance.createRandomSample(
//        Path.of("C:\\Users\\Cagatay\\Documents\\Studium\\MSE\\P9\\full-cleaned-evaluations"),
//        Path.of("C:\\Users\\Cagatay\\Documents\\Studium\\MSE\\P9\\random-sample.csv")
//    )
//    SubmissionCommands.instance.mergeCsvFiles(
//        listOf(
//            Path.of("C:\\Users\\Cagatay\\Documents\\Studium\\MSE\\P9\\full-cleaned-evaluations-1.csv"),
//            Path.of("C:\\Users\\Cagatay\\Documents\\Studium\\MSE\\P9\\full-cleaned-evaluations-2.csv"),
//            Path.of("C:\\Users\\Cagatay\\Documents\\Studium\\MSE\\P9\\full-cleaned-evaluations-3.csv")
//    ),
//        Path.of("C:\\Users\\Cagatay\\Documents\\Studium\\MSE\\P9\\full-cleaned-evaluations.csv")
//    )
//    SubmissionCommands.instance.transferSelectedSubmissions(
//        listOf(
//            SubmissionTransferKey.from(
//                "09c1a7d7-2746-4ea5-902b-79afe0ea1c1b", setOf("parking", "labyrinth", "visualizer")
//            ),
//            SubmissionTransferKey.from(
//                "09df514f-b9f3-4479-b174-e5f759a561bf", setOf("sortalgs")
//            ),
//            SubmissionTransferKey.from(
//                "0e4a3299-ef49-4709-9ccb-9cd4e3f3da50", setOf("tripleseqsearch")
//            ),
//            SubmissionTransferKey.from(
//                "0eddf19a-cd3c-4d21-ae77-5f2dec6c2ccb", setOf("binsearchfirst")
//            ),
//            SubmissionTransferKey.from(
//                "148c9c38-9ac4-4078-bf55-376232dc7d75", setOf("binsearchfirst")
//            ),
//            SubmissionTransferKey.from(
//                "0488a907-f907-4421-a7db-105757d766a9", setOf("swissmap")
//            ),
//            SubmissionTransferKey.from(
//                "0640339c-9d31-4254-aa12-a00034d9ba61", setOf("commitactivity", "mapcoloring")
//            ),
//            SubmissionTransferKey.from(
//                "008ede1d-a08b-4d93-85c1-89ede4d36204", setOf("fractal")
//            ),
//            SubmissionTransferKey.from(
//                "00d13516-dfc6-4398-881e-17e5112a28cb", setOf("smarthome")
//            ),
//            SubmissionTransferKey.from(
//                "0137ee46-af23-4be7-b1f3-35a743434db2", setOf("floodfill")
//            ),
//            SubmissionTransferKey.from(
//                "07400bd9-f6b5-4d75-8b25-06a4570d3692", setOf("eratosthenes")
//            ),
//            SubmissionTransferKey.from(
//                "094ae0fe-9dd9-424b-b885-3b2ba3d8db06", setOf("lotto")
//            ),
//            SubmissionTransferKey.from(
//                "09717186-b0ff-4479-8ffa-d5e9e465ebbb", setOf("stepstats")
//            ),
//            SubmissionTransferKey.from(
//                "251d4a38-a30d-44f8-9efa-1fa5f0563007", setOf("smarthome")
//            ),
//            SubmissionTransferKey.from(
//                "0cb146d4-4e3c-425d-b190-e8e8320fa4b8", setOf("fractal")
//            ),
//            SubmissionTransferKey.from(
//                "0e48918a-720f-45b7-be57-427719601bd2", setOf("smartcampus")
//            ),
//            SubmissionTransferKey.from(
//                "0fe3e319-90cd-4721-aa85-4488aba74ebc", setOf("textanalysis")
//            )
//        )
//    )

    databases.close()
}