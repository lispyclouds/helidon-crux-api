import com.fasterxml.jackson.core.JsonProcessingException;
import crux.api.ICruxAPI;
import io.helidon.webserver.Routing;

public class Routes {
    public static Routing routes(ICruxAPI node) {
        return Routing
            .builder()
            .get("/", (req, res) -> {
                try {
                    Handlers.healthCheck(res);
                } catch (JsonProcessingException e) {
                    Handlers.errorResponse(res, e.getMessage(), 500);
                }
            })
            .get("/customers", ((req, res) -> {
                try {
                    Handlers.listCustomers(res, node);
                } catch (JsonProcessingException e) {
                    Handlers.errorResponse(res, e.getMessage(), 500);
                }
            }))
            .build();
    }
}
