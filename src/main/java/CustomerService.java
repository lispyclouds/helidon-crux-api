import crux.api.ICruxAPI;
import data.Customer;
import io.helidon.common.http.Http;
import io.helidon.config.Config;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import utils.ThrowingFunction;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static utils.Response.errorResponse;
import static utils.Response.jsonResponse;

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
            .get("/", this::healthCheck)
            .get("/customers", this::listCustomers)
            .post("/customers", this::addCustomer)
            .delete("/customers/{id}", this::deleteCustomer);
    }

    public void healthCheck(ServerRequest request, ServerResponse response) {
        jsonResponse(response, "Never Forget!");
    }

    public void listCustomers(ServerRequest request, ServerResponse response) {
        final var query = DB.datafy(
            """
            {:find  [(eql/project ?customer [:id :firstName :lastName :email])]
             :where [[?customer :type :customer]]}
            """
        );

        try {
            final var customers = this.node
                .db()
                .query(query)
                .stream()
                .map(result -> result.get(0)) // List of results, take first.
                .map(ThrowingFunction.unchecked(result -> DB.objectify(result, Customer.class)))
                .collect(Collectors.toList());

            jsonResponse(response, customers);
        } catch (Exception ex) {
            errorResponse(response, ex.getMessage(), Http.Status.BAD_REQUEST_400);
        }
    }

    public void addCustomer(ServerRequest request, ServerResponse response) {
        final var query =
            """
            [[:crux.tx/put
              {:crux.db/id :customers/c-%s
               :type       :customer
               :id         "%s"
               :firstName  "%s"
               :lastName   "%s"
               :email      "%s"}]]
            """;
        final var id = UUID.randomUUID().toString();

        // TODO: Use a ThrowingConsumer when needed
        request
            .content()
            .as(Customer.class)
            .thenAccept(customer -> {
                try {
                    final var formatted = String.format(
                        query, id, id, customer.firstName(), customer.lastName(), customer.email()
                    );
                    this.node.submitTx((List<List<?>>) DB.datafy(formatted));
                    jsonResponse(response, id, Http.Status.ACCEPTED_202);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            })
            .exceptionally(ex -> {
                errorResponse(response, ex.getMessage(), Http.Status.BAD_REQUEST_400);
                return null;
            });
    }

    public void deleteCustomer(ServerRequest request, ServerResponse response) {
        final var id = request.path().param("id");
        final var query = String.format(
            """
            [[:crux.tx/delete :customers/c-%s]]
            """,
            id
        );

        this.node.submitTx((List<List<?>>) DB.datafy(query));
        jsonResponse(response, "OK");
    }
}
