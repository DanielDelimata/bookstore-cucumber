package eu.delimata.bookstore;

import eu.delimata.bookstore.utils.AllureEnvironmentWriter;
import io.cucumber.java.After;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.filter.log.LogDetail;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Hooks {

    private final BookstoreWorld world;

    public Hooks(BookstoreWorld world) {
        this.world = world;
    }

    @BeforeAll
    public static void setAllureEnvironment() {
        Properties properties = System.getProperties();
        Map<String, String> env = new HashMap<>();
        properties.forEach((k, v) -> env.put((String) k, (String) v));
        AllureEnvironmentWriter.writeAllureEnvironment(env);
        RestAssured.config = RestAssured.config()
                .logConfig(LogConfig.logConfig()
                        .enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL)
                        .enablePrettyPrinting(true));
    }

    @After
    public void cleanup(Scenario scenario) {
        // Best-effort cleanup; ignore outcome to keep tests resilient
        try {
            if (world.getLastBookId() != null) {
                world.getBooksApi().delete(world.getLastBookId());
                world.setLastBookId(null);
            }
        } catch (Exception exception) {
            scenario.log("Failed to delete book with id " + world.getLastBookId() + ": " + exception.getMessage());
        }

        try {
            if (world.getLastAuthorId() != null) {
                world.getAuthorsApi().delete(world.getLastAuthorId());
                world.setLastAuthorId(null);
            }
        }  catch (Exception exception) {
            scenario.log("Failed to delete author with id " + world.getLastAuthorId() + ": " + exception.getMessage());
        }
    }
}