package codes.matheus.cache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Cache {
    private final @NotNull Map<String, String> store;
    private final int limit;

    public Cache(int limit) {
        this.limit = limit;
        this.store = Collections.synchronizedMap(new LinkedHashMap<>(limit, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                return size() > limit;
            }
        });
    }

    public void put(@NotNull String key, @NotNull String value) {
        store.put(key, value);
    }

    public @Nullable String get(@NotNull String key) {
        return store.get(key);
    }
}
