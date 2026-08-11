package ch.cagatay.classrooms.submission

import ch.cagatay.classrooms.extractors.SubmissionExtractor
import ch.cagatay.classrooms.extractors.SubmissionExtractorResult
import ch.cagatay.evaluation.EvaluationContainer
import ch.cagatay.git.GitlabService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

class SubmissionFetcherService private constructor() {
    val gitlabService = GitlabService.instance

    companion object {
        val instance = SubmissionFetcherService()
    }

    suspend fun generateSubmissionResults(
        evaluationContainers: List<EvaluationContainer>
    ): Map<UUID, SubmissionResult> = coroutineScope {
        val total = evaluationContainers.size
        val done = AtomicInteger(0)
        val requestDispatcher = Dispatchers.IO.limitedParallelism(10)

        evaluationContainers
            .map { container ->
                async(requestDispatcher) {
                    try {
                        val exerciseResult = generateExerciseResult(container)

                        container.evaluation.id to SubmissionResult(
                            container.evaluation.id,
                            container.assignment,
                            exerciseResult
                        )
                    } catch (exception: Exception) {
                        null
                    } finally {
                        val completed = done.incrementAndGet()
                        val percentage =
                            if (total == 0) 100 else completed * 100 / total

                        println("Fetched $completed/$total ($percentage%) submissions")
                    }
                }
            }
            .awaitAll()
            .filterNotNull()
            .toMap()
    }

    private fun generateExerciseResult(evaluationContainer: EvaluationContainer): Map<String, ExerciseResult> {
        val extractedSubmissions = extractSubmissions(evaluationContainer)
        val feedbacks = SubmissionFeedbackExtractor.extract(evaluationContainer)
        return extractedSubmissions.submission!!.filter {
            feedbacks.containsKey(it.key) && it.value.submission !== null
        }.map {
            it.key to ExerciseResult(
                evaluationContainer.assignment.name + "-" + it.key + "-" + evaluationContainer.evaluation.id,
                it.value.submission!!,
                feedbacks[it.key]!!,
                it.value.isIgnored
            )
        }.toMap()
    }

    private fun extractSubmissions(evaluationContainer: EvaluationContainer): SubmissionExtractorResult {
        return SubmissionExtractor.extract(
            gitlabService.checkout(
                evaluationContainer.studentExerciseAssignment.gitlabProjectId.toString(),
                evaluationContainer.autofeedbackEvaluation.commit
            ),
            PreviousExerciseFeedbacks.noPreviousLlmResult()
        )
    }
}