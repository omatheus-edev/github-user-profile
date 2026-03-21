package codes.matheus.http;

import codes.matheus.http.routes.HtmlRoute;
import com.jlogm.Logger;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public final class Server {
    public static @NotNull Logger log = Logger.create(Server.class);
    private final @NotNull HttpServer server;
    private final int port;

    public Server(int port) {
        try {
            this.port = port;
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        server.createContext("/", new HtmlRoute());
        log.info("Registered endpoint " + "\"/\"" + "(GET)");

        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        log.info("HTTP server running on http://localhost:" + port + "/");
    }
}
