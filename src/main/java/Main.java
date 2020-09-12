import io.helidon.config.Config;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        final var config = Config.create();

        final var routing = Routing
            .builder()
            .get("/", (request, response) -> response.send("Hello World!"))
            .build();

        WebServer
            .create(routing, config.get("server"))
            .start()
            .toCompletableFuture()
            .get(30, TimeUnit.SECONDS);

        System.out.println("Server started.");
    }
}
