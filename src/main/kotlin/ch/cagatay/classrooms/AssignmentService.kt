package ch.cagatay.classrooms

import ch.cagatay.git.GitlabService
import java.util.UUID

class AssignmentService {
    val repository = AssignmentRepository.instance
    val gitlabService = GitlabService.instance

    companion object {
        val instance = AssignmentService()
    }

    fun generateAssignmentTextsForClassroom(classroomId: UUID): Map<UUID, AssignmentInfo> {
        val assignments = repository.findAssignmentsByClassroom(classroomId)

        val map = mutableMapOf<UUID, AssignmentInfo>()
        for(assignment in assignments) {
            val exerciseTexts = ExerciseExtractor.extract(
                gitlabService.checkoutLatest(
                    assignment.gitlabRepositoryTemplateId
                )
            )

            var solutionTexts: MutableMap<String, String> = mutableMapOf();
            if(assignment.gitlabRepositorySolutionId != null) {
                solutionTexts = SolutionExtractor.extract(
                    gitlabService.checkoutLatest(
                        assignment.gitlabRepositorySolutionId
                    )
                )
            }

            map[assignment.id] = AssignmentInfo(assignment, exerciseTexts, solutionTexts)
        }
        return map
    }
}