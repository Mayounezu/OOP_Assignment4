package com.mysteryticking;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FiniteEntity} (bonus).
 */
class FiniteEntityTest {

    @Test
    void invalidMaxFiresThrows() {
        PipeBomb bomb = new PipeBomb(10);
        TickListener tl = t -> {};
        assertThrows(IllegalArgumentException.class,
                () -> new FiniteEntity(tl, bomb, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new FiniteEntity(tl, bomb, -1));
    }

    @Test
    void firesExactlyMaxTimesThenStops() {
        PipeBomb bomb = new PipeBomb(10);
        List<Integer> fired = new ArrayList<>();
        TickListener delegate = fired::add;
        FiniteEntity fe = new FiniteEntity(delegate, bomb, 3);
        bomb.register(fe);
        bomb.start();
        // Delegate should have been called exactly 3 times
        assertEquals(3, fired.size());
    }

    @Test
    void delegateReceivesCorrectTicks() {
        PipeBomb bomb = new PipeBomb(5);
        List<Integer> fired = new ArrayList<>();
        bomb.register(new FiniteEntity(fired::add, bomb, 2));
        bomb.start();
        assertEquals(List.of(1, 2), fired);
    }

    @Test
    void unregistersItselfAfterMaxFires() {
        PipeBomb bomb = new PipeBomb(5);
        List<Integer> calls = new ArrayList<>();
        FiniteEntity fe = new FiniteEntity(calls::add, bomb, 2);
        bomb.register(fe);
        bomb.start();
        // Should have been called at t=1 and t=2 only
        assertEquals(2, calls.size());
        assertEquals(1, (int) calls.get(0));
        assertEquals(2, (int) calls.get(1));
    }
}
