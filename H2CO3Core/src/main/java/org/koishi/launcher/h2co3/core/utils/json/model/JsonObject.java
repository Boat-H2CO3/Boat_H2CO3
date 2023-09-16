package org.koishi.launcher.h2co3.core.utils.json.model;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import org.koishi.launcher.h2co3.core.utils.json.abstracts.JsonValue;
import org.koishi.launcher.h2co3.core.utils.json.interfaces.IListable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The class Json object is used to store objects of type {@link JsonValue} in {@link #values}
 */
public class JsonObject extends JsonValue implements IListable<JsonValue> {

    /**
     * The Values is a list which is used by the {@link IListable}
     */
    protected final List<JsonValue> values = new ArrayList<>();

    /**
     * Instantiates a new Json object.
     */
    public JsonObject() {
    }

    /**
     * Instantiates a new Json object.
     *
     * @param key the key is the identifier of {@link JsonValue}
     */
    public JsonObject(String key) {
        super(key);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        String space = createSpace();

        stringBuilder.append(space);

        if (getKey() != null && !getKey().isEmpty()) {
            stringBuilder.append("\"").append(getKey()).append("\"").append(" : ");
        }

        stringBuilder.append("{");
        loop((integer, jsonValue) -> {
            jsonValue.setIntend(getIntend() + 2);
            stringBuilder.append("\n");
            if (!(integer == size() - 1)) {
                stringBuilder.append(jsonValue).append(",");
            } else {
                stringBuilder.append(jsonValue);
            }
        });
        stringBuilder.append("\n").append(space).append("}");

        return stringBuilder.toString();
    }


    /**
     * Get json value by key.
     *
     * @param key the key is the identifier of {@link JsonValue}
     * @return the json value is null or the Json value that is found in {@link #values}
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public JsonValue get(String key) {
        if (!has(key)) return null;
        return values.stream().filter(jsonValue -> jsonValue.getKey().equalsIgnoreCase(key)).findFirst().get();
    }

    /**
     * Get {@link T} by key and cast it to {@link Class<T>}
     *
     * @param <T>   the type parameter is used to determine what type of object is needed
     * @param key   the key is the identifier of {@link JsonValue}
     * @param clazz the clazz is used to cast the {@link T}
     * @return the {@link T} object
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public <T extends JsonValue> T get(String key, Class<T> clazz) {
        return clazz.cast(get(key));
    }

    /**
     * Has is used to find a {@link JsonValue} by its key in {@link #values}
     *
     * @param key the key is the identifier of {@link JsonValue}
     * @return the boolean true or false
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean has(String key) {
        return values.stream().anyMatch(jsonValue -> jsonValue.getKey() != null && jsonValue.getKey().equalsIgnoreCase(key));
    }

    /**
     * Gets index.
     * Used to get the index of an key if it is found in {@link #values}
     *
     * @param key the key is the identifier of {@link JsonValue}
     * @return the index by key
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public int getIndex(String key) {
        if (!has(key)) return -1;

        for (int index = 0; index < values.size(); index++) {
            JsonValue jsonValue = values.get(index);
            if (jsonValue.getKey().equalsIgnoreCase(key)) {
                return index;
            }
        }
        return -1;
    }

    /**
     * Add an object to {@link #values} by its key.
     *
     * @param key       the key is the identifier of {@link JsonValue}
     * @param jsonValue the json value is the {@link JsonValue} that is going to be added to {@link #values}
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void add(String key, JsonValue jsonValue) {
        jsonValue.pushEventKey(key);
        add(jsonValue);
    }

    @Override
    public int size() {
        return values.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void add(JsonValue value) {
        if (has(value.getKey())) {
            int index = getIndex(value.getKey());
            add(index, value);
        } else {
            values.add(value);
        }
    }

    @Override
    public void add(int index, JsonValue value) {
        values.set(index, value);
    }

    @Override
    public JsonValue get(int index, Class<JsonValue> clazz) {
        return clazz.cast(get(index));
    }

    @Override
    public JsonValue get(int index) {
        return values.get(index);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void loop(Consumer<JsonValue> consumer) {
        for (JsonValue value : values) {
            consumer.accept(value);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void loop(BiConsumer<Integer, JsonValue> consumer) {
        for (int index = 0; index < values.size(); index++) {
            JsonValue jsonValue = values.get(index);
            consumer.accept(index, jsonValue);
        }
    }


}
