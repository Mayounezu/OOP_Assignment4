package com.mysteryticking;

/**
 * Immutable value object representing one step in a state machine's cycle.
 *
 * <p>Implemented as a Java record to guarantee immutability and to provide
 * auto-generated {@code equals}, {@code hashCode}, and {@code toString}.
 *
 * @param name the string printed when this state is entered; must not be null
 * @param cost the number of ticks this state occupies within one cycle; must be &ge; 1
 */
public record State(String name, int cost) {

    /**
     * Compact canonical constructor that validates invariants.
     *
     * @throws IllegalArgumentException if {@code name} is null or {@code cost} &lt; 1
     */
    public State {
        if (name == null) {
            throw new IllegalArgumentException("State name must not be null");
        }
        if (cost < 1) {
            throw new IllegalArgumentException(
                    "State cost must be >= 1, but was: " + cost);
        }
    }
}
