package org.koishi.launcher.h2co3.core.utils.json.model;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.core.utils.json.abstracts.JsonValue;

/**
 * The class Json boolean stores a boolean.
 */
public class JsonBoolean extends JsonValue {

    /**
     * The Value is the given boolean
     */
    public boolean value;

    /**
     * Instantiates a new Json boolean.
     */
    public JsonBoolean() {
    }

    /**
     * Instantiates a new Json boolean.
     *
     * @param value the value is the given boolean that is set to {@link #value}
     */
    public JsonBoolean(boolean value) {
        this.value = value;
    }

    /**
     * Instantiates a new Json boolean.
     *
     * @param key   the key is the identifier of {@link JsonValue}
     * @param value the value is the given boolean that is set to {@link #value}
     */
    public JsonBoolean(String key, boolean value) {
        super(key);
        this.value = value;
    }


    @NonNull
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        String space = createSpace();

        stringBuilder.append(space);
        if (getKey() != null && !getKey().isEmpty()) {
            stringBuilder.append("\"").append(getKey()).append("\"").append(" : ");
        }
        stringBuilder.append(isValue());

        return stringBuilder.toString();
    }


    /**
     * Is value boolean.
     *
     * @return the boolean value is the stored at {@link #value}
     */
    public boolean isValue() {
        return value;
    }

    /**
     * Sets value.
     *
     * @param value the value is the value {@link #value}
     */
    public void setValue(boolean value) {
        this.value = value;
    }


}
