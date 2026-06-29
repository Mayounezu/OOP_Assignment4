package com.mysteryticking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PipeBomb}.
 */
class PipeBombTest {

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

    // -----------------------------------------------------------------------
    // Constructor validation
    // -----------------------------------------------------------------------

    @Test
    void zeroCountdownThrows() {
        assertThrows(IllegalArgumentException.class, () -> new PipeBomb(0));
    }

    @Test
    void negativeCountdownThrows() {
        assertThrows(IllegalArgumentException.class, () -> new PipeBomb(-1));
    }

    @Test
    void countdownOneIsValid() {
        assertDoesNotThrow(() -> new PipeBomb(1));
    }

    // -----------------------------------------------------------------------
    // Tick loop
    // -----------------------------------------------------------------------

    @Test
    void countdownOneNotifiesTickOneThenBoom() {
        PipeBomb bomb = new PipeBomb(1);
        List<Integer> ticks = new ArrayList<>();
        bomb.register(ticks::add);
        bomb.start();
        assertEquals(List.of(1), ticks);
        assertTrue(baos.toString(StandardCharsets.UTF_8).contains("BOOM!"));
    }

    @Test
    void loopDeliversTicksFromOneToCountdown() {
        PipeBomb bomb = new PipeBomb(5);
        List<Integer> received = new ArrayList<>();
        bomb.register(received::add);
        bomb.start();
        assertEquals(List.of(1, 2, 3, 4, 5), received);
    }

    @Test
    void startPrintsBoomAfterLoop() {
        PipeBomb bomb = new PipeBomb(3);
        bomb.start();
        assertTrue(baos.toString(StandardCharsets.UTF_8).endsWith("BOOM!" + NL));
    }

    // -----------------------------------------------------------------------
    // Registration order
    // -----------------------------------------------------------------------

    @Test
    void notificationOrderMatchesRegistration() {
        PipeBomb bomb = new PipeBomb(1);
        List<String> order = new ArrayList<>();
        bomb.register(t -> order.add("A"));
        bomb.register(t -> order.add("B"));
        bomb.register(t -> order.add("C"));
        bomb.notifyListeners(1);
        assertEquals(List.of("A", "B", "C"), order);
    }

    // -----------------------------------------------------------------------
    // Unregister (bonus)
    // -----------------------------------------------------------------------

    @Test
    void unregisterReturnsTrueWhenFound() {
        PipeBomb bomb = new PipeBomb(5);
        TickListener listener = t -> {};
        bomb.register(listener);
        assertTrue(bomb.unregister(listener));
    }

    @Test
    void unregisterReturnsFalseWhenNotFound() {
        PipeBomb bomb = new PipeBomb(5);
        assertFalse(bomb.unregister(t -> {}));
    }

    @Test
    void unregisteredListenerReceivesNoMoreTicks() {
        PipeBomb bomb = new PipeBomb(5);
        List<Integer> received = new ArrayList<>();
        TickListener listener = received::add;
        bomb.register(listener);
        bomb.unregister(listener);
        bomb.start();
        assertTrue(received.isEmpty());
    }

    @Test
    void multipleListenersAllReceiveTicks() {
        PipeBomb bomb = new PipeBomb(3);
        List<Integer> a = new ArrayList<>(), b = new ArrayList<>();
        bomb.register(a::add);
        bomb.register(b::add);
        bomb.start();
        assertEquals(List.of(1, 2, 3), a);
        assertEquals(List.of(1, 2, 3), b);
    }

    @Test
    void unregisterDuringNotificationIsSafe() {
        // FiniteEntity unregisters itself mid-notification; must not throw
        PipeBomb bomb = new PipeBomb(5);
        List<Integer> fired = new ArrayList<>();
        bomb.register(new FiniteEntity(fired::add, bomb, 2));
        assertDoesNotThrow(bomb::start);
        assertEquals(2, fired.size()); // fired exactly twice
    }
}
