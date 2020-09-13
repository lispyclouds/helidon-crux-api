import com.fasterxml.jackson.core.JsonProcessingException;
import io.helidon.webserver.Routing;

public class Routes {
    public static Routing routes() {
        return Routing
            .builder()
            .get("/", (request, response) -> {
                try {
                    Handlers.healthCheck(response);
                } catch (JsonProcessingException e) {
                    Handlers.errorResponse(response, e.getMessage(), 500);
                }
            })
            .build();
    }
}
