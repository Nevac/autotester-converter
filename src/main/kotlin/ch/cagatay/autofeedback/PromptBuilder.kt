package ch.cagatay.autofeedback

import ch.cagatay.autofeedbacktesttool.attempt.Attempt

object PromptBuilder {

    fun build(attempt: Attempt): String {
        return build(
            attempt.exercise.task,
            attempt.exercise.solution,
            attempt.attempt,
            listOf()
        )
    }

    fun build(
        exercise: String?,
        solution: String?,
        submission: String?,
        ragDocument: List<String>?
    ): String {
        val instruction: String = """
                You are a programming tutor.
                You will be provided with the exercise ${if (solution != null) ", an example solution to the exercise " else ""} and the students attempt to solve the exercise.
                Your task is to provide feedback for the student's attempt to solve the given exercise. You will be provided with additional documents for domain knowledge with a RAG.
                Speak in you form.
                ${if (solution != null) "The student’s attempt doesn’t need to match exactly with the example solution since there are many valid solutions, it serves as a one shot example.\n" else ""}
                Your feedback mustn’t contain a direct solution, it should point out issues such that the student can try to work out a solution themselves.
                Dont greet or provide a summary at the end. Structure your response always in those three categories only. For every point in a category, create a new line starting with a dash.
                
                1. Correctness
                Description:
                - If there is anything violating the excercise requirements point them out here.
                - Do not mention any unused or unrelated code bits in this category
                - Do not propose code style or suggestions here
                - Do not mention any potential issues not explicitely adressed in the requirements in this category, put them in Code Style instead. Example: Unexpected inputs, overflows, exception handling
                - If there are no issues in this category, you must leave this category blank
                - Do not mention correct things
                
                2. Suggestion
                Description:
                - For every issue found in the correctness category, try to provide a hint without providing a direct solution.
                - Do not propose correctnes or code style here
                - If there are no issues in this category, leave it blank
                
                3. Code Style:
                Description:
                - Check for any major code style issues and provide a recommendation
                - Mention if there are unused or unrelated code bits in the attempt
                - Do not propose correctness or suggestion here
                - If there are no issues in this category, leave it blank
                
                """
            .trimIndent()

        var ragDocuments = ""
        if (ragDocument != null) {
            ragDocuments = """
                    --------
                    Additional RAG Documents:
                    $ragDocument
                    
                    """
                .trimIndent()
        }

        return """
                Instruction:
                $instruction
                
                --------
                Exercise:
                $exercise
                
                ${if (solution != null) "--------\nExample Solution:" else ""}
                ${if (solution != null) solution else ""}
                
                --------
                Attempt:
                $submission
                
                $ragDocuments
                
                """
            .trimIndent()
    }
}
