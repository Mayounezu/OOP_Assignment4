package com.mysteryticking;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link State} record.
 */
class StateTest {

    @Test
    void validStateCreation() {
        State s = new State("Snape", 2);
        assertEquals("Snape", s.name());
        assertEquals(2, s.cost());
    }

    @Test
    void nullNameThrows() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new State(null, 1));
        assertTrue(ex.getMessage().contains("null"));
    }

    @Test
    void zeroCostThrows() {
        assertThrows(IllegalArgumentException.class, () -> new State("X", 0));
    }

    @Test
    void negativeCostThrows() {
        assertThrows(IllegalArgumentException.class, () -> new State("X", -5));
    }

    @Test
    void costOneIsValid() {
        State s = new State("A", 1);
        assertEquals(1, s.cost());
    }

    @Test
    void emptyNameIsValid() {
        // Empty string is not null — should be accepted
        State s = new State("", 1);
        assertEquals("", s.name());
    }

    @Test
    void equalityByValue() {
        State a = new State("Snape", 2);
        State b = new State("Snape", 2);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void inequalityDifferentName() {
        assertNotEquals(new State("A", 1), new State("B", 1));
    }

    @Test
    void inequalityDifferentCost() {
        assertNotEquals(new State("A", 1), new State("A", 2));
    }
}
