import io.helidon.webserver.Routing;

public class Routes {
    public static Routing routes() {
        return Routing
            .builder()
            .get("/", (request, response) -> response.send("Hello World!"))
            .build();
    }
}
