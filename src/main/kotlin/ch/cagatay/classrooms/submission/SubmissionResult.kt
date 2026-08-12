package ch.cagatay.classrooms.submission

import ch.cagatay.classrooms.assignment.Assignment
import java.util.UUID

class SubmissionResult(
    val evaluationId: UUID,
    val assignment: Assignment,
    val exercises: Map<String, ExerciseResult>,
    val templates: Map<String, String>
)