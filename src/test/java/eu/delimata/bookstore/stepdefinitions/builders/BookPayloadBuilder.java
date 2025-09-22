package eu.delimata.bookstore.stepdefinitions.builders;

import java.util.HashMap;
import java.util.Map; /**
 * Builder for creating Map-based payloads for API calls.
 * Useful for testing scenarios with missing or invalid data.
 */
public class BookPayloadBuilder {
    private final Map<String, Object> payload;

    private BookPayloadBuilder() {
        this.payload = new HashMap<>();
    }

    public static BookPayloadBuilder validPayload() {
        return new BookPayloadBuilder()
                .withTitle("Test Title")
                .withDescription("Test Description")
                .withPageCount(100)
                .withExcerpt("Test Excerpt")
                .withPublishDate("2020-01-01T00:00:00.000Z");
    }

    public BookPayloadBuilder withId(Integer id) {
        payload.put("id", id);
        return this;
    }

    public BookPayloadBuilder withTitle(String title) {
        payload.put("title", title);
        return this;
    }

    public BookPayloadBuilder withDescription(String description) {
        payload.put("description", description);
        return this;
    }

    public BookPayloadBuilder withPageCount(Integer pageCount) {
        payload.put("pageCount", pageCount);
        return this;
    }

    public BookPayloadBuilder withExcerpt(String excerpt) {
        payload.put("excerpt", excerpt);
        return this;
    }

    public BookPayloadBuilder withPublishDate(String publishDate) {
        payload.put("publishDate", publishDate);
        return this;
    }

    public BookPayloadBuilder remove(String fieldName) {
        String mappedField = mapFieldName(fieldName);
        payload.remove(mappedField);
        return this;
    }

    public Map<String, Object> build() {
        return new HashMap<>(payload);
    }

    private String mapFieldName(String fieldName) {
        return switch (fieldName) {
            case "page count" -> "pageCount";
            case "publish date" -> "publishDate";
            default -> fieldName;
        };
    }
}
