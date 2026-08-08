package ch.cagatay.classrooms

data class AssignmentInfo(
    val assignment: Assignment,
    val exercises: Map<String, String>,
    val solutions: Map<String, String>
)