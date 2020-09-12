import clojure.java.api.Clojure;
import clojure.lang.Keyword;
import crux.api.Crux;
import crux.api.ICruxAPI;
import io.helidon.config.Config;

import java.time.Duration;
import java.util.Map;

public class DB {
    private ICruxAPI node;

    public DB() {
        final var config = Config.create();
        final var dbName = config.get("DB_NAME").asString().orElse("customers");
        final var dbHost = config.get("DB_HOST").asString().orElse("localhost");
        final var dbPort = config.get("DB_PORT").asInt().orElse(5432);
        final var dbUser = config.get("DB_USER").asString().orElse("helidon");
        final var dbPassword = config.get("DB_PASSWORD").asString().orElse("helidon");

        final var nodeConfig = String.format("""
                {:crux.node/topology [crux.jdbc/topology]
                 :crux.jdbc/dbtype   "postgresql"
                 :crux.jdbc/dbname   "%s"
                 :crux.jdbc/host     "%s"
                 :crux.jdbc/port     %d
                 :crux.jdbc/user     "%s"
                 :crux.jdbc/password "%s"}
                """,
            dbName, dbHost, dbPort, dbUser, dbPassword
        );

        this.node = Crux.startNode((Map<Keyword, ?>) datafy(nodeConfig));
        this.node.sync(Duration.ofSeconds(30)); // Become consistent for a max of 30s
    }

    public static Object datafy(String raw) {
        return Clojure.read(raw);
    }
}
