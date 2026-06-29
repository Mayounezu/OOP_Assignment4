package com.mysteryticking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link StateMachineEntity}.
 */
class StateMachineEntityTest {

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
    // Constructor validation
    // -----------------------------------------------------------------------

    @Test
    void noStatesThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new StateMachineEntity("X", 1));
    }

    @Test
    void nullStatesArrayThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new StateMachineEntity("X", 1, (State[]) null));
    }

    @Test
    void toStringReturnsEntityName() {
        StateMachineEntity e = new StateMachineEntity("Snape", 1, new State("S", 2));
        assertEquals("Snape", e.toString());
    }

    // -----------------------------------------------------------------------
    // Tick silence before startTick
    // -----------------------------------------------------------------------

    @Test
    void silentBeforeStartTick() {
        StateMachineEntity e = new StateMachineEntity("E", 5, new State("Hello", 1));
        for (int t = 1; t < 5; t++) e.onTick(t);
        assertEquals("", captured());
    }

    @Test
    void activatesExactlyAtStartTick() {
        StateMachineEntity e = new StateMachineEntity("E", 3, new State("GO", 1));
        e.onTick(2); // silent
        e.onTick(3); // entry
        assertEquals("GO" + NL, captured());
    }

    // -----------------------------------------------------------------------
    // State entry / mid-state logic
    // -----------------------------------------------------------------------

    @Test
    void midStateIsSilent() {
        // ("Hello", 3): entered at t=1, mid at t=2, mid at t=3
        StateMachineEntity e = new StateMachineEntity("E", 1, new State("Hello", 3));
        e.onTick(1); // entry → print
        e.onTick(2); // mid   → silent
        e.onTick(3); // mid   → silent
        assertEquals("Hello" + NL, captured());
    }

    @Test
    void cyclesCorrectly() {
        // cycle: ("A",1), ("B",1) → A at t=1,3,5…  B at t=2,4,6…
        StateMachineEntity e = new StateMachineEntity("E", 1,
                new State("A", 1), new State("B", 1));
        for (int t = 1; t <= 4; t++) e.onTick(t);
        assertEquals("A" + NL + "B" + NL + "A" + NL + "B" + NL, captured());
    }

    @Test
    void emptyStateNameProducesNoOutput() {
        StateMachineEntity e = new StateMachineEntity("E", 1,
                new State("", 2),
                new State("Visible", 2));
        e.onTick(1); // enters ("", 2) → no output
        e.onTick(3); // enters ("Visible", 2) → print
        assertEquals("Visible" + NL, captured());
    }

    // -----------------------------------------------------------------------
    // Snape first-cycle verification (matches spec trace)
    // -----------------------------------------------------------------------

    @Test
    void snapeFirstCycleMatchesSpec() {
        // Snape: startTick=1, ("Snape",2), ("Snape",2), ("Severus Snape",4), cycleLen=8
        // t=1→Snape  t=3→Snape  t=5→Severus Snape  t=2,4,6,7,8→silent
        StateMachineEntity snape = new StateMachineEntity("Snape", 1,
                new State("Snape", 2),
                new State("Snape", 2),
                new State("Severus Snape", 4));
        for (int t = 1; t <= 8; t++) snape.onTick(t);
        assertEquals("Snape" + NL + "Snape" + NL + "Severus Snape" + NL, captured());
    }

    // -----------------------------------------------------------------------
    // Defensive copy
    // -----------------------------------------------------------------------

    @Test
    void defensiveCopyPreventsExternalMutation() {
        State[] arr = { new State("Original", 1) };
        StateMachineEntity e = new StateMachineEntity("E", 1, arr);
        arr[0] = new State("Mutated", 1); // mutate after construction
        e.onTick(1);
        assertEquals("Original" + NL, captured()); // still prints original
    }
}
