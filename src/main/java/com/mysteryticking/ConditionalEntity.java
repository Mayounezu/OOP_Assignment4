package com.mysteryticking;

import java.util.function.Predicate;

/**
 * BONUS: A {@link TickListener} that replaces the fixed {@code startTick}
 * mechanism with a {@link Predicate}{@code <Integer>}. On each tick, if the
 * predicate returns {@code true}, the entity prints its message.
 *
 * <p>Examples:
 * <pre>
 *   // Prints on every tick divisible by 7.
 *   bomb.register(new ConditionalEntity(t -> t % 7 == 0, "Dobby is free!"));
 *
 *   // Prints on ticks 13 and 42.
 *   bomb.register(new ConditionalEntity(t -> t == 13 || t == 42, "Avada Kedavra!"));
 * </pre>
 */
public class ConditionalEntity implements TickListener {

    private final Predicate<Integer> predicate;
    private final String message;

    /**
     * Constructs a ConditionalEntity.
     *
     * @param predicate the condition evaluated on each tick number
     * @param message   the string printed when the predicate returns {@code true}
     * @throws IllegalArgumentException if {@code predicate} or {@code message} is null
     */
    public ConditionalEntity(Predicate<Integer> predicate, String message) {
        if (predicate == null) {
            throw new IllegalArgumentException("predicate must not be null");
        }
        if (message == null) {
            throw new IllegalArgumentException("message must not be null");
        }
        this.predicate = predicate;
        this.message = message;
    }

    /**
     * If {@code predicate.test(tick)} returns {@code true}, prints {@code message}
     * to standard output. Otherwise, does nothing.
     *
     * @param tick the current tick number
     */
    @Override
    public void onTick(int tick) {
        if (predicate.test(tick)) {
            System.out.println(message);
        }
    }

    /**
     * Returns a string representation of this entity.
     *
     * @return descriptive string
     */
    @Override
    public String toString() {
        return "ConditionalEntity[message=" + message + "]";
    }
}
