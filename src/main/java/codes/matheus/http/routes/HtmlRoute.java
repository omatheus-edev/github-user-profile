package codes.matheus.http.routes;

import codes.matheus.http.HttpStatus;
import codes.matheus.http.Response;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;

public final class HtmlRoute implements HttpHandler {
    @Override
    public void handle(@NotNull HttpExchange exchange) throws IOException {
        @NotNull String path = exchange.getRequestURI().getPath();
        if (path.equals("/")) {
            path = "index.html";
        }

        @Nullable InputStream file = getClass().getResourceAsStream("/frontend/" + path);

        if (file == null) {
            Response.builder(exchange)
                    .status(HttpStatus.NOT_FOUND)
                    .header("Content-Type", "text/plain")
                    .error("index.html not found")
                    .send();
            return;
        }

        byte[] bytes = file.readAllBytes();
        file.close();
        Response.builder(exchange)
                .header("Content-Type", getMimeType(path))
                .body(new String(bytes))
                .send();
    }

    private @NotNull String getMimeType(@NotNull String path) {
        if (path.endsWith(".html")) return "text/html; charset=UTF-8";
        if (path.endsWith(".css"))  return "text/css; charset=UTF-8";
        if (path.endsWith(".js"))   return "application/javascript; charset=UTF-8";
        if (path.endsWith(".png"))  return "image/png";
        if (path.endsWith(".ico"))  return "image/x-icon";
        if (path.endsWith(".svg"))  return "image/svg+xml";
        return "text/plain";
    }

}

