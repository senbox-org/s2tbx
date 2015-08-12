package org.esa.s2tbx.dataio;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Helper class to execute loops in parallel.
 */
public class Parallel {
    static final int PARALLELISM = Runtime.getRuntime().availableProcessors();
    /**
     * The loop code wrapper
     */
    public interface Runnable<T> {
        void run(T i);
    }

    /**
     * Iterates an Iterable collection in parallel.
     *
     * @param params    The items to iterate
     * @param code      The loop code
     * @param <T>       The type of items
     */
    public static <T> void ForEach(Iterable <T> params, final Runnable<T> code) {
        ExecutorService executor = Executors.newFixedThreadPool(PARALLELISM);
        List<Future<?>> tasks  = new LinkedList<>();
        for (final T param : params) {
            tasks.add(executor.submit(() -> code.run(param)));
        }
        tasks.stream().forEach(task -> {
            try   { task.get(); }
            catch (InterruptedException | ExecutionException ignored) { }
        });
        executor.shutdown();
    }

    /**
     * Runs a for-loop in parallel.
     *
     * @param start     The start index
     * @param stop      The end index
     * @param code      The loop code
     */
    public static void For(int start, int stop, final Runnable<Integer> code) {
        ExecutorService executor = Executors.newFixedThreadPool(PARALLELISM);
        List<Future<?>> tasks  = new LinkedList<>();
        for (int i = start; i < stop; i++) {
            final Integer index = i;
            tasks.add(executor.submit(() -> code.run(index)));
        }
        tasks.stream().forEach(task -> {
            try   { task.get(); }
            catch (InterruptedException | ExecutionException ignored) { }
        });
        executor.shutdown();
    }
}