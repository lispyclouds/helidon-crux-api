import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import crux.api.ICruxAPI;
import data.Customer;
import io.helidon.webserver.ServerResponse;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// Magic ✨
// See: https://dzone.com/articles/how-to-handle-checked-exception-in-lambda-expressi
@FunctionalInterface
interface ThrowingFunction<T, R, E extends Throwable> {

    R apply(T t) throws E;

    static <T, R, E extends Throwable> Function<T, R> unchecked(ThrowingFunction<T, R, E> f) {
        return t -> {
            try {
                return f.apply(t);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }
}

public class Handlers {
    private static final ObjectMapper objMapper = new ObjectMapper();

    public static void jsonResponse(ServerResponse response, Object content) throws JsonProcessingException {
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

    public static void jsonResponse(ServerResponse response, Object content, int statusCode) throws JsonProcessingException {
        response.headers().add("content-type", "application/json");
        response.status(statusCode);

        final var respMap = Map.of("message", content);
        response.send(objMapper.writeValueAsString(respMap));
    }

    public static void healthCheck(ServerResponse response) throws JsonProcessingException {
        jsonResponse(response, "Never Forget!");
    }

    public static void listCustomers(ServerResponse response, ICruxAPI node) throws JsonProcessingException {
        final var query = DB.datafy(
            """
                {:find  [(eql/project ?customer [:id :firstName :lastName :email])]
                 :where [[?customer :type :customer]]}
                """
        );

        final var customers = node
            .db()
            .query(query)
            .stream()
            .map(result -> result.get(0)) // List of results, take first.
            .map(ThrowingFunction.unchecked(result -> DB.objectify(result, Customer.class)))
            .collect(Collectors.toList());

        jsonResponse(response, customers);
    }
}
