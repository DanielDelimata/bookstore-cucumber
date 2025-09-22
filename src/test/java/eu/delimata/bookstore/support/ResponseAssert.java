package eu.delimata.bookstore.support;

import eu.delimata.bookstore.enums.HttpCode;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public final class ResponseAssert  {

    public static void assertHttpStatusCode(Response response, HttpCode expected) {
        int code = response.getStatusCode();
        HttpCode actual = HttpCode.resolve(code);
        assertThat(code)
                .withFailMessage("Expected HTTP status %s (%d) but was %s (%d)",
                        expected.name(), expected.toInt(),
                        actual != null ? actual.name() : "UNKNOWN", code)
                .isEqualTo(expected.toInt());
    }

    public static void assertHttpStatusCode(Response response, HttpCode... expected) {
        assertThat(expected)
                .withFailMessage("No expected HTTP codes provided.")
                .isNotEmpty();

        int code = response.getStatusCode();
        HttpCode actual = HttpCode.resolve(code);

        Integer[] expectedCodes = Arrays.stream(expected)
                .mapToInt(HttpCode::toInt)
                .boxed()
                .toArray(Integer[]::new);

        String expectedStr = Arrays.stream(expected)
                .map(e -> e.name() + " (" + e.toInt() + ")")
                .collect(Collectors.joining(", "));

        assertThat(code)
                .withFailMessage(
                        "Expected HTTP status to be one of [%s], but was %s (%d)",
                        expectedStr,
                        actual != null ? actual.name() : "UNKNOWN",
                        code
                )
                .isIn(expectedCodes);
    }

    public static void assertHttpStatusCode(Response response, Collection<HttpCode> expected) {
        assertHttpStatusCode(response, expected.toArray(new HttpCode[0]));
    }
}
