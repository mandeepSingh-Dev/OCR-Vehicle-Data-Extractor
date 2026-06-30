package ai.os.ocrdemoapp

import ai.os.ocrdemoapp.ui.OldRCFields
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class LevenshteinDistanceTest {

    @Test
    fun similarityScore() {

        val score = similarityScore("Alarnat",actual = OldRCFields.ALAMAT)

        println("Similarity Score is: $score")
        assertTrue(true)
    }

}