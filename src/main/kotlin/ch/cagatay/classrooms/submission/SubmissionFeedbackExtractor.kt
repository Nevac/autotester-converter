package ch.cagatay.classrooms.submission

import ch.cagatay.converter.evaluation.EvaluationContainer

class SubmissionFeedbackExtractor {
    companion object {
        fun extract(evaluationContainer: EvaluationContainer): Map<String, String> {
            return SubmissionFeedbackExtractor().extract(evaluationContainer)
        }
    }

    fun extract(evaluationContainer: EvaluationContainer): Map<String, String> {
        val feedback = evaluationContainer.llmResult.value.feedback;
        return feedback.split("Exercise: ").associate {
            it.substringBefore("\n") to it.substringAfter("\n")
        }
    }
}