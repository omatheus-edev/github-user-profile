package codes.matheus.util;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public final class Json {
    private Json() {
        throw new UnsupportedOperationException("this class cannot be instantiated");
    }

    public static @NotNull String serialize(@NotNull String key, @NotNull String value) {
        @NotNull JsonObject obj = new JsonObject();
        obj.addProperty(key, value);
        return obj.toString();
    }
}
