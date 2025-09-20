package eu.delimata.bookstore.api;

import eu.delimata.bookstore.model.Author;
import io.qameta.allure.Step;
import io.restassured.response.Response;

public class AuthorsApi extends BaseApi {

    private static final String AUTHORS = "/api/v1/Authors";

    @Step
    public Response getAll() {
        return givenSpec().get(AUTHORS);
    }

    @Step
    public Response getById(int id) {
        return givenSpec().pathParam("id", id).get(AUTHORS + "/{id}");
    }

    @Step
    public Response create(Object author) {
        return givenSpec().body(author).post(AUTHORS);
    }

    @Step
    public Response update(int id, Author author) {
        return givenSpec().pathParam("id", id).body(author).put(AUTHORS + "/{id}");
    }

    @Step
    public Response delete(int id) {
        return givenSpec().pathParam("id", id).delete(AUTHORS + "/{id}");
    }
}
