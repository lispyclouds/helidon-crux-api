import com.fasterxml.jackson.core.JsonProcessingException;
import crux.api.ICruxAPI;
import data.Customer;
import io.helidon.config.Config;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import utils.ThrowingFunction;

import java.io.IOException;
import java.util.stream.Collectors;

import static utils.Response.jsonResponse;
import static utils.Response.respond;

public class CustomerService implements Service {
    private final ICruxAPI node;

    CustomerService(Config config) {
        this.node = new DB(config).node;
    }

    public void stop() {
        System.out.println("Stopping DB.");

        try {
            this.node.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Routing.Rules rules) {
        rules
            .get("/", (request, response) -> respond(this::healthCheck, request, response))
            .get("/customers", (request, response) -> respond(this::listCustomers, request, response));
    }

    public void healthCheck(ServerRequest request, ServerResponse response) throws JsonProcessingException {
        jsonResponse(response, "Never Forget!");
    }

    public void listCustomers(ServerRequest request, ServerResponse response) throws JsonProcessingException {
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
