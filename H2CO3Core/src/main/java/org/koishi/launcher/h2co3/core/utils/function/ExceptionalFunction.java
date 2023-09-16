package org.koishi.launcher.h2co3.core.utils.function;

/**
 *
 * @author huangyuhui
 */
public interface ExceptionalFunction<T, R, E extends Exception> {
    R apply(T t) throws E;

    static <T, E extends RuntimeException> ExceptionalFunction<T, T, E> identity() {
        return t -> t;
    }
}