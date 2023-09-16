package org.koishi.launcher.h2co3.core.utils.json.model;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.core.utils.json.abstracts.JsonValue;

/**
 * The class Json string is used to store a string.
 */
public class JsonString extends JsonValue {

    private String value;

    /**
     * Instantiates a new Json string.
     */
    public JsonString() {
    }

    /**
     * Instantiates a new Json string.
     *
     * @param key   the key is the identifier of {@link JsonValue}
     * @param value the value is the given string that is set to {@link #value}
     */
    public JsonString(String key, String value) {
        super(key);
        this.value = value;
    }

    /**
     * Instantiates a new Json string.
     *
     * @param value the value is the given string that is set to {@link #value}
     */
    public JsonString(String value) {
        this.value = value;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        String space = createSpace();

        stringBuilder.append(space);
        if (getKey() != null && !getKey().isEmpty()) {
            stringBuilder.append("\"").append(getKey()).append("\"")
                    .append(" : ").append("\"").append(getValue()).append("\"");
        } else {
            stringBuilder.append("\"").append(value).append("\"");
        }

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

    /**
     * Sets value.
     *
     * @param value the value is set to {@link #value}
     */
    public void setValue(String value) {
        this.value = value;
    }

}
