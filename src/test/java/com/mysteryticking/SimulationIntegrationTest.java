package com.mysteryticking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test verifying the full simulation output for countdown=24
 * matches the exact expected output from the assignment spec.
 */
class SimulationIntegrationTest {

    private static final String NL = System.lineSeparator();

    private ByteArrayOutputStream baos;
    private PrintStream originalOut;

    @BeforeEach
    void redirectOut() {
        baos = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(baos, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreOut() {
        System.setOut(originalOut);
    }

    private String captured() {
        return baos.toString(StandardCharsets.UTF_8);
    }

    // -----------------------------------------------------------------------
    // Exact-output test (spec reference: sample output, countdown=24)
    // -----------------------------------------------------------------------

    @Test
    void countdown24MatchesExpectedOutput() {
        Main.runSimulation(24);

        String expected =
                "Snape"         + NL +
                "Snape"         + NL +
                "Severus Snape" + NL +
                                  NL +  // blank line at t=8
                "Snape"         + NL +
                "Snape"         + NL +
                "Severus Snape" + NL +
                "Dumbledore"    + NL +
                                  NL +  // blank line at t=16
                "Snape"         + NL +
                "Snape"         + NL +
                "Severus Snape" + NL +
                "Dumbledore"    + NL +
                                  NL +  // blank line at t=24
                "BOOM!"         + NL;

        assertEquals(expected, captured());
    }

    // -----------------------------------------------------------------------
    // Structural / property tests
    // -----------------------------------------------------------------------

    @Test
    void outputAlwaysEndsWithBoom() {
        Main.runSimulation(5);
        assertTrue(captured().endsWith("BOOM!" + NL));
    }

    @Test
    void snapeFirstTickPrintsSnape() {
        Main.runSimulation(1);
        // t=1: Snape enters state 0 → "Snape"
        assertEquals("Snape" + NL + "BOOM!" + NL, captured());
    }

    @Test
    void cycleSeparatorProducesBlankLineAtMultiplesOf8() {
        Main.runSimulation(8);
        String output = captured();
        // The blank line appears between "Severus Snape" and "BOOM!"
        // i.e. output contains two consecutive NL sequences somewhere
        assertTrue(output.contains(NL + NL),
                "Expected a blank line (double newline) at t=8");
    }

    @Test
    void dumbledoreFirstAppearsAtTick16() {
        Main.runSimulation(16);
        String output = captured();
        // Dumbledore must appear exactly once (enters state 0 at t=16)
        assertTrue(output.contains("Dumbledore"),
                "Dumbledore should appear by tick 16");
        assertEquals(1, countOccurrences(output, "Dumbledore"),
                "Dumbledore should appear exactly once in 16 ticks");
    }

    @Test
    void snapeRepeatsEveryCycle() {
        // In 16 ticks Snape completes 2 full cycles → 6 state-entry prints
        Main.runSimulation(16);
        String output = captured();
        // "Snape" (the string) appears 4 times, "Severus Snape" 2 times
        assertEquals(4, countOccurrences(output, "Snape\n".replace("\n", NL)));
        assertEquals(2, countOccurrences(output, "Severus Snape"));
    }

    // -----------------------------------------------------------------------
    // Helper
    // -----------------------------------------------------------------------

    private static int countOccurrences(String text, String sub) {
        int count = 0;
        int idx = 0;
        while ((idx = text.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }
}
