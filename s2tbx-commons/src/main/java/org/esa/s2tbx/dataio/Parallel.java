package org.esa.s2tbx.dataio;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by kraftek on 7/16/2015.
 */
public class Parallel {
    static final int iCPU = Runtime.getRuntime().availableProcessors();

    public interface LoopBody <T> {
        void run(T i);
    }

    public static <T> void ForEach(Iterable <T> parameters, final LoopBody<T> loopBody) {
        ExecutorService executor = Executors.newFixedThreadPool(iCPU);
        List<Future<?>> futures  = new LinkedList<>();
        for (final T param : parameters) {
            Future<?> future = executor.submit(() -> loopBody.run(param));
            futures.add(future);
        }
        for (Future<?> f : futures) {
            try   { f.get(); }
            catch (InterruptedException | ExecutionException ignored) { }
        }
        executor.shutdown();
    }

    public static void For(int start, int stop, final LoopBody<Integer> loopBody) {
        ExecutorService executor = Executors.newFixedThreadPool(iCPU);
        List<Future<?>> futures  = new LinkedList<>();

        for (int i=start; i<stop; i++) {
            final Integer k = i;
            Future<?> future = executor.submit(() -> loopBody.run(k));
            futures.add(future);
        }
        for (Future<?> f : futures) {
            try   { f.get(); }
            catch (InterruptedException | ExecutionException ignored) { }
        }

        executor.shutdown();
    }
}