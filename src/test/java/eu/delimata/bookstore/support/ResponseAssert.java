package eu.delimata.bookstore.support;

import io.restassured.response.Response;
import org.assertj.core.api.AbstractIntegerAssert;

import static org.assertj.core.api.Assertions.assertThat;

public final class ResponseAssert  {

    public static final String HTTP_STATUS = "HTTP status";

    public static AbstractIntegerAssert<?> assertHttpStatusCode(Response world) {
        return assertThat(world.then().extract().statusCode()).as(HTTP_STATUS);
    }
}
