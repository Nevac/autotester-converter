package org.example

import kotlin.test.Test
import kotlin.test.assertEquals

class MainTest {
    @Test
    fun `greeting addresses the supplied name`() {
        assertEquals("Hello, Kotlin!", greeting("Kotlin"))
    }
}
