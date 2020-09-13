import io.helidon.config.Config;
import io.helidon.media.jackson.JacksonSupport;
import io.helidon.metrics.MetricsSupport;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;

public class Main {
    public static void main(String[] args) {
        final var config = Config.create();
        final var customerService = new CustomerService(config);
        final var metricsSupport = MetricsSupport.create();
        final var jacksonSupport = JacksonSupport.create();
        final var routing = Routing.builder()
            .register(metricsSupport)
            .register(customerService)
            .build();

        WebServer
            .builder()
            .addMediaSupport(jacksonSupport)
            .config(config.get("server"))
            .routing(routing)
            .build()
            .start()
            .thenAccept(webServer -> {
                System.out.println("Server started on port: " + webServer.port());
                webServer.whenShutdown().thenRun(customerService::stop);
            })
            .exceptionally(ex -> {
                System.err.println("Startup failed: " + ex.getMessage());
                ex.printStackTrace(System.err);
                return null;
            });
    }
}
