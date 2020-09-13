import crux.api.ICruxAPI;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerResponse;
import utils.ThrowingConsumer;

public class Routes {
    private static void respond(ThrowingConsumer<ServerResponse, ICruxAPI, Exception> fn, ServerResponse res, ICruxAPI node) {
        try {
            fn.accept(res, node);
        } catch (Exception e) {
            Handlers.errorResponse(res, e.getMessage(), 500);
        }
    }

    public static Routing routes(ICruxAPI node) {
        return Routing
            .builder()
            .get("/", (req, res) -> respond(Handlers::healthCheck, res, node))
            .get("/customers", (req, res) -> respond(Handlers::listCustomers, res, node))
            .build();
    }
}
