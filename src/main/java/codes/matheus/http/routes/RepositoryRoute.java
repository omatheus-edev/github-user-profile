package codes.matheus.http.routes;

import codes.matheus.cache.Cache;
import codes.matheus.config.AppConfig;
import codes.matheus.http.HttpStatus;
import codes.matheus.http.Response;
import codes.matheus.service.GithubService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public final class RepositoryRoute implements HttpHandler {
    private final @NotNull GithubService githubService;

    public RepositoryRoute(@NotNull AppConfig config, @NotNull Cache cache) {
        this.githubService = new GithubService(config, cache);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("OPTIONS")) {
            Response.builder(exchange)
                    .header("Access-Control-Allow-Methods", "GET, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Content-Type")
                    .send();
            return;
        }

        if (!exchange.getRequestMethod().equals("GET")) {
            Response.builder(exchange)
                    .status(HttpStatus.METHOD_NOT_ALLOWED)
                    .error("method not allowed")
                    .send();
            return;
        }

        // repos/username/name
        @NotNull String[] parts = exchange.getRequestURI().getPath().substring(1).split("/");
        @NotNull String username = parts[1];

        if (username.isBlank()) {
            Response.builder(exchange)
                    .status(HttpStatus.BAD_REQUEST)
                    .error("username is required")
                    .send();
            return;
        }

        @Nullable String name = null;
        if (parts[0].equals("repos")) {
            if (parts.length < 3) {
                Response.builder(exchange)
                        .status(HttpStatus.BAD_REQUEST)
                        .error("repository name is required")
                        .send();
                return;
            }
            name = parts[2];
        }

        if (name != null) {
            @NotNull String body = githubService.getRepository(username, name);
            Response.builder(exchange)
                    .status(HttpStatus.OK)
                    .body(body)
                    .send();
        }
    }
}
