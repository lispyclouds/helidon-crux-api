package utils;

import io.helidon.common.http.Http;
import io.helidon.webserver.ServerResponse;

import java.util.Map;

public class Response {

    public static void jsonResponse(ServerResponse response, Object content) {
        jsonResponse(response, content, Http.Status.OK_200);
    }

    public static void errorResponse(ServerResponse response, String error) {
        errorResponse(response, error, Http.Status.NOT_FOUND_404);
    }

    public static void errorResponse(ServerResponse response, String error, Http.Status statusCode) {
        response.headers().add("content-type", "application/json");
        response.status(statusCode);
        response.send(String.format(
            """
            {
              "message": "%s"
            }
            """,
            error
        ));
    }

    public static void jsonResponse(ServerResponse response, Object content, Http.Status statusCode) {
        response.status(statusCode);
        response.send(Map.of("message", content));
    }
}
