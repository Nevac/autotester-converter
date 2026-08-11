package ch.cagatay.evaluation

import ch.cagatay.autofeedback.Evaluation
import ch.cagatay.autofeedback.Result
import ch.cagatay.classrooms.assignment.Assignment
import ch.cagatay.classrooms.evaluation.AutofeedbackEvaluation
import ch.cagatay.classrooms.student.exercise.assignment.StudentExerciseAssignment

data class EvaluationContainer(
    val evaluation: Evaluation,
    val llmResult: Result,
    val autofeedbackEvaluation: AutofeedbackEvaluation,
    val assignment: Assignment,
    val studentExerciseAssignment: StudentExerciseAssignment
)