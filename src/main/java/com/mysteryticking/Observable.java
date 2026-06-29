package com.mysteryticking;

/**
 * Subject interface for the Observer pattern.
 * Any observable subject must support listener registration,
 * deregistration, and notification.
 */
public interface Observable {

    /**
     * Register a listener to be notified on each tick.
     *
     * @param listener the listener to add; registration order determines
     *                 notification order within each tick
     */
    void register(TickListener listener);

    /**
     * Notify all registered listeners of the given tick.
     *
     * @param tick the current tick number
     */
    void notifyListeners(int tick);

    /**
     * Remove a previously registered listener.
     *
     * @param listener the listener to remove
     * @return {@code true} if the listener was found and removed;
     *         {@code false} if it was not registered
     */
    boolean unregister(TickListener listener);
}
