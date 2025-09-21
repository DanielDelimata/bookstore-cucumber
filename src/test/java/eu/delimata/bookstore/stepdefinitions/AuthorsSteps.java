package eu.delimata.bookstore.stepdefinitions;

import eu.delimata.bookstore.BookstoreWorld;
import eu.delimata.bookstore.model.Author;
import eu.delimata.bookstore.support.ResponseAssert;
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
import static eu.delimata.bookstore.support.ResponseAssert.assertHttpStatusCode;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class AuthorsSteps {
    
    private final BookstoreWorld world;

    public AuthorsSteps(BookstoreWorld world) {
        this.world = world;
    }

    @And("I have some unique data of an author")
    public void iHaveSomeUniqueDataOfAnAuthor() {
        int id = TestData.uniqueId();
        world.setLastAuthorPayload(new Author(id, id, "John" + id, "Doe" + id));
    }

    @Given("an author exists in the bookstore")
    public void anAuthorPreparedExists() {
        // Use fixed data to simplify validation
        world.setLastAuthorPayload(new Author(1, 1, "First Name 1", "Last Name 1"));
        world.setLastAuthorId(world.getLastAuthorPayload().id());
    }

    @When("I add a new author to the bookstore")
    public void iAddANewAuthor() {
        if (world.getLastAuthorPayload() == null) {
            int id = TestData.uniqueId();
            world.setLastAuthorPayload(new Author(id, id, "John" + id, "Doe" + id));
        }
        world.setLastResponse(world.getAuthorsApi().create(world.getLastAuthorPayload()));
        world.setLastAuthorId(world.getLastAuthorPayload().id());
    }

    @Then("the author is visible in the list and in the author details")
    public void authorVisibleInListAndDetails() {
        Author author = world.getAuthorsApi().getAuthorById(world.getLastAuthorId());
        assertThat(author.id()).isEqualTo(world.getLastAuthorPayload().id());

        String listJson = world.getAuthorsApi().getAll().then().statusCode(OK.toInt()).extract().asString();
        List<Integer> ids = JsonPath.from(listJson).getList("id", Integer.class);
        assertThat(ids).contains(world.getLastAuthorId());
    }

    @When("I edit selected information for that author")
    public void editSelectedAuthorInformation() {
        Author updated = new Author(
                world.getLastAuthorPayload().id(),
                world.getLastAuthorPayload().idBook(),
                world.getLastAuthorPayload().firstName() + " Jr.",
                world.getLastAuthorPayload().lastName()
        );
        world.setLastResponse(world.getAuthorsApi().update(world.getLastAuthorId(), updated));
        ResponseAssert.assertHttpStatusCode(world.getLastResponse(), OK);
        world.setLastAuthorPayload(updated);
    }

    @Then("the changes are visible in the author details")
    public void changesVisibleInAuthorDetails() {
        Author author = world.getAuthorsApi().getAuthorById(world.getLastAuthorId());
        assertThat(author.firstName()).endsWith("Jr.");
    }

    @When("I remove that author from the bookstore")
    public void removeThatAuthor() {
        world.setLastResponse(world.getAuthorsApi().delete(world.getLastAuthorId()));
        ResponseAssert.assertHttpStatusCode(world.getLastResponse(), OK);
    }

    @Then("the author is no longer available to users")
    public void authorNoLongerAvailable() {
        ResponseAssert.assertHttpStatusCode(world.getLastResponse(), NOT_FOUND);
    }

    @Then("I see a confirmation that the author was added")
    public void confirmAuthorAdded() {
        ResponseAssert.assertHttpStatusCode(world.getLastResponse(), OK);
    }

    @Then("the author details include the first name and the last name")
    public void authorDetailsIncludeStandardInfo() {
        Author author = world.getAuthorsApi().getAuthorById(world.getLastAuthorId());
        assertThat(author.firstName()).isNotBlank();
        assertThat(author.lastName()).isNotBlank();
    }

    @When("I open the details of that author")
    public void openAuthorDetails() {
        world.setLastResponse(world.getAuthorsApi().getById(world.getLastAuthorId()));
    }

    @Then("the details reflect the information provided when the author was added")
    public void detailsReflectWhenAuthorAdded() {
        Author author = world.getLastResponse().then().statusCode(OK.toInt()).extract().as(Author.class);
        assertThat(author.firstName()).isEqualTo(world.getLastAuthorPayload().firstName());
        assertThat(author.lastName()).isEqualTo(world.getLastAuthorPayload().lastName());
    }

    @When("I change selected information for that author")
    public void changeSelectedAuthorInfo() {
        editSelectedAuthorInformation();
    }

    @Then("the updated information about the author is visible to users")
    public void updatedAuthorInfoVisible() {
        changesVisibleInAuthorDetails();
    }

    @Then("the author no longer appears in search or details")
    public void authorNoLongerInSearchOrDetails() {
        authorNoLongerAvailable();
    }

    @When("^I try to open the details of an author that (.*)$")
    public void tryOpenAuthorDetailsThat(String caseDescription) {
        int idToUse;
        switch (caseDescription.trim()) {
            case "was already removed" -> {
                int id = TestData.uniqueId();
                Author author = new Author(id, id, "ToRemove" + id, "Delimata");
                assertHttpStatusCode(world.getBooksApi().create(author), OK);
                assertHttpStatusCode(world.getBooksApi().delete(id), OK);
                idToUse = id;
            }
            case "never existed" -> idToUse = 987_654_321;
            case "has an invalid identifier" -> idToUse = -1;
            default -> throw new IllegalArgumentException("Unsupported case: " + caseDescription);
        }
        world.setLastResponse(world.getAuthorsApi().getById(idToUse));
    }

    @Then("the system informs me that the author item is not available")
    public void itemNotAvailable() {
        assertHttpStatusCode(world.getLastResponse(), NOT_FOUND, BAD_REQUEST, NO_CONTENT);
    }

    @When("^I try to add a new author with missing data (.*)$")
    public void tryAddAuthorWithMissing(String missing) {
        Map<String, Object> payload = new HashMap<>();
        int id = TestData.uniqueId();
        payload.put("id", id);
        payload.put("idBook", id);
        payload.put("firstName", "Name" + id);
        payload.put("lastName", "Surname" + id);

        switch (missing.trim()) {
            case "display name" -> payload.remove("firstName");
            case "short bio" -> payload.remove("lastName");
            case "date of birth" -> payload.remove("idBook");
            default -> throw new IllegalArgumentException("Unsupported missing field: " + missing);
        }
        world.setLastResponse(world.getAuthorsApi().create(payload));
    }

    @Then("the addition of an author is rejected with a clear message indicating which data must be provided")
    public void authorAdditionRejected() {
        assertHttpStatusCode(world.getLastResponse(), BAD_REQUEST, UNPROCESSABLE_ENTITY);
    }

    @When("I attempt to update the author in an ambiguous or inconsistent way")
    public void attemptAmbiguousAuthorUpdate() {
        int id = TestData.uniqueId();
        Author author = new Author(id, id, "Jane" + id, "Doe");
        assertHttpStatusCode(world.getBooksApi().create(author), OK);

        var mismatched = new Author(id + 1, author.idBook(), author.firstName(), author.lastName());
        world.setLastResponse(world.getAuthorsApi().update(id, mismatched));
        world.setLastAuthorId(id);
    }

    @Then("the update of author is rejected with guidance on how to correctly specify the item to change")
    public void authorUpdateRejected() {
        assertHttpStatusCode(world.getLastResponse(), BAD_REQUEST, CONFLICT, UNPROCESSABLE_ENTITY);
    }

    @Given("I have removed that author from the bookstore")
    public void removedThatAuthor() {
        int id = TestData.uniqueId();
        Author author = new Author(id, id, "RemoveMe" + id, "Doe");
        assertHttpStatusCode(world.getBooksApi().create(author), OK);
        assertHttpStatusCode(world.getBooksApi().delete(id), OK);
        world.setLastAuthorId(id);
    }

    @When("I attempt to remove the same author again")
    public void attemptRemoveAuthorAgain() {
        world.setLastResponse(world.getAuthorsApi().delete(world.getLastAuthorId()));
    }

    @Then("the system informs me the author item is already unavailable and no further changes are made")
    public void authorAlreadyUnavailable() {
        assertHttpStatusCode(world.getLastResponse(), NOT_FOUND, BAD_REQUEST, NO_CONTENT);
    }
}
