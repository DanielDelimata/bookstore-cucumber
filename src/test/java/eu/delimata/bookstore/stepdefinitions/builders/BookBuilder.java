package eu.delimata.bookstore.stepdefinitions.builders;

import eu.delimata.bookstore.model.Book;
import eu.delimata.bookstore.utils.TestData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Builder pattern implementation for creating Book objects in tests.
 * Provides fluent API for book creation with sensible defaults.
 */
public class BookBuilder {
    private Integer id;
    private String title;
    private String description;
    private Integer pageCount;
    private String excerpt;
    private String publishDate;

    private BookBuilder() {}

    public static BookBuilder aBook() {
        return new BookBuilder();
    }

    public static BookBuilder fromBook(Book book) {
        return new BookBuilder()
                .withId(book.id())
                .withTitle(book.title())
                .withDescription(book.description())
                .withPageCount(book.pageCount())
                .withExcerpt(book.excerpt())
                .withPublishDate(book.publishDate());
    }

    public BookBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public BookBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public BookBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public BookBuilder withPageCount(Integer pageCount) {
        this.pageCount = pageCount;
        return this;
    }

    public BookBuilder withExcerpt(String excerpt) {
        this.excerpt = excerpt;
        return this;
    }

    public BookBuilder withPublishDate(String publishDate) {
        this.publishDate = publishDate;
        return this;
    }

    public Book build() {
        return new Book(
                id != null ? id : TestData.uniqueId(),
                title != null ? title : "Default Title",
                description != null ? description : "Default description",
                pageCount != null ? pageCount : 100,
                excerpt != null ? excerpt : "Default excerpt",
                publishDate != null ? publishDate : getCurrentDateTime()
        );
    }

    public Book buildRandom() {
        return new Book(
                id != null ? id : TestData.uniqueId(),
                title != null ? title : TestData.randomTitle(),
                description != null ? description : TestData.randomDescription(),
                pageCount != null ? pageCount : TestData.randomPageCount(),
                excerpt != null ? excerpt : TestData.randomExcerpt(),
                publishDate != null ? publishDate : getCurrentDateTime()
        );
    }

    private String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z";
    }
}

