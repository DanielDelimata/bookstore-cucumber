package eu.delimata.bookstore.stepdefinitions;

import eu.delimata.bookstore.BookstoreWorld;
import eu.delimata.bookstore.model.Book;
import eu.delimata.bookstore.stepdefinitions.builders.BookBuilder;
import eu.delimata.bookstore.utils.TestData;
import io.restassured.path.json.JsonPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static eu.delimata.bookstore.enums.HttpCode.OK;
import static eu.delimata.bookstore.support.ResponseAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Helper class containing common operations and validations for book-related steps.
 * Reduces duplication and improves maintainability of step definitions.
 */
@Slf4j
@RequiredArgsConstructor
public class BookStepHelpers {

    private final BookstoreWorld world;

    /**
     * Ensures that a book payload exists in the world context.
     * Creates a random book if none exists.
     */
    public void ensureBookPayloadExists() {
        if (world.getLastBookPayload() == null) {
            int id = TestData.uniqueId();
            Book book = BookBuilder.aBook()
                    .withId(id)
                    .buildRandom();
            world.setLastBookPayload(book);
            log.debug("Created random book payload with ID: {}", id);
        }
    }

    /**
     * Ensures that a book exists both in payload and in the bookstore.
     * Creates and stores a book if necessary.
     */
    public void ensureBookExists() {
        if (world.getLastBookPayload() == null) {
            int id = TestData.uniqueId();
            Book book = BookBuilder.aBook()
                    .withId(id)
                    .buildRandom();

            assertThat(world.getBooksApi().create(book)).hasStatusCode(OK);
            world.setLastBookPayload(book);
            world.setLastBookId(id);
            log.debug("Created and stored book with ID: {}", id);
        }
    }

    /**
     * Creates a book in the bookstore and returns it.
     *
     * @return the created book
     */
    public Book createAndReturnBook() {
        int id = TestData.uniqueId();
        Book book = BookBuilder.aBook()
                .withId(id)
                .buildRandom();

        assertThat(world.getBooksApi().create(book)).hasStatusCode(OK);
        log.debug("Created book with ID: {}", id);
        return book;
    }

    /**
     * Deletes a book by ID and verifies the operation was successful.
     *
     * @param bookId the ID of the book to delete
     */
    public void deleteBook(int bookId) {
        assertThat(world.getBooksApi().delete(bookId)).hasStatusCode(OK);
        log.debug("Deleted book with ID: {}", bookId);
    }

    /**
     * Verifies that a book appears in the book list.
     */
    public void verifyBookInList() {
        String listJson = world.getBooksApi()
                .getAll()
                .then()
                .statusCode(OK.toInt())
                .extract()
                .asString();

        JsonPath jsonPath = JsonPath.from(listJson);
        List<Integer> bookIds = jsonPath.getList("id", Integer.class);

        assertThat(bookIds)
                .as("Book list should contain the book with ID: %d", world.getLastBookId())
                .contains(world.getLastBookId());

        log.debug("Verified book with ID {} appears in list", world.getLastBookId());
    }

    /**
     * Verifies that a book's details match the expected payload.
     */
    public void verifyBookInDetails() {
        Book retrievedBook = world.getBooksApi().getBookById(world.getLastBookId());

        assertThat(retrievedBook.id())
                .as("Retrieved book ID should match expected ID")
                .isEqualTo(world.getLastBookPayload().id());

        log.debug("Verified book details for ID: {}", world.getLastBookId());
    }

    /**
     * Returns appropriate book ID for different test cases.
     *
     * @param caseDescription description of the test case
     * @return book ID to use for the test case
     */
    public int getBookIdForCase(String caseDescription) {
        return switch (caseDescription) {
            case "was already removed" -> {
                int id = TestData.uniqueId();
                Book book = BookBuilder.aBook()
                        .withId(id)
                        .buildRandom();
                assertThat(world.getBooksApi().create(book)).hasStatusCode(OK);
                assertThat(world.getBooksApi().delete(id)).hasStatusCode(OK);
                yield id;
            }
            case "never existed" -> 999_999_999;
            case "has an invalid identifier" -> -1;
            default -> throw new IllegalArgumentException("Unsupported test case: " + caseDescription);
        };
    }
}