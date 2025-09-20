package eu.delimata.bookstore;

import eu.delimata.bookstore.api.AuthorsApi;
import eu.delimata.bookstore.api.BooksApi;
import eu.delimata.bookstore.model.Author;
import eu.delimata.bookstore.model.Book;
import io.restassured.response.Response;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class BookstoreWorld {

    @Setter(AccessLevel.NONE)
    private final BooksApi booksApi = new BooksApi();
    @Setter(AccessLevel.NONE)
    private final AuthorsApi authorsApi = new AuthorsApi();

    private Integer lastBookId;
    private Integer lastAuthorId;

    private Book lastBookPayload;
    private Author lastAuthorPayload;

    private Response lastResponse;
    private long lastOperationMillis;

    @Setter(AccessLevel.NONE)
    private final Map<String, Object> context = new HashMap<>();
}
