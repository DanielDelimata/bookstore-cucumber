package eu.delimata.bookstore.support;

import io.restassured.response.Response;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import static eu.delimata.bookstore.enums.HttpCode.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public final class ResponseAssert extends AbstractAssert<ResponseAssert, Response> {

    public static final String HTTP_STATUS = "HTTP status";

    private ResponseAssert(Response actual) { super(actual, ResponseAssert.class); }

    public static ResponseAssert assertThat(Response actual) { return new ResponseAssert(actual); }

    public ResponseAssert assertStatusOk() {
        Assertions.assertThat(actual.statusCode()).as(HTTP_STATUS)
                .isEqualTo(OK.toInt());
        Assertions.assertThat(actual.contentType()).containsIgnoringCase("json");
        return this;
    }

    public ResponseAssert assertStatusCreated() {
        Assertions.assertThat(actual.statusCode()).as(HTTP_STATUS)
                .isEqualTo(CREATED.toInt());
        return this;
    }

    public ResponseAssert hasLocation() {
        Assertions.assertThat(actual.getHeader("Location")).as("Location").isNotBlank();
        return this;
    }

    public ResponseAssert assertStatusNoContent() {
        Assertions.assertThat(actual.statusCode()).as(HTTP_STATUS)
                .isEqualTo(NO_CONTENT.toInt());
        Assertions.assertThat(actual.asString()).as("Body for 204").isBlank();
        return this;
    }

    public ResponseAssert matchesSchema(String classpathSchema) {
        actual.then().body(matchesJsonSchemaInClasspath(classpathSchema));
        return this;
    }
}
