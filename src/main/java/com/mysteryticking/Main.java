package com.mysteryticking;

/**
 * Entry point for the Mysterious Ticking Noise simulation.
 *
 * <p>Creates one {@link PipeBomb} (default countdown 160, overridable via
 * a command-line integer argument) and registers six listeners in the order
 * specified by the assignment:
 *
 * <ol>
 *   <li>Snape        — startTick 1,  cycle 8</li>
 *   <li>Dumbledore   — startTick 16, cycle 8</li>
 *   <li>Ron          — startTick 33, cycle 8</li>
 *   <li>Hermione     — startTick 49, cycle 16</li>
 *   <li>Harry Potter — startTick 65, cycle 8</li>
 *   <li>CycleSeparator — lambda printing a blank line when {@code tick % 8 == 0}</li>
 * </ol>
 *
 * <p>Also demonstrates the bonus {@link FiniteEntity} and
 * {@link ConditionalEntity} classes in the {@link #runBonusDemo(int)} method.
 */
public class Main {

    /**
     * Application entry point.
     *
     * @param args optional: args[0] is an integer countdown value (default 160)
     */
    public static void main(String[] args) {
        int countdown = 160;
        if (args.length > 0) {
            try {
                countdown = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid countdown argument: " + args[0]
                        + ". Using default 160.");
            }
        }

        runSimulation(countdown);
    }

    /**
     * Builds and runs the main simulation with the six registered listeners.
     *
     * @param countdown number of ticks for the simulation
     */
    public static void runSimulation(int countdown) {
        PipeBomb bomb = new PipeBomb(countdown);

        // 1. Snape — startTick 1, states: ("Snape",2), ("Snape",2), ("Severus Snape",4)
        bomb.register(new StateMachineEntity("Snape", 1,
                new State("Snape", 2),
                new State("Snape", 2),
                new State("Severus Snape", 4)));

        // 2. Dumbledore — startTick 16, states: ("Dumbledore",8)
        bomb.register(new StateMachineEntity("Dumbledore", 16,
                new State("Dumbledore", 8)));

        // 3. Ron — startTick 33, states: ("Ron",2), ("Ron",2), ("Ron Weasly",4)
        bomb.register(new StateMachineEntity("Ron", 33,
                new State("Ron", 2),
                new State("Ron", 2),
                new State("Ron Weasly", 4)));

        // 4. Hermione — startTick 49
        //    states: ("Hermione",4), ("Hermione",4), ("Hermione",2), ("Hermione",2), ("Hermione",4)
        //    cycleLen = 16
        bomb.register(new StateMachineEntity("Hermione", 49,
                new State("Hermione", 4),
                new State("Hermione", 4),
                new State("Hermione", 2),
                new State("Hermione", 2),
                new State("Hermione", 4)));

        // 5. Harry Potter — startTick 65
        //    states: ("Harry Potter",1), ("Harry Potter",1), ("OoOohhhh",2),
        //            ("Harry Potter",1), ("Harry Potter",1), ("YeeeEeaahH",2)
        //    cycleLen = 8
        bomb.register(new StateMachineEntity("Harry Potter", 65,
                new State("Harry Potter", 1),
                new State("Harry Potter", 1),
                new State("OoOohhhh", 2),
                new State("Harry Potter", 1),
                new State("Harry Potter", 1),
                new State("YeeeEeaahH", 2)));

        // 6. CycleSeparator — lambda (no separate class); prints blank line when tick % 8 == 0
        //    Must be registered last so it always fires after all entity output for that tick.
        bomb.register(tick -> {
            if (tick % 8 == 0) {
                System.out.println();
            }
        });

        bomb.start();
    }

    /**
     * BONUS DEMO: Demonstrates {@link FiniteEntity} and {@link ConditionalEntity}.
     * Run this method separately to see the bonus features in action.
     *
     * @param countdown number of ticks for the bonus simulation
     */
    public static void runBonusDemo(int countdown) {
        System.out.println("=== BONUS DEMO (countdown=" + countdown + ") ===");
        PipeBomb bomb = new PipeBomb(countdown);

        // FiniteEntity: Ron fires at most 3 times, then silently disappears
        TickListener ronSme = new StateMachineEntity("Ron", 1,
                new State("Ron", 2),
                new State("Ron Weasly", 4));
        bomb.register(new FiniteEntity(ronSme, bomb, 3));

        // ConditionalEntity: prints on every tick divisible by 7
        bomb.register(new ConditionalEntity(
                t -> t % 7 == 0, "Dobby is free!"));

        // ConditionalEntity: prints on ticks 13 and 42
        bomb.register(new ConditionalEntity(
                t -> t == 13 || t == 42, "Avada Kedavra!"));

        bomb.start();
    }
}
