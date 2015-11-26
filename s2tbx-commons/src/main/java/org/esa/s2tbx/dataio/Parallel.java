/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2014-2015 CS-Romania (office@c-s.ro)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

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
    static final int PARALLELISM = Math.max(Runtime.getRuntime().availableProcessors() - 1, 2);
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
        ForEach(params, PARALLELISM, code);
    }

    /**
     * Iterates an Iterable collection in parallel with the given
     * degree of parallelism
     *
     * @param params    The items to iterate
     * @param parallelism The degree of parallelism
     * @param code      The loop code
     * @param <T>       The type of items
     */
    public static <T> void ForEach(Iterable <T> params, int parallelism, final Runnable<T> code) {
        ExecutorService executor = Executors.newFixedThreadPool(parallelism);
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
        For(start, stop, PARALLELISM, code);
    }

    /**
     * Runs a for-loop in parallel with the given degree of parallelism
     *
     * @param start     The start index
     * @param stop      The end index
     * @param parallelism The degree of parallelism
     * @param code      The loop code
     */
    public static void For(int start, int stop, int parallelism, final Runnable<Integer> code) {
        ExecutorService executor = Executors.newFixedThreadPool(parallelism);
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