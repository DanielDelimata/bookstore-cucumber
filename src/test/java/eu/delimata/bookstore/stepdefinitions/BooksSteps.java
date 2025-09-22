package eu.delimata.bookstore.stepdefinitions;

import eu.delimata.bookstore.BookstoreWorld;
import eu.delimata.bookstore.model.Book;
import eu.delimata.bookstore.utils.TestData;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.path.json.JsonPath;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static eu.delimata.bookstore.enums.HttpCode.BAD_REQUEST;
import static eu.delimata.bookstore.enums.HttpCode.CONFLICT;
import static eu.delimata.bookstore.enums.HttpCode.NOT_FOUND;
import static eu.delimata.bookstore.enums.HttpCode.NO_CONTENT;
import static eu.delimata.bookstore.enums.HttpCode.OK;
import static eu.delimata.bookstore.enums.HttpCode.UNPROCESSABLE_ENTITY;
import static eu.delimata.bookstore.support.ResponseAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class BooksSteps {
    
    private final BookstoreWorld world;

    public BooksSteps(BookstoreWorld world) {
        this.world = world;
    }

    @And("I have some unique data of a book")
    public void iHaveSomeUniqueDataOfABook() {
        int id = TestData.uniqueId();
        world.setLastBookPayload(TestData.randomBook(id));
    }

    @Given("a book exists in the bookstore")
    public void aBookPreparedExists() {
        // Use fixed data to simplify validation
        world.setLastBookPayload(new Book(
                1,
                "Book 1", 
                "Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\n", 
                100,
                """
                        Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\\n
                        Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\\n
                        Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\\n
                        Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\\n
                        Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\\n
                        """,
                "2025-09-20T08:00:07.7606597+00:00"));
        world.setLastBookId(world.getLastBookPayload().id());
    }

    @When("I add a new book to the bookstore")
    public void iAddANewBook() {
        if (world.getLastBookPayload() == null) {
            int id = TestData.uniqueId();
            world.setLastBookPayload(TestData.randomBook(id));
        }
        world.setLastResponse(world.getBooksApi().create(world.getLastBookPayload()));
        world.setLastBookId(world.getLastBookPayload().id());
    }

    @Then("the book is visible in the list and in its details")
    public void theBookIsVisibleInListAndDetails() {
        Book book = world.getBooksApi().getBookById(world.getLastBookId());
        assertThat(book.id()).isEqualTo(world.getLastBookPayload().id());

        String listJson = world.getBooksApi().getAll().then().statusCode(OK.toInt()).extract().asString();
        JsonPath jp = JsonPath.from(listJson);
        List<Integer> ids = jp.getList("id", Integer.class);
        assertThat(ids).contains(world.getLastBookId());
    }

    @When("I edit selected information of that book")
    public void iEditSelectedInformationOfBook() {
        Book updated = new Book(
                world.getLastBookPayload().id(),
                world.getLastBookPayload().title() + " (2nd ed.)",
                world.getLastBookPayload().description(),
                world.getLastBookPayload().pageCount() + 1,
                world.getLastBookPayload().excerpt(),
                world.getLastBookPayload().publishDate()
        );
        world.setLastResponse(world.getBooksApi().update(world.getLastBookId(), updated));
        assertThat(world.getLastResponse()).hasStatusCode(OK);
        world.setLastBookPayload(updated);
    }

    @Then("the changes are visible in the book details")
    public void changesVisibleInBookDetails() {
        Book book = world.getBooksApi().getBookById(world.getLastBookId());
        assertThat(book.title()).contains("(2nd ed.)");
    }

    @When("I remove that book from the bookstore")
    public void iRemoveThatBook() {
        world.setLastResponse(world.getBooksApi().delete(world.getLastBookId()));
        assertThat(world.getLastResponse()).hasStatusCode(OK);
    }

    @Then("I should see that the book is no longer available to users")
    public void theBookIsNoLongerAvailable() {
        assertThat(world.getLastResponse()).hasStatusCode(NOT_FOUND);
    }

    @Then("I see a confirmation that the book was added")
    public void iSeeConfirmationBookAdded() {
        assertThat(world.getLastResponse()).hasStatusCode(OK);
    }

    @Then("the book details include title, description, page count, and publishing date")
    public void bookDetailsIncludeStandardInformation() {
        Book book = world.getBooksApi().getBookById(world.getLastBookId());
        assertThat(book.title()).isNotBlank();
        assertThat(book.description()).isNotBlank();
        assertThat(book.pageCount()).isGreaterThan(0);
        assertThat(book.publishDate()).isNotBlank();
    }

    @When("I open the details of that book")
    public void iOpenDetailsOfThatBook() {
        world.setLastResponse(world.getBooksApi().getById(world.getLastBookId()));
    }

    @Then("the details reflect the information provided when it was added")
    public void detailsReflectInfoProvidedWhenAdded() {
        Book book = world.getLastResponse().then().statusCode(OK.toInt()).extract().as(Book.class);
        assertThat(book.title()).isEqualTo(world.getLastBookPayload().title());
    }

    @When("I change selected information of that book")
    public void iChangeSelectedInfoOfThatBook() {
        iEditSelectedInformationOfBook();
    }

    @Then("the updated information about the book is visible to users")
    public void updatedInformationVisibleToUsers() {
        changesVisibleInBookDetails();
    }

    @Then("the book no longer appears in search or details")
    public void bookNoLongerAppearsInSearchOrDetails() {
        theBookIsNoLongerAvailable();
    }

    @When("^I try to open the details of a book that (.*)$")
    public void iTryToOpenDetailsOfNonexistentBook(String caseDescription) {
        int idToUse;
        switch (caseDescription.trim()) {
            case "was already removed" -> {
                int id = TestData.uniqueId();
                Book book = TestData.randomBook(id);
                assertThat(world.getBooksApi().create(book)).hasStatusCode(OK);
                assertThat(world.getBooksApi().delete(id)).hasStatusCode(OK);
                idToUse = id;
            }
            case "never existed" -> idToUse = 999_999_999;
            case "has an invalid identifier" -> idToUse = -1;
            default -> throw new IllegalArgumentException("Unsupported case: " + caseDescription);
        }
        world.setLastResponse(world.getBooksApi().getById(idToUse));
    }

    @Then("the system informs me that the book item is not available")
    public void systemInformsItemNotAvailable() {
        assertThat(world.getLastResponse()).hasStatusCode(NOT_FOUND);
    }

    @When("^I try to add a new book with missing data (.*)$")
    public void iTryToAddBookWithMissingData(String missing) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", TestData.uniqueId());
        payload.put("title", "T");
        payload.put("description", "D");
        payload.put("pageCount", 10);
        payload.put("excerpt", "E");
        payload.put("publishDate", "2020-01-01T00:00:00.000Z");

        switch (missing.trim()) {
            case "title" -> payload.remove("title");
            case "page count" -> payload.remove("pageCount");
            case "description" -> payload.remove("description");
            default -> throw new IllegalArgumentException("Unsupported missing field: " + missing);
        }
        world.setLastResponse(world.getBooksApi().create(payload));
    }

    @Then("the addition of a book is rejected with a clear message indicating which data must be provided")
    public void additionRejectedWithClearMessage() {
        assertThat(world.getLastResponse()).hasStatusCodeIn(BAD_REQUEST, UNPROCESSABLE_ENTITY);
    }

    @When("I attempt to update the book in an ambiguous or inconsistent way")
    public void attemptAmbiguousUpdate() {
        int id = TestData.uniqueId();
        Book book = TestData.randomBook(id);
        assertThat(world.getBooksApi().create(book)).hasStatusCode(OK);

        Book mismatched = new Book(id + 1, book.title(), book.description(), book.pageCount(), book.excerpt(), book.publishDate());
        world.setLastResponse(world.getBooksApi().update(id, mismatched));
    }

    @Then("the update of book is rejected with guidance on how to correctly specify the item to change")
    public void updateRejectedWithGuidance() {
        assertThat(world.getLastResponse()).hasStatusCodeIn(BAD_REQUEST, CONFLICT, UNPROCESSABLE_ENTITY);
    }

    @Given("I have removed that book from the bookstore")
    public void iHaveRemovedThatBook() {
        if (world.getLastBookPayload() == null) {
            int id = TestData.uniqueId();
            world.setLastBookPayload(TestData.randomBook(id));
            assertThat(world.getBooksApi().create(world.getLastBookPayload())).hasStatusCode(OK);
            world.setLastBookId(world.getLastBookPayload().id());
        }
        assertThat(world.getBooksApi().delete(world.getLastBookId())).hasStatusCode(OK);
    }

    @When("I attempt to remove the same book again")
    public void attemptRemoveSameBookAgain() {
        world.setLastResponse(world.getBooksApi().delete(world.getLastBookId()));
    }

    @Then("the system informs me the book item is already unavailable and no further changes are made")
    public void systemInformsItemUnavailableOnSecondDelete() {
        assertThat(world.getLastResponse()).hasStatusCodeIn(NOT_FOUND, BAD_REQUEST, NO_CONTENT);
    }
}
