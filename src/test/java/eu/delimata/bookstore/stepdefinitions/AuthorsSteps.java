package eu.delimata.bookstore.stepdefinitions;

import eu.delimata.bookstore.BookstoreWorld;
import eu.delimata.bookstore.model.Author;
import eu.delimata.bookstore.stepdefinitions.builders.AuthorBuilder;
import eu.delimata.bookstore.stepdefinitions.builders.AuthorPayloadBuilder;
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
public class AuthorsSteps {

    private final BookstoreWorld world;
    private final AuthorStepHelpers helpers;

    public AuthorsSteps(BookstoreWorld world) {
        this.world = world;
        this.helpers = new AuthorStepHelpers(world);
    }

    // GIVEN steps - Setup/Preparation

    @And("I have some unique data of an author")
    public void iHaveSomeUniqueDataOfAnAuthor() {
        int id = TestData.uniqueId();
        Author author = AuthorBuilder.anAuthor()
                .withId(id)
                .withIdBook(id)
                .buildRandom();
        world.setLastAuthorPayload(author);
        log.debug("Created unique author data with ID: {}", id);
    }

    @Given("an author exists in the bookstore")
    public void anAuthorPreparedExists() {
        Author author = AuthorBuilder.anAuthor()
                .withId(1)
                .withIdBook(1)
                .withFirstName("First Name 1")
                .withLastName("Last Name 1")
                .build();

        world.setLastAuthorPayload(author);
        world.setLastAuthorId(author.id());
        log.debug("Prepared fixed author with ID: {}", author.id());
    }

    @Given("I have removed that author from the bookstore")
    public void removedThatAuthor() {
        helpers.ensureAuthorExists();
        helpers.deleteAuthor(world.getLastAuthorId());
        log.debug("Removed author with ID: {}", world.getLastAuthorId());
    }

    // WHEN steps - Actions

    @When("I add a new author to the bookstore")
    public void iAddANewAuthor() {
        helpers.ensureAuthorPayloadExists();
        world.setLastResponse(world.getAuthorsApi().create(world.getLastAuthorPayload()));
        world.setLastAuthorId(world.getLastAuthorPayload().id());
        log.debug("Added author with ID: {}", world.getLastAuthorId());
    }

    @When("I edit selected information for that author")
    public void editSelectedAuthorInformation() {
        Author updated = AuthorBuilder.fromAuthor(world.getLastAuthorPayload())
                .withFirstName(world.getLastAuthorPayload().firstName() + " Jr.")
                .build();

        world.setLastResponse(world.getAuthorsApi().update(world.getLastAuthorId(), updated));
        assertThat(world.getLastResponse()).hasStatusCode(OK);
        world.setLastAuthorPayload(updated);
        log.debug("Updated author with ID: {}", world.getLastAuthorId());
    }

    @When("I change selected information for that author")
    public void changeSelectedAuthorInfo() {
        editSelectedAuthorInformation();
    }

    @When("I remove that author from the bookstore")
    public void removeThatAuthor() {
        world.setLastResponse(world.getAuthorsApi().delete(world.getLastAuthorId()));
        assertThat(world.getLastResponse()).hasStatusCode(OK);
        log.debug("Removed author with ID: {}", world.getLastAuthorId());
    }

    @When("I attempt to remove the same author again")
    public void attemptRemoveAuthorAgain() {
        world.setLastResponse(world.getAuthorsApi().delete(world.getLastAuthorId()));
        log.debug("Attempted to remove author with ID: {} again", world.getLastAuthorId());
    }

    @When("I open the details of that author")
    public void openAuthorDetails() {
        world.setLastResponse(world.getAuthorsApi().getById(world.getLastAuthorId()));
    }

    @When("^I try to open the details of an author that (.*)$")
    public void tryOpenAuthorDetailsThat(String caseDescription) {
        int authorId = helpers.getAuthorIdForCase(caseDescription.trim());
        world.setLastResponse(world.getAuthorsApi().getById(authorId));
        log.debug("Tried to get details of author with ID: {} (case: {})", authorId, caseDescription);
    }

