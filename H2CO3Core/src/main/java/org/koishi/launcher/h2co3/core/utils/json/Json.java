package org.koishi.launcher.h2co3.core.utils.json;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.koishi.launcher.h2co3.core.utils.json.abstracts.JsonHandler;
import org.koishi.launcher.h2co3.core.utils.json.abstracts.JsonValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * The type Json.
 */
public class Json {

    private final JsonGenerator jsonGenerator = new JsonGenerator();
    private final JsonParser jsonParser = new JsonParser();

    /**
     * Parse {@link String} to {@link JsonValue}.
     *
     * @param json the json used to parse
     * @return the json value is the raw return type
     */
    public JsonValue fromJsonString(String json) {
        return jsonParser.parse(json);
    }

    /**
     * Parse {@link String} to {@link JsonValue} and cast it to {@link Class<T>}
     *
     * @param <T>   the type parameter determines the type of {@link JsonValue}
     * @param json  the input stream is used to parse
     * @param clazz the clazz is used to cast {@link T}
     * @return the value {@link T}
     */
    public <T extends JsonValue> T fromJsonString(String json, Class<T> clazz) {
        return jsonParser.parse(json, clazz);
    }


    /**
     * Parse {@link File} to {@link JsonValue} and cast it to {@link Class<T>}
     *
     * @param <T>   the type parameter determines the type of {@link JsonValue}
     * @param file  the file is used to parse
     * @param clazz the clazz is used to cast {@link T}
     * @return the value {@link T}
     * @throws FileNotFoundException the file not found exception
     */
    public <T extends JsonValue> T fromFile(File file, Class<T> clazz) throws FileNotFoundException {
        return clazz.cast(jsonParser.parse(file));
    }

    /**
     * Parse {@link File} to {@link JsonValue}.
     *
     * @param file the file used to parse
     * @return the json value raw return type
     * @throws FileNotFoundException the file not found exception
     */
    public JsonValue fromFile(File file) throws FileNotFoundException {
        return jsonParser.parse(new FileInputStream(file));
    }


    /**
     * Parse {@link InputStream} to {@link JsonValue} and cast it to {@link Class<T>}
     *
     * @param <T>         the type parameter determines the type of {@link JsonValue}
     * @param inputStream the input stream is used to parse
     * @param clazz       the clazz is used to cast {@link T}
     * @return the value {@link T}
     */
    public <T extends JsonValue> T parse(InputStream inputStream, Class<T> clazz) {
        return jsonParser.parse(inputStream, clazz);
    }

    /**
     * Parse json value with {@link InputStream}
     *
     * @param inputStream the input stream is used to parse
     * @return the json value is the raw return type
     */
    public JsonValue parse(InputStream inputStream) {
        return jsonParser.parse(inputStream);
    }

    /**
     * To json value.
     * Parse object of type {@link T} to {@link JsonValue}
     *
     * @param <T>   the type parameter used to determine value
     * @param value the value used to parse
     * @return the json value returns raw type of {@link JsonValue}
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public <T> JsonValue toJson(T value) {
        return jsonGenerator.toJson(value);
    }


    /**
     * From json to list.
     * Convert {@link JsonValue} to {@link T} and cast with {@link Class<K>}
     *
     * @param <T>       the type parameter determines object type
     * @param <K>       the type parameter is the class that is used t ocast
     * @param jsonValue the json value is used to convert
     * @param listClazz the list clazz is used to cast {@link T}
     * @param clazz     the clazz is used to determine object in {@link List}
     * @return T is a list with cast {@link Class<T>}
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public <T extends List<K>, K> T fromJson(JsonValue jsonValue, Class<T> listClazz, Class<K> clazz) {
        return jsonGenerator.fromJson(jsonValue, listClazz, clazz);
    }

    /**
     * From json with cast.
     * Convert {@link JsonValue} to {@link T}
     *
     * @param <T>       the type parameter determines type of return object
     * @param jsonValue the json value is used to convert to {@link T}
     * @param clazz     the clazz is used to cast {@link T}
     * @return {@link T} cast with {@link Class<T>}
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public <T> T fromJsonCast(JsonValue jsonValue, Class<T> clazz) {
        return jsonGenerator.fromJsonCast(jsonValue, clazz);
    }

    /**
     * From json.
     * Convert {@link JsonValue} to raw object
     *
     * @param <T>       the type parameter determines type of return object
     * @param jsonValue the json value is used to convert to {@link Object}
     * @param clazz     the clazz is used create object
     * @return the object
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public <T> Object fromJson(JsonValue jsonValue, Class<T> clazz) {
        return jsonGenerator.fromJson(jsonValue, clazz);
    }


    /**
     * Register handler.
     * Register a {@link JsonHandler <?>} by {@link Class<?>}
     *
     * @param clazz       the clazz used to put {@link Class<?>} and {@link JsonHandler<?>} in {@link #jsonGenerator}
     * @param jsonHandler the json handler is used to insert
     */
    public void registerHandler(Class<?> clazz, JsonHandler<?> jsonHandler) {
        jsonGenerator.registerHandler(clazz, jsonHandler);
    }

    /**
     * Unregister handler.
     * Unregister a {@link JsonHandler} by {@link Class<?>}
     *
     * @param clazz the clazz used to remove {@link JsonHandler} from {@link #jsonGenerator}
     */
    public void unregisterHandler(Class<?> clazz) {
        jsonGenerator.unregisterHandler(clazz);
    }

}
