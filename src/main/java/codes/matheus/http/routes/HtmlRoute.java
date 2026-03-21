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
        @Nullable InputStream html = getClass().getResourceAsStream("/frontend/index.html");

        if (html == null) {
            Response.builder(exchange)
                    .status(HttpStatus.NOT_FOUND)
                    .header("Content-Type", "text/plain")
                    .error("index.html not found")
                    .send();
            return;
        }

        byte[] bytes = html.readAllBytes();
        Response.builder(exchange)
                .header("Content-Type", "text/html; chatset=UTF-8")
                .body(new String(bytes))
                .send();
        html.close();
    }


}

