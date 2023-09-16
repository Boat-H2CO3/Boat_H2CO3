package org.koishi.launcher.h2co3.core.utils.json.abstracts;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.core.utils.json.model.JsonObject;

/**
 * The class {@link JsonValue} is a upper class for Json values like {@link JsonObject}
 */
public abstract class JsonValue {

    private int intend = 0;
    private String key;

    /**
     * Instantiates a new Json value.
     */
    public JsonValue() {
    }

    /**
     * Instantiates a new Json value.
     *
     * @param key the key is used to find a document in {@link JsonObject}
     */
    public JsonValue(String key) {
        this.key = key;
    }

    /**
     * Create space string.
     * Used to generate the spaces when a object of type {@link JsonValue} is used with {@link #toString()}
     *
     * @return the string
     */
    public String createSpace() {
        StringBuilder spaceBuilder = new StringBuilder();
        for (int i = 0; i < intend; i++) {
            spaceBuilder.append(" ");
        }
        return spaceBuilder.toString();
    }

    @NonNull
    @Override
    public abstract String toString();

    /**
     * Gets key.
     *
     * @return key the key is used to find a document in {@link JsonObject}
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets key.
     *
     * @param key the key is used to find a document in {@link JsonObject}
     */
    public void pushEventKey(String key) {
        this.key = key;
    }


    /**
     * Gets intend.
     *
     * @return the intend used to calculate free space in method {@link #createSpace()}
     */
    public int getIntend() {
        return intend;
    }

    /**
     * Sets intend.
     *
     * @param intend the intend used to calculate ree space in method {@link #createSpace()}
     */
    public void setIntend(int intend) {
        this.intend = intend;
    }

}
