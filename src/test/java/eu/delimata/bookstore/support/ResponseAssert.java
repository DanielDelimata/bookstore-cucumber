package eu.delimata.bookstore.support;

import eu.delimata.bookstore.enums.HttpCode;
import io.restassured.response.Response;
import org.assertj.core.api.AbstractAssert;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Custom AssertJ assertion class for HTTP Response validation.
 * Provides fluent API for asserting HTTP status codes with detailed error messages.
 */
public class ResponseAssert extends AbstractAssert<ResponseAssert, Response> {

    private ResponseAssert(Response response) {
        super(response, ResponseAssert.class);
    }

    /**
     * Creates a new instance of ResponseAssert.
     *
     * @param response the response to assert on
     * @return new ResponseAssert instance
     */
    public static ResponseAssert assertThat(Response response) {
        return new ResponseAssert(response);
    }

    /**
     * Asserts that the response has the expected HTTP status code.
     *
     * @param expected the expected HTTP status code
     * @return this assertion instance for method chaining
     */
    public ResponseAssert hasStatusCode(HttpCode expected) {
        isNotNull();

        int actualCode = actual.getStatusCode();
        HttpCode actualHttpCode = HttpCode.resolve(actualCode);

        if (actualCode != expected.toInt()) {
            failWithMessage(
                    "Expected HTTP status <%s (%d)> but was <%s (%d)>",
                    expected.name(),
                    expected.toInt(),
                    formatHttpCode(actualHttpCode),
                    actualCode
            );
        }

        return this;
    }

    /**
     * Asserts that the response has one of the expected HTTP status codes.
     *
     * @param expected array of expected HTTP status codes
     * @return this assertion instance for method chaining
     */
    public ResponseAssert hasStatusCodeIn(HttpCode... expected) {
        return hasStatusCodeIn(Arrays.asList(expected));
    }

    /**
     * Asserts that the response has one of the expected HTTP status codes.
     *
     * @param expected collection of expected HTTP status codes
     * @return this assertion instance for method chaining
     */
    public ResponseAssert hasStatusCodeIn(Collection<HttpCode> expected) {
        isNotNull();

        if (expected == null || expected.isEmpty()) {
            failWithMessage("Expected HTTP codes collection cannot be null or empty");
        }

        int actualCode = actual.getStatusCode();
        HttpCode actualHttpCode = HttpCode.resolve(actualCode);

        Set<Integer> expectedCodes = expected.stream()
                .map(HttpCode::toInt)
                .collect(Collectors.toSet());

        if (!expectedCodes.contains(actualCode)) {
            String expectedStr = expected.stream()
                    .map(code -> String.format("%s (%d)", code.name(), code.toInt()))
                    .collect(Collectors.joining(", "));

            failWithMessage(
                    "Expected HTTP status to be one of [%s] but was <%s (%d)>",
                    expectedStr,
                    formatHttpCode(actualHttpCode),
                    actualCode
            );
        }

        return this;
    }

    /**
     * Asserts that the response has a successful HTTP status code (2xx).
     *
     * @return this assertion instance for method chaining
     */
    public ResponseAssert isSuccessful() {
        isNotNull();

        int actualCode = actual.getStatusCode();

        if (actualCode < 200 || actualCode >= 300) {
            HttpCode actualHttpCode = HttpCode.resolve(actualCode);
            failWithMessage(
                    "Expected successful HTTP status (2xx) but was <%s (%d)>",
                    formatHttpCode(actualHttpCode),
                    actualCode
            );
        }

        return this;
    }

    /**
     * Asserts that the response has a client error HTTP status code (4xx).
     *
     * @return this assertion instance for method chaining
     */
    public ResponseAssert isClientError() {
        isNotNull();

        int actualCode = actual.getStatusCode();

        if (actualCode < 400 || actualCode >= 500) {
            HttpCode actualHttpCode = HttpCode.resolve(actualCode);
            failWithMessage(
                    "Expected client error HTTP status (4xx) but was <%s (%d)>",
                    formatHttpCode(actualHttpCode),
                    actualCode
            );
        }

        return this;
    }

    /**
     * Asserts that the response has a server error HTTP status code (5xx).
     *
     * @return this assertion instance for method chaining
     */
    public ResponseAssert isServerError() {
        isNotNull();

        int actualCode = actual.getStatusCode();

        if (actualCode < 500 || actualCode >= 600) {
            HttpCode actualHttpCode = HttpCode.resolve(actualCode);
            failWithMessage(
                    "Expected server error HTTP status (5xx) but was <%s (%d)>",
                    formatHttpCode(actualHttpCode),
                    actualCode
            );
        }

        return this;
    }

    private String formatHttpCode(HttpCode httpCode) {
        return httpCode != null ? httpCode.name() : "UNKNOWN";
    }
}