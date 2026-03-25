package codes.matheus.http.routes;

import codes.matheus.cache.Cache;
import codes.matheus.config.AppConfig;
import codes.matheus.http.HttpStatus;
import codes.matheus.http.Response;
import codes.matheus.service.GithubService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class ProfileRoute implements HttpHandler {
    private final @NotNull GithubService githubService;

    public ProfileRoute(@NotNull AppConfig config, @NotNull Cache cache) {
        this.githubService = new GithubService(config, cache);
    }

    @Override
    public void handle(@NotNull HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("OPTIONS")) {
            Response.builder(exchange)
                    .header("Access-Control-Allow-Methods", "GET, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Content-Type")
                    .noContent()
                    .send();
            return;
        }

        if (!exchange.getRequestMethod().equals("GET")) {
            Response.builder(exchange)
                    .status(HttpStatus.METHOD_NOT_ALLOWED)
                    .error("method now allowed")
                    .send();
            return;
        }

        @NotNull String path = exchange.getRequestURI().getPath().substring("/users/".length());
        @NotNull String[] parts = path.split("/");
        @NotNull String username = parts[0];

        if (username.isBlank()) {
            Response.builder(exchange)
                    .status(HttpStatus.BAD_REQUEST)
                    .error("username is required")
                    .send();
            return;
        }

        // users/username/repos
        // users/username/events
        @NotNull String body = (parts.length > 1 && parts[1].equals("repos"))
                ? githubService.getRepository(username)
                : (parts.length > 1 && parts[1].equals("events"))
                ? githubService.getEvents(username)
                : githubService.getProfile(username);

        Response.builder(exchange)
                .status(HttpStatus.OK)
                .body(body)
                .send();
    }
}
