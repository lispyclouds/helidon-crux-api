import clojure.java.api.Clojure;
import clojure.lang.IFn;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import crux.api.Crux;
import crux.api.ICruxAPI;
import io.helidon.config.Config;

import java.time.Duration;

public class DB {
    public final ICruxAPI node;
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final static IFn toJson;

    static {
        Clojure.var("clojure.core", "require")
            .invoke(Clojure.read("jsonista.core"));
        toJson = Clojure.var("jsonista.core", "write-value-as-string");
    }

    public DB(Config config) {
        final var dbName = config.get("DB_NAME").asString().orElse("customers");
        final var dbHost = config.get("DB_HOST").asString().orElse("localhost");
        final var dbPort = config.get("DB_PORT").asInt().orElse(5432);
        final var dbUser = config.get("DB_USER").asString().orElse("helidon");
        final var dbPassword = config.get("DB_PASSWORD").asString().orElse("helidon");
        final var connectionPool = datafy(
            """
            {:dialect crux.jdbc.psql/->dialect
             :db-spec {:dbname   "%s"
                       :host     "%s"
                       :port     %d
                       :user     "%s"
                       :password "%s"}}
            """.formatted(dbName, dbHost, dbPort, dbUser, dbPassword)
        );

        this.node = Crux.startNode(configurator -> {
            configurator.with("crux/tx-log", txLog -> {
                txLog.module("crux.jdbc/->tx-log");
                txLog.set("connection-pool", connectionPool);
            });
            configurator.with("crux/document-store", docStore -> {
                docStore.module("crux.jdbc/->document-store");
                docStore.set("connection-pool", connectionPool);
            });
        });
        this.node.sync(Duration.ofSeconds(30)); // Become consistent for a max of 30s
    }

    public static Object datafy(String raw) {
        return Clojure.read(raw);
    }

    public static <T> T objectify(Object data, Class<T> cls) throws JsonProcessingException {
        return objectMapper.readValue((String) toJson.invoke(data), cls);
    }
}
