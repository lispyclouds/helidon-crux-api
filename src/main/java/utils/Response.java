package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.helidon.common.http.Http;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;

import java.util.Map;

public class Response {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void jsonResponse(ServerResponse response, Object content) throws JsonProcessingException {
        jsonResponse(response, content, Http.Status.OK_200);
    }

    public static void errorResponse(ServerResponse response, String error) {
        errorResponse(response, error, Http.Status.NOT_FOUND_404);
    }

    public static void errorResponse(ServerResponse response, String error, Http.Status statusCode) {
        response.headers().add("content-type", "application/json");
        response.status(statusCode);
        response.send(String.format("{\"error\": \"%s\"}", error));
    }

    public static void jsonResponse(ServerResponse response, Object content, Http.Status statusCode) throws JsonProcessingException {
        response.headers().add("content-type", "application/json");
        response.status(statusCode);

        final var respMap = Map.of("message", content);
        response.send(mapper.writeValueAsString(respMap));
    }

    public static void respond(ThrowingBiConsumer<ServerRequest, ServerResponse, Exception> fn, ServerRequest req, ServerResponse res) {
        try {
            fn.accept(req, res);
        } catch (Exception e) {
            errorResponse(res, e.getMessage(), Http.Status.INTERNAL_SERVER_ERROR_500);
        }
    }
}
