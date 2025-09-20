package eu.delimata.bookstore.api;


import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public abstract class BaseApi {

    protected final RequestSpecification spec;

    protected BaseApi() {
        String baseUrl = System.getProperty("baseUrl",
                System.getenv().getOrDefault("BASE_URL", "https://fakerestapi.azurewebsites.net"));
        this.spec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json")
                .setConfig(RestAssured.config()
                        .httpClient(HttpClientConfig.httpClientConfig()
                                .setParam("http.connection.timeout", 5_000)
                                .setParam("http.socket.timeout", 10_000)))
                .addFilter(new AllureRestAssured())
                .log(LogDetail.URI)
                .build();
    }

    protected RequestSpecification givenSpec() {
        return given().spec(spec);
    }
}
