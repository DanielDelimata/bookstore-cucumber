package eu.delimata.bookstore.stepdefinitions.builders;

import eu.delimata.bookstore.utils.TestData;

import java.util.HashMap;
import java.util.Map; /**
 * Builder for creating Map-based payloads for API calls.
 * Useful for testing scenarios with missing or invalid author data.
 */
public class AuthorPayloadBuilder {
    private final Map<String, Object> payload;

    private AuthorPayloadBuilder() {
        this.payload = new HashMap<>();
    }

    public static AuthorPayloadBuilder validPayload() {
        int id = TestData.uniqueId();
        return new AuthorPayloadBuilder()
                .withId(id)
                .withIdBook(id)
                .withFirstName("Test Name")
                .withLastName("Test Surname");
    }

    public AuthorPayloadBuilder withId(Integer id) {
        payload.put("id", id);
        return this;
    }

    public AuthorPayloadBuilder withIdBook(Integer idBook) {
        payload.put("idBook", idBook);
        return this;
    }

    public AuthorPayloadBuilder withFirstName(String firstName) {
        payload.put("firstName", firstName);
        return this;
    }

    public AuthorPayloadBuilder withLastName(String lastName) {
        payload.put("lastName", lastName);
        return this;
    }

    public AuthorPayloadBuilder remove(String fieldName) {
        String mappedField = mapFieldName(fieldName);
        payload.remove(mappedField);
        return this;
    }

    public Map<String, Object> build() {
        return new HashMap<>(payload);
    }

    private String mapFieldName(String fieldName) {
        return switch (fieldName) {
            case "display name" -> "firstName";
            case "short bio" -> "lastName";
            case "date of birth" -> "idBook";
            default -> fieldName;
        };
    }
}
