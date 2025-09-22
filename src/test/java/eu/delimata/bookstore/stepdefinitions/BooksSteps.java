package eu.delimata.bookstore.stepdefinitions;

import eu.delimata.bookstore.BookstoreWorld;
import eu.delimata.bookstore.model.Book;
import eu.delimata.bookstore.stepdefinitions.builders.BookBuilder;
import eu.delimata.bookstore.stepdefinitions.builders.BookPayloadBuilder;
import eu.delimata.bookstore.utils.TestData;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static eu.delimata.bookstore.enums.HttpCode.*;
import static eu.delimata.bookstore.support.ResponseAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class BooksSteps {

    private final BookstoreWorld world;
    private final BookStepHelpers helpers;

    public BooksSteps(BookstoreWorld world) {
        this.world = world;
        this.helpers = new BookStepHelpers(world);
    }

    // GIVEN steps - Setup/Preparation

    @And("I have some unique data of a book")
    public void iHaveSomeUniqueDataOfABook() {
        int id = TestData.uniqueId();
        Book book = BookBuilder.aBook()
                .withId(id)
                .buildRandom();
        world.setLastBookPayload(book);
    }

    @Given("a book exists in the bookstore")
    public void aBookPreparedExists() {
        Book book = BookBuilder.aBook()
                .withId(1)
                .withTitle("Book 1")
                .withDescription("Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\n")
                .withPageCount(100)
                .withExcerpt("""
                        Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\\n
                        Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\\n
                        Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\\n
                        Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\\n
                        Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\\n
                        """)
                .withPublishDate("2025-09-20T08:00:07.7606597+00:00")
                .build();

        world.setLastBookPayload(book);
        world.setLastBookId(book.id());
    }

    @Given("I have removed that book from the bookstore")
    public void iHaveRemovedThatBook() {
        helpers.ensureBookExists();
        helpers.deleteBook(world.getLastBookId());
    }

    // WHEN steps - Actions

    @When("I add a new book to the bookstore")
    public void iAddANewBook() {
        helpers.ensureBookPayloadExists();
        world.setLastResponse(world.getBooksApi().create(world.getLastBookPayload()));
        world.setLastBookId(world.getLastBookPayload().id());
        log.debug("Added book with ID: {}", world.getLastBookId());
    }

    @When("I edit selected information of that book")
    public void iEditSelectedInformationOfBook() {
        Book updated = BookBuilder.fromBook(world.getLastBookPayload())
                .withTitle(world.getLastBookPayload().title() + " (2nd ed.)")
                .withPageCount(world.getLastBookPayload().pageCount() + 1)
                .build();

        world.setLastResponse(world.getBooksApi().update(world.getLastBookId(), updated));
        assertThat(world.getLastResponse()).hasStatusCode(OK);
        world.setLastBookPayload(updated);
        log.debug("Updated book with ID: {}", world.getLastBookId());
    }

    @When("I change selected information of that book")
    public void iChangeSelectedInfoOfThatBook() {
        iEditSelectedInformationOfBook();
    }

    @When("I remove that book from the bookstore")
    public void iRemoveThatBook() {
        world.setLastResponse(world.getBooksApi().delete(world.getLastBookId()));
        assertThat(world.getLastResponse()).hasStatusCode(OK);
        log.debug("Removed book with ID: {}", world.getLastBookId());
    }

    @When("I attempt to remove the same book again")
    public void attemptRemoveSameBookAgain() {
        world.setLastResponse(world.getBooksApi().delete(world.getLastBookId()));
        log.debug("Attempted to remove book with ID: {} again", world.getLastBookId());
    }

    @When("I open the details of that book")
    public void iOpenDetailsOfThatBook() {
        world.setLastResponse(world.getBooksApi().getById(world.getLastBookId()));
    }

    @When("^I try to open the details of a book that (.*)$")
    public void iTryToOpenDetailsOfNonexistentBook(String caseDescription) {
        int bookId = helpers.getBookIdForCase(caseDescription.trim());
        world.setLastResponse(world.getBooksApi().getById(bookId));
        log.debug("Tried to get details of book with ID: {} (case: {})", bookId, caseDescription);
    }

    @When("^I try to add a new book with missing data (.*)$")
    public void iTryToAddBookWithMissingData(String missingField) {
        Map<String, Object> payload = BookPayloadBuilder.validPayload()
                .withId(TestData.uniqueId())
                .remove(missingField.trim())
                .build();

        world.setLastResponse(world.getBooksApi().create(payload));
        log.debug("Attempted to add book with missing field: {}", missingField);
    }

    @When("I attempt to update the book in an ambiguous or inconsistent way")
    public void attemptAmbiguousUpdate() {
        Book originalBook = helpers.createAndReturnBook();

        Book mismatchedBook = BookBuilder.fromBook(originalBook)
                .withId(originalBook.id() + 1) // Mismatched ID
                .build();

        world.setLastResponse(world.getBooksApi().update(originalBook.id(), mismatchedBook));
        log.debug("Attempted ambiguous update: path ID {} vs payload ID {}",
                originalBook.id(), mismatchedBook.id());
    }

    // THEN steps - Verifications

    @Then("I see a confirmation that the book was added")
    public void iSeeConfirmationBookAdded() {
        assertThat(world.getLastResponse()).hasStatusCode(OK);
    }

    @Then("the book is visible in the list and in its details")
    public void theBookIsVisibleInListAndDetails() {
        helpers.verifyBookInDetails();
        helpers.verifyBookInList();
    }

    @Then("the book details include title, description, page count, and publishing date")
    public void bookDetailsIncludeStandardInformation() {
        Book book = world.getBooksApi().getBookById(world.getLastBookId());

        assertThat(book.title())
                .as("Book title should not be blank")
                .isNotBlank();
        assertThat(book.description())
                .as("Book description should not be blank")
                .isNotBlank();
        assertThat(book.pageCount())
                .as("Book page count should be greater than 0")
                .isGreaterThan(0);
        assertThat(book.publishDate())
                .as("Book publish date should not be blank")
                .isNotBlank();
    }

    @Then("the details reflect the information provided when it was added")
    public void detailsReflectInfoProvidedWhenAdded() {
        Book retrievedBook = world.getLastResponse()
                .then()
                .statusCode(OK.toInt())
                .extract()
                .as(Book.class);

        assertThat(retrievedBook.title())
                .as("Retrieved book title should match the original")
                .isEqualTo(world.getLastBookPayload().title());
    }

    @Then("the changes are visible in the book details")
    public void changesVisibleInBookDetails() {
        Book book = world.getBooksApi().getBookById(world.getLastBookId());
        assertThat(book.title())
                .as("Updated book title should contain '(2nd ed.)'")
                .contains("(2nd ed.)");
    }

    @Then("the updated information about the book is visible to users")
    public void updatedInformationVisibleToUsers() {
        changesVisibleInBookDetails();
    }

    @Then("I should see that the book is no longer available to users")
    @Then("the book no longer appears in search or details")
    @Then("the system informs me that the book item is not available")
    public void theBookIsNoLongerAvailable() {
        assertThat(world.getLastResponse()).hasStatusCode(NOT_FOUND);
    }

    @Then("the addition of a book is rejected with a clear message indicating which data must be provided")
    public void additionRejectedWithClearMessage() {
        assertThat(world.getLastResponse())
                .hasStatusCodeIn(BAD_REQUEST, UNPROCESSABLE_ENTITY);
    }

    @Then("the update of book is rejected with guidance on how to correctly specify the item to change")
    public void updateRejectedWithGuidance() {
        assertThat(world.getLastResponse())
                .hasStatusCodeIn(BAD_REQUEST, CONFLICT, UNPROCESSABLE_ENTITY);
    }

    @Then("the system informs me the book item is already unavailable and no further changes are made")
    public void systemInformsItemUnavailableOnSecondDelete() {
        assertThat(world.getLastResponse())
                .hasStatusCodeIn(NOT_FOUND, BAD_REQUEST, NO_CONTENT);
    }
}