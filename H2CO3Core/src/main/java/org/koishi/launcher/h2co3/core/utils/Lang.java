package org.koishi.launcher.h2co3.core.utils;

import org.koishi.launcher.h2co3.core.utils.function.ExceptionalBiConsumer;
import org.koishi.launcher.h2co3.core.utils.function.ExceptionalConsumer;
import org.koishi.launcher.h2co3.core.utils.function.ExceptionalFunction;
import org.koishi.launcher.h2co3.core.utils.function.ExceptionalRunnable;
import org.koishi.launcher.h2co3.core.utils.function.ExceptionalSupplier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class Lang {

    public static final Function<Throwable, Void> handleUncaught = e -> {
        handleUncaughtException(e);
        return null;
    };
    private static Timer timer;

    private Lang() {
    }

    public static <T> T requireNonNullElse(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    public static <T> T requireNonNullElseGet(T value, Supplier<? extends T> defaultValue) {
        return value != null ? value : defaultValue.get();
    }

    public static <T, U> U requireNonNullElseGet(T value, Function<? super T, ? extends U> mapper, Supplier<? extends U> defaultValue) {
        return value != null ? mapper.apply(value) : defaultValue.get();
    }

    public static <K, V> Map<K, V> mapOf(Pair<K, V>... pairs) {
        return mapOf(Arrays.asList(pairs));
    }

    public static <K, V> Map<K, V> mapOf(Iterable<Pair<K, V>> pairs) {
        Map<K, V> map = new LinkedHashMap<>();
        for (Pair<K, V> pair : pairs)
            map.put(pair.getKey(), pair.getValue());
        return map;
    }

    @SafeVarargs
    public static <T> List<T> immutableListOf(T... elements) {
        return Collections.unmodifiableList(Arrays.asList(elements));
    }

    public static <T extends Comparable<T>> T clamp(T min, T val, T max) {
        return val.compareTo(min) < 0 ? min : val.compareTo(max) > 0 ? max : val;
    }

    public static double clamp(double min, double val, double max) {
        return Math.max(min, Math.min(val, max));
    }

    public static int clamp(int min, int val, int max) {
        return Math.max(min, Math.min(val, max));
    }

    public static boolean test(ExceptionalRunnable<?> r) {
        try {
            r.run();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static <E extends Exception> boolean test(ExceptionalSupplier<Boolean, E> r) {
        try {
            return r.get();
        } catch (Exception e) {
            return false;
        }
    }

    public static <T> T ignoringException(ExceptionalSupplier<T, ?> supplier) {
        return ignoringException(supplier, null);
    }

    public static <T> T ignoringException(ExceptionalSupplier<T, ?> supplier, T defaultValue) {
        try {
            return supplier.get();
        } catch (Exception ignore) {
            return defaultValue;
        }
    }

    public static <V> Optional<V> tryCast(Object obj, Class<V> clazz) {
        if (clazz.isInstance(obj)) {
            return Optional.ofNullable(clazz.cast(obj));
        } else {
            return Optional.empty();
        }
    }

    public static <T> T getOrDefault(List<T> a, int index, T defaultValue) {
        return index < 0 || index >= a.size() ? defaultValue : a.get(index);
    }

    public static <T> T merge(T a, T b, BinaryOperator<T> operator) {
        return a == null ? b : b == null ? a : operator.apply(a, b);
    }

    public static <T> List<T> removingDuplicates(List<T> list) {
        return new ArrayList<>(new LinkedHashSet<>(list));
    }

    public static <T> List<T> merge(Collection<? extends T> a, Collection<? extends T> b) {
        List<T> result = new ArrayList<>();
        if (a != null)
            result.addAll(a);
        if (b != null)
            result.addAll(b);
        return result;
    }

    public static <T> List<T> copyList(List<T> list) {
        return list == null || list.isEmpty() ? null : new ArrayList<>(list);
    }

    public static void executeDelayed(Runnable runnable, TimeUnit timeUnit, long timeout, boolean isDaemon) {
        thread(() -> {
            try {
                timeUnit.sleep(timeout);
                runnable.run();
            } catch (InterruptedException ignore) {
            }

        }, null, isDaemon);
    }

    public static Thread thread(Runnable runnable) {
        return thread(runnable, null);
    }

    public static Thread thread(Runnable runnable, String name) {
        return thread(runnable, name, false);
    }

    public static Thread thread(Runnable runnable, String name, boolean isDaemon) {
        Thread thread = new Thread(runnable);
        thread.setDaemon(isDaemon);
        if (name != null)
            thread.setName(name);
        thread.start();
        return thread;
    }

    public static ThreadPoolExecutor threadPool(String name, boolean daemon, int threads, long timeout, TimeUnit timeunit) {
        AtomicInteger counter = new AtomicInteger(1);
        ThreadPoolExecutor pool = new ThreadPoolExecutor(threads, threads, timeout, timeunit, new LinkedBlockingQueue<>(), r -> {
            Thread t = new Thread(r, name + "-" + counter.getAndIncrement());
            t.setDaemon(daemon);
            return t;
        });
        pool.allowCoreThreadTimeOut(true);
        return pool;
    }

    public static int parseInt(Object string, int defaultValue) {
        try {
            return Integer.parseInt(String.valueOf(string));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static Integer toIntOrNull(Object string) {
        try {
            return string == null ? null : Integer.parseInt(String.valueOf(string));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Double toDoubleOrNull(Object string) {
        try {
            return string == null ? null : Double.parseDouble(String.valueOf(string));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @SafeVarargs
    public static <T> T nonNull(T... t) {
        for (T a : t) if (a != null) return a;
        return null;
    }

    public static <T> T apply(T t, Consumer<T> consumer) {
        consumer.accept(t);
        return t;
    }

    public static void rethrow(Throwable e) {
        if (e == null)
            return;
        if (e instanceof ExecutionException || e instanceof CompletionException) {
            rethrow(e.getCause());
        } else if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        } else {
            throw new CompletionException(e);
        }
    }

    public static Runnable wrap(ExceptionalRunnable<?> runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                rethrow(e);
            }
        };
    }

    public static <T> Supplier<T> wrap(ExceptionalSupplier<T, ?> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                rethrow(e);
                throw new InternalError("Unreachable code");
            }
        };
    }

    public static <T, R> Function<T, R> wrap(ExceptionalFunction<T, R, ?> fn) {
        return t -> {
            try {
                return fn.apply(t);
            } catch (Exception e) {
                rethrow(e);
                throw new InternalError("Unreachable code");
            }
        };
    }

    public static <T> Consumer<T> wrapConsumer(ExceptionalConsumer<T, ?> fn) {
        return t -> {
            try {
                fn.accept(t);
            } catch (Exception e) {
                rethrow(e);
            }
        };
    }

    public static <T, E> BiConsumer<T, E> wrap(ExceptionalBiConsumer<T, E, ?> fn) {
        return (t, e) -> {
            try {
                fn.accept(t, e);
            } catch (Exception ex) {
                rethrow(ex);
            }
        };
    }

    public static <T> Consumer<T> compose(Consumer<T>... consumers) {
        return t -> {
            for (Consumer<T> consumer : consumers) {
                consumer.accept(t);
            }
        };
    }

    public static <T> Stream<T> toStream(Optional<T> optional) {
        return optional.map(Stream::of).orElseGet(Stream::empty);
    }

    public static <T> Iterable<T> toIterable(Enumeration<T> enumeration) {
        if (enumeration == null) {
            throw new NullPointerException();
        }
        return () -> new Iterator<>() {
            public boolean hasNext() {
                return enumeration.hasMoreElements();
            }

            public T next() {
                return enumeration.nextElement();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static <T> Iterable<T> toIterable(Stream<T> stream) {
        return stream::iterator;
    }

    public static <T> Iterable<T> toIterable(Iterator<T> iterator) {
        return () -> iterator;
    }

    public static <T, U> void forEachZipped(Iterable<T> i1, Iterable<U> i2, BiConsumer<T, U> action) {
        Iterator<T> it1 = i1.iterator();
        Iterator<U> it2 = i2.iterator();
        while (it1.hasNext() && it2.hasNext())
            action.accept(it1.next(), it2.next());
    }

    public static synchronized Timer getTimer() {
        if (timer == null) {
            timer = new Timer();
        }
        return timer;
    }

    public static synchronized TimerTask setTimeout(Runnable runnable, long delayMs) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        };
        getTimer().schedule(task, delayMs);
        return task;
    }

    public static Throwable resolveException(Throwable e) {
        if (e instanceof ExecutionException || e instanceof CompletionException)
            return resolveException(e.getCause());
        else
            return e;
    }

    public static <R> R handleUncaughtException(Throwable e) {
        Objects.requireNonNull(Thread.currentThread().getUncaughtExceptionHandler()).uncaughtException(Thread.currentThread(), e);
        return null;
    }
}