    @When("^I try to add a new author with missing data (.*)$")
    public void tryAddAuthorWithMissing(String missingField) {
        Map<String, Object> payload = AuthorPayloadBuilder.validPayload()
                .withId(TestData.uniqueId())
                .remove(missingField.trim())
                .build();

        world.setLastResponse(world.getAuthorsApi().create(payload));
        log.debug("Attempted to add author with missing field: {}", missingField);
    }

    @When("I attempt to update the author in an ambiguous or inconsistent way")
    public void attemptAmbiguousAuthorUpdate() {
        Author originalAuthor = helpers.createAndReturnAuthor();

        Author mismatchedAuthor = AuthorBuilder.fromAuthor(originalAuthor)
                .withId(originalAuthor.id() + 1) // Mismatched ID
                .build();

        world.setLastResponse(world.getAuthorsApi().update(originalAuthor.id(), mismatchedAuthor));
        world.setLastAuthorId(originalAuthor.id());
        log.debug("Attempted ambiguous update: path ID {} vs payload ID {}",
                originalAuthor.id(), mismatchedAuthor.id());
    }

    // THEN steps - Verifications

    @Then("I see a confirmation that the author was added")
    public void confirmAuthorAdded() {
        assertThat(world.getLastResponse()).hasStatusCode(OK);
    }

    @Then("the author is visible in the list and in the author details")
    public void authorVisibleInListAndDetails() {
        helpers.verifyAuthorInDetails();
        helpers.verifyAuthorInList();
    }

    @Then("the author details include the first name and the last name")
    public void authorDetailsIncludeStandardInfo() {
        Author author = world.getAuthorsApi().getAuthorById(world.getLastAuthorId());

        assertThat(author.firstName())
                .as("Author first name should not be blank")
                .isNotBlank();
        assertThat(author.lastName())
                .as("Author last name should not be blank")
                .isNotBlank();
    }

    @Then("the details reflect the information provided when the author was added")
    public void detailsReflectWhenAuthorAdded() {
        Author retrievedAuthor = world.getLastResponse()
                .then()
                .statusCode(OK.toInt())
                .extract()
                .as(Author.class);

        assertThat(retrievedAuthor.firstName())
                .as("Retrieved author first name should match the original")
                .isEqualTo(world.getLastAuthorPayload().firstName());
        assertThat(retrievedAuthor.lastName())
                .as("Retrieved author last name should match the original")
                .isEqualTo(world.getLastAuthorPayload().lastName());
    }

    @Then("the changes are visible in the author details")
    public void changesVisibleInAuthorDetails() {
        Author author = world.getAuthorsApi().getAuthorById(world.getLastAuthorId());
        assertThat(author.firstName())
                .as("Updated author first name should end with 'Jr.'")
                .endsWith("Jr.");
    }

    @Then("the updated information about the author is visible to users")
    public void updatedAuthorInfoVisible() {
        changesVisibleInAuthorDetails();
    }

    @Then("the author is no longer available to users")
    @Then("the author no longer appears in search or details")
    @Then("the system informs me that the author item is not available")
    public void authorNoLongerAvailable() {
        assertThat(world.getLastResponse()).hasStatusCode(NOT_FOUND);
    }

    @Then("the addition of an author is rejected with a clear message indicating which data must be provided")
    public void authorAdditionRejected() {
        assertThat(world.getLastResponse())
                .hasStatusCodeIn(BAD_REQUEST, UNPROCESSABLE_ENTITY);
    }

    @Then("the update of author is rejected with guidance on how to correctly specify the item to change")
    public void authorUpdateRejected() {
        assertThat(world.getLastResponse())
                .hasStatusCodeIn(BAD_REQUEST, CONFLICT, UNPROCESSABLE_ENTITY);
    }

    @Then("the system informs me the author item is already unavailable and no further changes are made")
    public void authorAlreadyUnavailable() {
        assertThat(world.getLastResponse())
                .hasStatusCodeIn(NOT_FOUND, BAD_REQUEST, NO_CONTENT);
    }
}