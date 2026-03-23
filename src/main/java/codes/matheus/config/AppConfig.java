package codes.matheus.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AppConfig {
    public static @NotNull AppConfig fromEnv() {
        @NotNull String token = getEnv("GITHUB_TOKEN", "");
        int port = Integer.parseInt(getEnv("PORT", "8080"));
        int cacheLimit = Integer.parseInt(getEnv("CACHE_MAX_SIZE", "100"));
        return new AppConfig(token, port, cacheLimit);
    }

    private static @NotNull String getEnv(@NotNull String name, @NotNull String fallback) {
        @Nullable String value = System.getenv(name);
        return (value != null && !value.isBlank()) ? value : fallback;
    }

    private final @NotNull String token;
    private final int port;
    private final int cacheLimit;

    private AppConfig(@NotNull String token, int port, int cacheLimit) {
        this.token = token;
        this.port = port;
        this.cacheLimit = cacheLimit;
    }

    public @NotNull String getToken() {
        return token;
    }

    public int getPort() {
        return port;
    }

    public int getCacheLimit() {
        return cacheLimit;
    }
}