package org.koishi.launcher.h2co3.core.utils.json.interfaces;

import org.koishi.launcher.h2co3.core.utils.json.abstracts.JsonValue;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The interface Listable.
 * Used to implement functions like {@link #loop(Consumer)} or {@link #get(int)}
 *
 * @param <T> the type parameter determines generic type of value that can be get or added from a list
 */
public interface IListable<T> {

    /**
     * Size int.
     *
     * @return the int determines the size of given list
     */
    int size();

    /**
     * Add value to something.
     *
     * @param value the value is going to be add to a list or something else
     */
    void add(T value);

    /**
     * Add value to something where a index is needed.
     *
     * @param index the index is the position identifier of the object
     * @param value the value is going to be add to a list or something else
     */
    void add(int index, T value);

    /**
     * Get a value of type {@link T} by its index and cast it with {@link Class<T>}.
     *
     * @param index the index is the position of the object
     * @param clazz the clazz is the type of object
     * @return object of type {@link T}
     */
    T get(int index, Class<T> clazz);

    /**
     * Get a value of type {@link T} by its index.
     *
     * @param index the index is the position of the object
     * @return object of type {@link T}
     */
    T get(int index);

    /**
     * Loop is used to iterate through a {@link java.util.List} or an {@link java.util.Iterator}
     *
     * @param consumer the consumer accepts values from {@link java.util.List}
     */
    void loop(Consumer<JsonValue> consumer);

    /**
     * Loop is used to iterate through a {@link java.util.List} or an {@link java.util.Iterator}
     *
     * @param consumer the consumer the consumer accepts values and indexes from {@link java.util.List}
     */
    void loop(BiConsumer<Integer, JsonValue> consumer);


}
