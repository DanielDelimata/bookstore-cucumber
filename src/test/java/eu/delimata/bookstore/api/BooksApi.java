package eu.delimata.bookstore.api;

import eu.delimata.bookstore.model.Book;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static eu.delimata.bookstore.enums.HttpCode.OK;

public class BooksApi extends BaseApi {

    private static final String BOOKS = "/api/v1/Books";

    @Step
    public Response getAll() {
        return givenSpec().when().get(BOOKS);
    }

    @Step
    public Response getById(int id) {
        return givenSpec().pathParam("id", id).when().get(BOOKS + "/{id}");
    }

    @Step
    public Response create(Object book) {
        return givenSpec().body(book).when().post(BOOKS);
    }

    @Step
    public Response update(int id, Book book) {
        return givenSpec().pathParam("id", id).body(book).when().put(BOOKS + "/{id}");
    }

    @Step
    public Response delete(int id) {
        return givenSpec().pathParam("id", id).when().delete(BOOKS + "/{id}");
    }

    public Book getBookById(Integer id) {
        return getById(id).then().statusCode(OK.toInt()).extract().as(Book.class);
    }
}
