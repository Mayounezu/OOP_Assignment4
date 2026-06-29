package com.mysteryticking;

/**
 * Bonus entry point demonstrating {@link FiniteEntity} and
 * {@link ConditionalEntity}.
 *
 * <p>Run with:
 * <pre>
 *   java -cp hw4.jar com.mysteryticking.BonusMain [countdown]
 * </pre>
 *
 * <p>The output shows:
 * <ul>
 *   <li>Ron printing at most 3 times before silently disappearing (FiniteEntity).</li>
 *   <li>"Dobby is free!" on every tick divisible by 7 (ConditionalEntity).</li>
 *   <li>"Avada Kedavra!" on ticks 13 and 42 (ConditionalEntity).</li>
 * </ul>
 */
public class BonusMain {

    public static void main(String[] args) {
        int countdown = 50;
        if (args.length > 0) {
            try {
                countdown = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid countdown argument: " + args[0]
                        + ". Using default 50.");
            }
        }

        System.out.println("=== BONUS DEMO (countdown=" + countdown + ") ===");
        System.out.println("Ron has maxFires=3 — verify he stops printing after 3 activations.");
        System.out.println();

        PipeBomb bomb = new PipeBomb(countdown);

        // FiniteEntity wrapping Ron: fires at most 3 times, then unregisters itself.
        // Ron's StateMachineEntity: startTick=1, ("Ron",2), ("Ron Weasly",4) → cycleLen=6
        // State entries at t=1 ("Ron"), t=3 ("Ron"), t=7 ("Ron"), t=9 ("Ron"), ...
        // But FiniteEntity caps this at 3 firings (all ticks, not just state-entry ticks).
        TickListener ronSme = new StateMachineEntity("Ron", 1,
                new State("Ron", 2),
                new State("Ron Weasly", 4));
        FiniteEntity finiteRon = new FiniteEntity(ronSme, bomb, 3);
        bomb.register(finiteRon);

        // ConditionalEntity: prints on every tick divisible by 7.
        bomb.register(new ConditionalEntity(
                t -> t % 7 == 0, "Dobby is free!"));

        // ConditionalEntity: prints on ticks 13 and 42.
        bomb.register(new ConditionalEntity(
                t -> t == 13 || t == 42, "Avada Kedavra!"));

        // CycleSeparator lambda — same as main simulation
        bomb.register(t -> {
            if (t % 8 == 0) {
                System.out.println();
            }
        });

        bomb.start();
    }
}
