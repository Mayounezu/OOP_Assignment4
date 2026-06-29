package com.mysteryticking;

/**
 * Observer interface for tick events.
 * Being a functional interface allows observers to be supplied as lambdas.
 */
@FunctionalInterface
public interface TickListener {
    /**
     * Called on every tick of the simulation.
     *
     * @param tick the current tick number (1-based)
     */
    void onTick(int tick);
}
