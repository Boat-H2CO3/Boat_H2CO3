package org.koishi.launcher.h2co3.core.utils.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.koishi.launcher.h2co3.core.utils.gson.tools.TolerableValidationException;
import org.koishi.launcher.h2co3.core.utils.gson.tools.Validation;

import java.io.IOException;

/**
 *
 * @author huangyuhui
 */
public final class ValidationTypeAdapterFactory implements TypeAdapterFactory {

    public static final ValidationTypeAdapterFactory INSTANCE = new ValidationTypeAdapterFactory();

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> tt) {
        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, tt);
        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter writer, T t) throws IOException {
                if (t instanceof Validation) {
                    try {
                        ((Validation) t).validate();
                    } catch (TolerableValidationException e) {
                        delegate.write(writer, null);
                        return;
                    }
                }

                delegate.write(writer, t);
            }

            @Override
            public T read(JsonReader reader) throws IOException {
                T t = delegate.read(reader);
                if (t instanceof Validation) {
                    try {
                        ((Validation) t).validate();
                    } catch (TolerableValidationException e) {
                        return null;
                    }
                }
                return t;
            }
        };
    }
}
