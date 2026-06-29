package com.mysteryticking;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete subject in the Observer pattern.
 * Counts down from {@code countdown} ticks to zero, notifying all registered
 * {@link TickListener}s on each tick. After the loop it prints {@code BOOM!}.
 */
public class PipeBomb implements Observable {

    private final int countdown;
    private final List<TickListener> listeners;

    /**
     * Constructs a PipeBomb with the given countdown length.
     *
     * @param countdown number of ticks before the bomb explodes; must be &ge; 1
     * @throws IllegalArgumentException if {@code countdown} &lt; 1
     */
    public PipeBomb(int countdown) {
        if (countdown < 1) {
            throw new IllegalArgumentException(
                    "countdown must be >= 1, but was: " + countdown);
        }
        this.countdown = countdown;
        this.listeners = new ArrayList<>();
    }

    /**
     * Appends {@code listener} to the registration list.
     * Registration order determines notification order within each tick.
     *
     * @param listener the listener to add
     */
    @Override
    public void register(TickListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes {@code listener} from the registration list.
     *
     * @param listener the listener to remove
     * @return {@code true} if found and removed; {@code false} otherwise
     */
    @Override
    public boolean unregister(TickListener listener) {
        return listeners.remove(listener);
    }

    /**
     * Calls {@link TickListener#onTick(int)} on every registered listener in
     * registration order.
     *
     * @param tick the current tick number
     */
    @Override
    public void notifyListeners(int tick) {
        // Iterate over a snapshot to allow safe unregistration during notification
        List<TickListener> snapshot = new ArrayList<>(listeners);
        for (TickListener listener : snapshot) {
            listener.onTick(tick);
        }
    }

    /**
     * Runs the tick loop from tick 1 to {@code countdown} (inclusive),
     * calling {@link #notifyListeners(int)} on each iteration.
     * Prints {@code BOOM!} to standard output after the loop completes.
     */
    public void start() {
        for (int tick = 1; tick <= countdown; tick++) {
            notifyListeners(tick);
        }
        System.out.println("BOOM!");
    }
}
