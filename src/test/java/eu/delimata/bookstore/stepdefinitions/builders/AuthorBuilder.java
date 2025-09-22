package eu.delimata.bookstore.stepdefinitions.builders;

import eu.delimata.bookstore.model.Author;
import eu.delimata.bookstore.utils.TestData;

/**
 * Builder pattern implementation for creating Author objects in tests.
 * Provides fluent API for author creation with sensible defaults.
 */
public class AuthorBuilder {
    private Integer id;
    private Integer idBook;
    private String firstName;
    private String lastName;

    private AuthorBuilder() {}

    public static AuthorBuilder anAuthor() {
        return new AuthorBuilder();
    }

    public static AuthorBuilder fromAuthor(Author author) {
        return new AuthorBuilder()
                .withId(author.id())
                .withIdBook(author.idBook())
                .withFirstName(author.firstName())
                .withLastName(author.lastName());
    }

    public AuthorBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public AuthorBuilder withIdBook(Integer idBook) {
        this.idBook = idBook;
        return this;
    }

    public AuthorBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public AuthorBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public Author build() {
        return new Author(
                id != null ? id : TestData.uniqueId(),
                idBook != null ? idBook : (id != null ? id : TestData.uniqueId()),
                firstName != null ? firstName : "Default",
                lastName != null ? lastName : "Author"
        );
    }

    public Author buildRandom() {
        int authorId = id != null ? id : TestData.uniqueId();
        return new Author(
                authorId,
                idBook != null ? idBook : authorId,
                firstName != null ? firstName : TestData.randomFirstName(),
                lastName != null ? lastName : TestData.randomLastName()
        );
    }
}

