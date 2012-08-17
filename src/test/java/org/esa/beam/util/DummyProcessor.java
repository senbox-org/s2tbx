package org.esa.beam.util;

/**
 * @author Norman Fomferra
 */
public class DummyProcessor {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: DummyProcessor <seconds> <steps>");
            System.exit(1);
        }
        int seconds = Integer.parseInt(args[0]);
        int steps = Integer.parseInt(args[1]);
        System.out.println("Start");
        long period = (seconds * 1000L) / steps;
        for (int i = 0; i < steps; i++) {
            try {
                Thread.sleep(period);
            } catch (InterruptedException e) {
                System.err.println("Process interrupted");
                System.exit(2);
            }
            System.out.println("Progress " + ((i + 1) * 100) / steps + "%");
        }
        System.out.println("Done");
    }
}
