package org.koishi.launcher.h2co3.core.utils.function;

public interface ExceptionalPredicate<T, E extends Exception> {
    boolean test(T t) throws E;
}
