package eu.delimata.bookstore.utils;

import com.github.javafaker.Faker;
import eu.delimata.bookstore.model.Book;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public final class TestData {

    private static final Faker FAKER = new Faker();

    private static final AtomicInteger SEQ = new AtomicInteger(1);

    private TestData() {
    }

    public static int uniqueId() {
        return (int)(System.currentTimeMillis() & 0x7FFFFFFF) + SEQ.getAndIncrement();
    }

    public static Book randomBook(int id) {
        return new Book(
                id,
                FAKER.book().title(),
                FAKER.lorem().sentence(),
                FAKER.number().numberBetween(30, 1200),
                FAKER.lorem().paragraph(),
                DateTimeFormatter.ISO_INSTANT.format(OffsetDateTime.now())
        );
    }
}
