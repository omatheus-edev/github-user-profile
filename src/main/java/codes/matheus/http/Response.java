package codes.matheus.http;

import codes.matheus.util.Json;
import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class Response {
    public static @NotNull Builder builder(@NotNull HttpExchange exchange) {
        return new Builder(exchange);
    }

    private final @NotNull HttpExchange exchange;
    private final @NotNull HttpStatus status;
    private final @NotNull Map<String, String> headers;
    private final byte[] body;

    public Response(@NotNull Builder builder) {
        this.exchange = builder.exchange;
        this.status = builder.status;
        this.headers = builder.headers;
        this.body = builder.body;
    }

    public void send() throws IOException {
        headers.forEach((key, value) -> exchange.getResponseHeaders().add(key, value));
        int length = body != null ? body.length : -1;
        exchange.sendResponseHeaders(status.getCode(), length);

        if (body != null) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        } else {
            exchange.getResponseBody().close();
        }
    }

    public static final class Builder {
        private final @NotNull HttpExchange exchange;
        private final @NotNull Map<String, String> headers = new HashMap<>();
        private @NotNull HttpStatus status = HttpStatus.OK;
        private byte[] body;

        public Builder(@NotNull HttpExchange exchange) {
            this.exchange = exchange;
            headers.put("Content-Type", "application/json");
            headers.put("Access-Control-Allow-Origin", "*");
        }

        public Builder status(@NotNull HttpStatus status) {
            this.status = status;
            return this;
        }

        public Builder header(@NotNull String key, @NotNull String value) {
            headers.put(key, value);
            return this;
        }

        public Builder body(@NotNull String body) {
            this.body = body.getBytes(StandardCharsets.UTF_8);
            return this;
        }

        public Builder error(@NotNull String message) {
            return body(Json.serialize("error", message));
        }

        public Builder noContent() {
            this.status = HttpStatus.NO_CONTENT;
            this.body = null;
            return this;
        }

        public @NotNull Response build() {
            return new Response(this);
        }

        public void send() throws IOException {
            build().send();
        }
    }
}
