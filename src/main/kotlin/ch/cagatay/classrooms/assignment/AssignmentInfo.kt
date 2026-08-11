package ch.cagatay.classrooms.assignment

data class AssignmentInfo(
    val assignment: Assignment,
    val exercises: Map<String, String>,
    val solutions: Map<String, String>
)