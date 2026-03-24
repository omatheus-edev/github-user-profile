package codes.matheus;

import codes.matheus.http.Server;
import org.jetbrains.annotations.NotNull;

public class Main {
    public static void main(String[] args) {
        @NotNull Server server = new Server();
        server.run();
    }
}