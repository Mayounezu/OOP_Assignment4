package com.mysteryticking;

/**
 * BONUS: A {@link TickListener} decorator that wraps another listener and
 * automatically unregisters itself from an {@link Observable} after it has
 * been notified {@code maxFires} times.
 *
 * <p>Once the firing count reaches {@code maxFires}, the entity calls
 * {@link Observable#unregister(TickListener)} on itself and is never
 * notified again.
 *
 * <p>Example:
 * <pre>
 *   // Ron fires at most 3 times, then silently disappears.
 *   TickListener ron = new StateMachineEntity("Ron", 1,
 *       new State("Ron", 2), new State("Ron Weasly", 4));
 *   bomb.register(new FiniteEntity(ron, bomb, 3));
 * </pre>
 */
public class FiniteEntity implements TickListener {

    private final TickListener delegate;
    private final Observable observable;
    private final int maxFires;
    private int fireCount;

    /**
     * Constructs a FiniteEntity.
     *
     * @param delegate   the underlying listener whose {@code onTick} is delegated to
     * @param observable the subject from which this entity will unregister itself
     * @param maxFires   maximum number of times the delegate may be notified; must be &ge; 1
     * @throws IllegalArgumentException if {@code maxFires} &lt; 1
     */
    public FiniteEntity(TickListener delegate, Observable observable, int maxFires) {
        if (maxFires < 1) {
            throw new IllegalArgumentException(
                    "maxFires must be >= 1, but was: " + maxFires);
        }
        this.delegate = delegate;
        this.observable = observable;
        this.maxFires = maxFires;
        this.fireCount = 0;
    }

    /**
     * Forwards the tick to the delegate listener. After the {@code maxFires}-th
     * forwarded call, unregisters this entity from the observable so it receives
     * no further notifications.
     *
     * @param tick the current tick number
     */
    @Override
    public void onTick(int tick) {
        delegate.onTick(tick);
        fireCount++;
        if (fireCount >= maxFires) {
            observable.unregister(this);
        }
    }

    /**
     * Returns a string representation including the delegate and firing info.
     *
     * @return descriptive string
     */
    @Override
    public String toString() {
        return "FiniteEntity[delegate=" + delegate + ", maxFires=" + maxFires
                + ", fired=" + fireCount + "]";
    }
}
