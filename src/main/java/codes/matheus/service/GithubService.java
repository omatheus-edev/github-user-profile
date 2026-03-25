package codes.matheus.service;

import codes.matheus.cache.Cache;
import codes.matheus.config.AppConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class GithubService {
    private final @NotNull HttpClient client;
    private final @NotNull AppConfig config;
    private final @NotNull Cache cache;

    public GithubService(@NotNull AppConfig config, @NotNull Cache cache) {
        this.client = HttpClient.newHttpClient();
        this.config = config;
        this.cache = cache;
    }

    public @NotNull String getProfile(@NotNull String username) {
        return fetch("users/" + username);
    }

    public @NotNull String getRepository(@NotNull String username) {
        return fetch("users/" + username + "/repos");
    }

    public @NotNull String getRepository(@NotNull String username, @NotNull String name) {
        return fetch("repos/" + username + "/" + name);
    }

    public @NotNull String getEvents(@NotNull String username) {
        return fetch("users/" + username + "/events");
    }

    private @NotNull String fetch(@NotNull String path) {
        try {
            @Nullable String cached = cache.get(path);
            if (cached != null) {
                return cached;
            }

            @NotNull HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.github.com/" + path))
                    .GET()
                    .header("Accept", "application/vnd.github.v3+json")
                    .header("Authorization", "Bearer " + config.getToken())
                    .build();

            @NotNull HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            @NotNull String body = response.body();
            cache.put(path, body);
            return body;

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}