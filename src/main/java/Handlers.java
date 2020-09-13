import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.helidon.webserver.ServerResponse;

import java.util.Map;

public class Handlers {
    private static final ObjectMapper objMapper = new ObjectMapper();

    private static void jsonResponse(ServerResponse response, Object content) throws JsonProcessingException {
        jsonResponse(response, content, 200);
    }

    public static void errorResponse(ServerResponse response, String error) {
        errorResponse(response, error, 404);
    }

    public static void errorResponse(ServerResponse response, String error, int statusCode) {
        response.headers().add("content-type", "application/json");
        response.status(statusCode);
        response.send(String.format("{\"error\": \"%s\"}", error));
    }

    private static void jsonResponse(ServerResponse response, Object content, int statusCode) throws JsonProcessingException {
        response.headers().add("content-type", "application/json");
        response.status(statusCode);
        response.send(objMapper.writeValueAsString(content));
    }

    public static void healthCheck(ServerResponse response) throws JsonProcessingException {
        final var map = Map.of("message", "Never Forget!");
        jsonResponse(response, map);
    }
}
