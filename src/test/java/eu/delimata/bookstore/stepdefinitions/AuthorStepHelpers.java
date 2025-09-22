package eu.delimata.bookstore.stepdefinitions;

import eu.delimata.bookstore.BookstoreWorld;
import eu.delimata.bookstore.model.Author;
import eu.delimata.bookstore.stepdefinitions.builders.AuthorBuilder;
import eu.delimata.bookstore.utils.TestData;
import io.restassured.path.json.JsonPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static eu.delimata.bookstore.enums.HttpCode.OK;
import static eu.delimata.bookstore.support.ResponseAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Helper class containing common operations and validations for author-related steps.
 * Reduces duplication and improves maintainability of step definitions.
 */
@Slf4j
@RequiredArgsConstructor
public class AuthorStepHelpers {

    private final BookstoreWorld world;

    /**
     * Ensures that an author payload exists in the world context.
     * Creates a random author if none exists.
     */
    public void ensureAuthorPayloadExists() {
        if (world.getLastAuthorPayload() == null) {
            int id = TestData.uniqueId();
            Author author = AuthorBuilder.anAuthor()
                    .withId(id)
                    .withIdBook(id)
                    .buildRandom();
            world.setLastAuthorPayload(author);
            log.debug("Created random author payload with ID: {}", id);
        }
    }

    /**
     * Ensures that an author exists both in payload and in the bookstore.
     * Creates and stores an author if necessary.
     */
    public void ensureAuthorExists() {
        if (world.getLastAuthorPayload() == null) {
            int id = TestData.uniqueId();
            Author author = AuthorBuilder.anAuthor()
                    .withId(id)
                    .withIdBook(id)
                    .buildRandom();

            assertThat(world.getAuthorsApi().create(author)).hasStatusCode(OK);
            world.setLastAuthorPayload(author);
            world.setLastAuthorId(id);
            log.debug("Created and stored author with ID: {}", id);
        }
    }

    /**
     * Creates an author in the bookstore and returns it.
     *
     * @return the created author
     */
    public Author createAndReturnAuthor() {
        int id = TestData.uniqueId();
        Author author = AuthorBuilder.anAuthor()
                .withId(id)
                .withIdBook(id)
                .buildRandom();

        assertThat(world.getAuthorsApi().create(author)).hasStatusCode(OK);
        log.debug("Created author with ID: {}", id);
        return author;
    }

    /**
     * Deletes an author by ID and verifies the operation was successful.
     *
     * @param authorId the ID of the author to delete
     */
    public void deleteAuthor(int authorId) {
        assertThat(world.getAuthorsApi().delete(authorId)).hasStatusCode(OK);
        log.debug("Deleted author with ID: {}", authorId);
    }

    /**
     * Verifies that an author appears in the author list.
     */
    public void verifyAuthorInList() {
        String listJson = world.getAuthorsApi()
                .getAll()
                .then()
                .statusCode(OK.toInt())
                .extract()
                .asString();

        JsonPath jsonPath = JsonPath.from(listJson);
        List<Integer> authorIds = jsonPath.getList("id", Integer.class);

        assertThat(authorIds)
                .as("Author list should contain the author with ID: %d", world.getLastAuthorId())
                .contains(world.getLastAuthorId());

        log.debug("Verified author with ID {} appears in list", world.getLastAuthorId());
    }

    /**
     * Verifies that an author's details match the expected payload.
     */
    public void verifyAuthorInDetails() {
        Author retrievedAuthor = world.getAuthorsApi().getAuthorById(world.getLastAuthorId());

        assertThat(retrievedAuthor.id())
                .as("Retrieved author ID should match expected ID")
                .isEqualTo(world.getLastAuthorPayload().id());

        log.debug("Verified author details for ID: {}", world.getLastAuthorId());
    }

    /**
     * Returns appropriate author ID for different test cases.
     *
     * @param caseDescription description of the test case
     * @return author ID to use for the test case
     */
    public int getAuthorIdForCase(String caseDescription) {
        return switch (caseDescription) {
            case "was already removed" -> {
                int id = TestData.uniqueId();
                Author author = AuthorBuilder.anAuthor()
                        .withId(id)
                        .withIdBook(id)
                        .withFirstName("ToRemove" + id)
                        .withLastName("Delimata")
                        .build();

                assertThat(world.getAuthorsApi().create(author)).hasStatusCode(OK);
                assertThat(world.getAuthorsApi().delete(id)).hasStatusCode(OK);
                yield id;
            }
            case "never existed" -> 987_654_321;
            case "has an invalid identifier" -> -1;
            default -> throw new IllegalArgumentException("Unsupported test case: " + caseDescription);
        };
    }
}