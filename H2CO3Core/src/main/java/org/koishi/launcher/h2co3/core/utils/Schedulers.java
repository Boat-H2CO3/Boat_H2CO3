package org.koishi.launcher.h2co3.core.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public final class Schedulers {

    private Schedulers() {
    }

    private static final ExecutorService ioExecutor = createIoExecutor();

    /**
     * Get singleton instance of the thread pool for I/O operations,
     * usually for reading files from disk, or Internet connections.
     *
     * This thread pool has no more than 4 threads, and number of threads will get
     * reduced if concurrency is less than thread number.
     *
     * @return Thread pool for I/O operations.
     */
    public static ExecutorService io() {
        return ioExecutor;
    }

    public static ForkJoinPool defaultScheduler() {
        return ForkJoinPool.commonPool();
    }

    public static void shutdown() {
        Logging.LOG.info("Shutting down executor services.");

        // shutdownNow will interrupt all threads.
        // So when we want to close the HMCLPE, no threads need to be waited for finish.
        // Sometimes it resolves the problem that the HMCLPE does not exit.

        if (ioExecutor != null) {
            ioExecutor.shutdownNow();
        }
    }

    private static ExecutorService createIoExecutor() {
        return Executors.newFixedThreadPool(4);
    }
}