package com.mysteryticking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Concrete observer that models an entity cycling through a fixed sequence of
 * {@link State}s, printing a state's name exactly once — the moment it enters
 * that state.
 *
 * <p><b>Tick mechanics:</b>
 * <pre>
 *   posInCycle = (tick - startTick) mod cycleLength
 * </pre>
 * The entity has just entered state {@code i} iff {@code posInCycle} equals
 * the sum of the costs of states {@code 0, 1, ..., i-1}.
 */
public class StateMachineEntity implements TickListener {

    private final String entityName;
    private final int startTick;
    private final List<State> states;

    /** Pre-computed total length of one cycle (sum of all state costs). */
    private final int cycleLength;

    /**
     * Pre-computed cumulative costs: {@code cumulativeCosts[i]} is the position
     * in the cycle at which state {@code i} is entered.
     * cumulativeCosts[0] = 0, cumulativeCosts[1] = states[0].cost(), etc.
     */
    private final int[] cumulativeCosts;

    /**
     * Constructs a StateMachineEntity.
     *
     * @param entityName name used in {@link #toString()}
     * @param startTick  the entity is silent for all ticks strictly before this value
     * @param states     the cyclic state sequence; at least one state is required
     * @throws IllegalArgumentException if no states are provided
     */
    public StateMachineEntity(String entityName, int startTick, State... states) {
        if (states == null || states.length == 0) {
            throw new IllegalArgumentException(
                    "StateMachineEntity requires at least one State");
        }
        this.entityName = entityName;
        this.startTick = startTick;

        // Defensive copy
        List<State> copy = new ArrayList<>();
        for (State s : states) {
            copy.add(s);
        }
        this.states = Collections.unmodifiableList(copy);

        // Pre-compute cumulative costs and cycle length
        this.cumulativeCosts = new int[states.length];
        int sum = 0;
        for (int i = 0; i < states.length; i++) {
            cumulativeCosts[i] = sum;
            sum += states[i].cost();
        }
        this.cycleLength = sum;
    }

    /**
     * Called on every tick. If {@code tick < startTick}, the entity is silent.
     * Otherwise, checks whether the entity has just entered a new state and, if
     * so, prints the state's name (unless the name is empty).
     *
     * @param tick the current tick number
     */
    @Override
    public void onTick(int tick) {
        if (tick < startTick) {
            return; // entity is silent before startTick
        }

        int posInCycle = (tick - startTick) % cycleLength;

        // Check if posInCycle coincides with the start of any state
        for (int i = 0; i < states.size(); i++) {
            if (posInCycle == cumulativeCosts[i]) {
                String name = states.get(i).name();
                if (!name.isEmpty()) {
                    System.out.println(name);
                }
                return; // at most one state can start at this position
            }
        }
        // posInCycle is mid-state: do nothing
    }

    /**
     * Returns the entity's name.
     *
     * @return {@code entityName}
     */
    @Override
    public String toString() {
        return entityName;
    }
}
