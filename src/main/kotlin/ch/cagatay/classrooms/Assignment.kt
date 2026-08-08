package ch.cagatay.classrooms
import java.time.OffsetDateTime
import java.util.UUID

data class Assignment(
    val id: UUID,
    val name: String,
    val type: String,
    val classroomId: String,
    val autoArchive: Boolean,
    val dueDate: OffsetDateTime?,
    val status: String,
    val gitlabRepositoryTemplateId: String,
    val gitlabRepositorySolutionId: String?,
    val gitlabRepositoryTestsId: String?,
    val evaluationSteps: Array<String>,
    val gitlabGroupId: Long,
)
