package codes.matheus.http;

import codes.matheus.cache.Cache;
import codes.matheus.config.AppConfig;
import codes.matheus.http.routes.HtmlRoute;
import codes.matheus.http.routes.ProfileRoute;
import com.jlogm.Logger;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public final class Server {
    public static @NotNull Logger log = Logger.create(Server.class);
    private final @NotNull HttpServer server;
    private final @NotNull AppConfig config;
    private final @NotNull Cache cache;
    private final int port;

    public Server() {
        try {
            this.config = AppConfig.fromEnv();
            this.cache = new Cache(config.getCacheLimit());
            this.port = config.getPort();
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        server.createContext("/", new HtmlRoute());
        log.info("Registered endpoint " + "\"/\"" + " (GET)");
        server.createContext("/users/", new ProfileRoute(config, cache));
        log.info("Registered endpoint " + "\"/users/\"" + " (GET)");

        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        log.info("HTTP server running on http://localhost:" + port + "/");
    }
}
