package org.koishi.launcher.h2co3.core.utils.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.File;
import java.lang.reflect.Type;

/**
 *
 * @author huangyuhui
 */
public final class FileTypeAdapter implements JsonSerializer<File>, JsonDeserializer<File> {

    public static final FileTypeAdapter INSTANCE = new FileTypeAdapter();

    private FileTypeAdapter() {
    }

    @Override
    public JsonElement serialize(File t, Type type, JsonSerializationContext jsc) {
        if (t == null)
            return JsonNull.INSTANCE;
        else
            return new JsonPrimitive(t.getPath());
    }

    @Override
    public File deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        if (je == null)
            return null;
        else
            return new File(je.getAsString());
    }

}
