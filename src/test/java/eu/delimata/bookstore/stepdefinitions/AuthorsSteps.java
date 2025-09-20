package eu.delimata.bookstore.stepdefinitions;

import eu.delimata.bookstore.BookstoreWorld;
import eu.delimata.bookstore.model.Author;
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
        int id = (int) (System.currentTimeMillis() / 1000);
        world.setLastAuthorPayload(new Author(id, id, "John" + id, "Doe" + id));
        world.getContext().put("isAuthorConfirmedInBookstore", false);
    }

    @Given("an author exists in the bookstore")
    public void anAuthorPreparedExists() {
        if (world.getLastAuthorPayload() == null) {
            int id = (int) (System.currentTimeMillis() / 1000);
            world.setLastAuthorPayload(new Author(id, id, "Jane" + id, "Doe" + id));
        }
        world.setLastResponse(world.getAuthorsApi().create(world.getLastAuthorPayload()));
        assertHttpStatusCode(world.getLastResponse()).
                isEqualTo(OK.toInt());
        world.setLastAuthorId(world.getLastAuthorPayload().id());
        world.getContext().put("isAuthorConfirmedInBookstore", true);
    }

    @When("I add a new author to the bookstore")
    public void iAddANewAuthor() {
        if (world.getLastAuthorPayload() == null) {
            int id = (int) (System.currentTimeMillis() / 1000);
            world.setLastAuthorPayload(new Author(id, id, "John" + id, "Doe" + id));
        }
        long t0 = System.currentTimeMillis();
        world.setLastResponse(world.getAuthorsApi().create(world.getLastAuthorPayload()));
        world.setLastOperationMillis(System.currentTimeMillis() - t0);
        world.setLastAuthorId(world.getLastAuthorPayload().id());
    }

    @Then("the author is visible in the list and in the author details")
    public void authorVisibleInListAndDetails() {
        Author got = world.getAuthorsApi().getById(world.getLastAuthorId()).then().statusCode(OK.toInt()).extract().as(Author.class);
        assertThat(got.id()).isEqualTo(world.getLastAuthorPayload().id());

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
        assertHttpStatusCode(world.getLastResponse()).isEqualTo(OK.toInt());
        world.setLastAuthorPayload(updated);
    }

    @Then("the changes are visible in the author details")
    public void changesVisibleInAuthorDetails() {
        Author got = world.getAuthorsApi().getById(world.getLastAuthorId()).then().statusCode(OK.toInt()).extract().as(Author.class);
        assertThat(got.firstName()).endsWith("Jr.");
    }

    @When("I remove that author from the bookstore")
    public void removeThatAuthor() {
        world.setLastResponse(world.getAuthorsApi().delete(world.getLastAuthorId()));
        assertHttpStatusCode(world.getLastResponse()).isEqualTo(OK.toInt());
    }

    @Then("the author is no longer available to users")
    public void authorNoLongerAvailable() {
        assertHttpStatusCode(world.getLastResponse()).isIn(NOT_FOUND.toInt(), BAD_REQUEST.toInt(), NO_CONTENT.toInt());
    }

    @Then("I see a confirmation that the author was added")
    public void confirmAuthorAdded() {
        assertHttpStatusCode(world.getLastResponse()).isEqualTo(OK.toInt());
    }

    @Then("the author details include the first name and the last name")
    public void authorDetailsIncludeStandardInfo() {
        Author got = world.getAuthorsApi().getById(world.getLastAuthorId()).then().statusCode(OK.toInt()).extract().as(Author.class);
        assertThat(got.firstName()).isNotBlank();
        assertThat(got.lastName()).isNotBlank();
    }

    @When("I open the details of that author")
    public void openAuthorDetails() {
        world.setLastResponse(world.getAuthorsApi().getById(world.getLastAuthorId()));
    }

    @Then("the details reflect the information provided when the author was added")
    public void detailsReflectWhenAuthorAdded() {
        Author got = world.getLastResponse().then().statusCode(OK.toInt()).extract().as(Author.class);
        assertThat(got.firstName()).isEqualTo(world.getLastAuthorPayload().firstName());
        assertThat(got.lastName()).isEqualTo(world.getLastAuthorPayload().lastName());
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
                int id = (int) (System.currentTimeMillis() / 1000);
                Author author = new Author(id, id, "ToRemove" + id, "Delimata");
                assertHttpStatusCode(world.getBooksApi().create(author)).isEqualTo(OK.toInt());
                assertHttpStatusCode(world.getBooksApi().delete(id)).isEqualTo(OK.toInt());
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
        assertHttpStatusCode(world.getLastResponse()).isIn(NOT_FOUND.toInt(), BAD_REQUEST.toInt(), NO_CONTENT.toInt());
    }

    @When("^I try to add a new author with missing data (.*)$")
    public void tryAddAuthorWithMissing(String missing) {
        Map<String, Object> payload = new HashMap<>();
        int id = (int) (System.currentTimeMillis() / 1000);
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
        assertHttpStatusCode(world.getLastResponse()).isIn(BAD_REQUEST.toInt(), UNPROCESSABLE_ENTITY.toInt());
    }

    @When("I attempt to update the author in an ambiguous or inconsistent way")
    public void attemptAmbiguousAuthorUpdate() {
        int id = (int) (System.currentTimeMillis() / 1000);
        var a = new Author(id, id, "Jane" + id, "Doe");
        assertHttpStatusCode(world.getBooksApi().create(a)).isEqualTo(OK.toInt());

        var mismatched = new Author(id + 1, a.idBook(), a.firstName(), a.lastName());
        world.setLastResponse(world.getAuthorsApi().update(id, mismatched));
        world.setLastAuthorId(id);
    }

    @Then("the update of author is rejected with guidance on how to correctly specify the item to change")
    public void authorUpdateRejected() {
        assertHttpStatusCode(world.getLastResponse()).isIn(BAD_REQUEST.toInt(), CONFLICT.toInt(), UNPROCESSABLE_ENTITY.toInt());
    }

    @Given("I have removed that author from the bookstore")
    public void removedThatAuthor() {
        int id = (int) (System.currentTimeMillis() / 1000);
        Author author = new Author(id, id, "RemoveMe" + id, "Doe");
        assertHttpStatusCode(world.getBooksApi().create(author)).isEqualTo(OK.toInt());
        assertHttpStatusCode(world.getBooksApi().delete(id)).isEqualTo(OK.toInt());
        world.setLastAuthorId(id);
    }

    @When("I attempt to remove the same author again")
    public void attemptRemoveAuthorAgain() {
        world.setLastResponse(world.getAuthorsApi().delete(world.getLastAuthorId()));
    }

    @Then("the system informs me the author item is already unavailable and no further changes are made")
    public void authorAlreadyUnavailable() {
        assertHttpStatusCode(world.getLastResponse()).isIn(NOT_FOUND.toInt(), BAD_REQUEST.toInt(), NO_CONTENT.toInt());
    }
}
