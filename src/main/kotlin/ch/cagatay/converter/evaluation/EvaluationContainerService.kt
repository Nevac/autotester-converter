package ch.cagatay.converter.evaluation

import ch.cagatay.autofeedback.Evaluation
import ch.cagatay.autofeedback.EvaluationRepository
import ch.cagatay.autofeedback.Result
import ch.cagatay.autofeedback.ResultRepository
import ch.cagatay.classrooms.assignment.AssignmentRepository
import ch.cagatay.classrooms.evaluation.AutofeedbackEvaluation
import ch.cagatay.classrooms.evaluation.AutofeedbackEvaluationRepository
import ch.cagatay.classrooms.exercise.assignment.ExerciseAssignmentRepository
import ch.cagatay.classrooms.student.exercise.assignment.AssignmentStudentKey
import ch.cagatay.classrooms.student.exercise.assignment.StudentExerciseAssignmentRepository
import java.util.UUID

class EvaluationContainerService private constructor() {
    val evaluationRepository = EvaluationRepository.instance
    val autofeedbackEvaluationRepository = AutofeedbackEvaluationRepository.instance
    val resultRepository = ResultRepository.instance
    val assignmentRepository = AssignmentRepository.instance
    val exerciseAssignmentRepository = ExerciseAssignmentRepository.instance
    val studentExerciseAssignmentRepository = StudentExerciseAssignmentRepository.instance

    companion object {
        val instance = EvaluationContainerService()
    }

    fun getEvaluationContainersByIds(ids: List<UUID>): List<EvaluationContainer> {
        val evaluations = evaluationRepository.findByIds(ids)
        val autofeedbackEvaluations = autofeedbackEvaluationRepository.findByIds(ids)
        val results = resultRepository.findByEvaluationIdsLlmResults(ids)
        return buildEvaluationContainer(
            evaluations,
            autofeedbackEvaluations,
            results
        )
    }

    fun getAllEvaluationContainers(): List<EvaluationContainer> {
        val evaluations = evaluationRepository.findAllSuccessful()
        val autofeedbackEvaluations = autofeedbackEvaluationRepository.findAll()
        val results = resultRepository.findAllLlmResults()
        return buildEvaluationContainer(
            evaluations,
            autofeedbackEvaluations,
            results
        )
    }

    private fun buildEvaluationContainer(
        evaluations: Map<UUID, Evaluation>,
        autofeedbackEvaluations: Map<UUID, AutofeedbackEvaluation>,
        results: Map<UUID, Result>
    ): List<EvaluationContainer> {
        val assignmentIds = autofeedbackEvaluations.values.map { it.assignmentId }.toSet().toList()
        val assignments = assignmentRepository.findByIds(assignmentIds)
        val exerciseAssignments = exerciseAssignmentRepository.findByIds(assignmentIds)
        val studentExerciseAssignment = studentExerciseAssignmentRepository.findByAssignmentAndStudentKeys(
            autofeedbackEvaluations.values.map {
                AssignmentStudentKey(it.assignmentId, it.studentName)
            }.toList()
        )

        return evaluations.filter {
            val evalId = it.key
            results.containsKey(evalId) && autofeedbackEvaluations.containsKey(evalId)
        }.map {
            val evalId = it.key
            val eval = it.value
            val autofeedbackEvaluation = autofeedbackEvaluations[evalId]!!
            val assignmentId = autofeedbackEvaluation.assignmentId

            EvaluationContainer(
                eval,
                results[evalId]!!,
                autofeedbackEvaluation,
                assignments[assignmentId]!!,
                exerciseAssignments[assignmentId]!!,
                studentExerciseAssignment[AssignmentStudentKey(assignmentId, autofeedbackEvaluation.studentName)]!!
            )
        }
    }
}