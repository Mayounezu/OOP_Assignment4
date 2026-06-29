package com.mysteryticking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ConditionalEntity} (bonus).
 */
class ConditionalEntityTest {

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

    @Test
    void nullPredicateThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new ConditionalEntity(null, "msg"));
    }

    @Test
    void nullMessageThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new ConditionalEntity(t -> true, null));
    }

    @Test
    void printsWhenPredicateTrue() {
        ConditionalEntity ce = new ConditionalEntity(t -> t == 5, "Hello!");
        ce.onTick(5);
        assertEquals("Hello!" + NL, captured());
    }

    @Test
    void silentWhenPredicateFalse() {
        ConditionalEntity ce = new ConditionalEntity(t -> t == 5, "Hello!");
        ce.onTick(1);
        ce.onTick(2);
        ce.onTick(3);
        assertEquals("", captured());
    }

    @Test
    void divisibilityPredicatePrintsOnCorrectTicks() {
        ConditionalEntity ce = new ConditionalEntity(t -> t % 7 == 0, "Dobby is free!");
        for (int t = 1; t <= 21; t++) ce.onTick(t);
        // Fires at t=7, t=14, t=21 — exactly 3 times
        String expected = "Dobby is free!" + NL
                        + "Dobby is free!" + NL
                        + "Dobby is free!" + NL;
        assertEquals(expected, captured());
    }

    @Test
    void specificTicksPredicatePrintsExactly() {
        ConditionalEntity ce = new ConditionalEntity(
                t -> t == 13 || t == 42, "Avada Kedavra!");
        for (int t = 1; t <= 50; t++) ce.onTick(t);
        String expected = "Avada Kedavra!" + NL + "Avada Kedavra!" + NL;
        assertEquals(expected, captured());
    }
}
