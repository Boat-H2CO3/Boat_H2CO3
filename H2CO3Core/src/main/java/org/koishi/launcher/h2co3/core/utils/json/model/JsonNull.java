package org.koishi.launcher.h2co3.core.utils.json.model;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.core.utils.json.abstracts.JsonValue;

/**
 * The class Json null store a null value.
 */
public class JsonNull extends JsonValue {

    /**
     * The Value is a final value because null is null!
     */
    protected final String value = "null";

    /**
     * Instantiates a new Json null.
     */
    public JsonNull() {
    }

    /**
     * Instantiates a new Json null.
     *
     * @param key the key is the identifier of {@link JsonValue}
     */
    public JsonNull(String key) {
        super(key);
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
        stringBuilder.append("null");

        return stringBuilder.toString();
    }

    /**
     * Gets value.
     *
     * @return the value is the stored at {@link #value}
     */
    public String getValue() {
        return value;
    }
}
