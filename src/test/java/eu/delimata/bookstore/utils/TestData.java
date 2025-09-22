package eu.delimata.bookstore.utils;

import com.github.javafaker.Faker;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class for generating test data for bookstore tests.
 * Provides methods for creating realistic random book data.
 */
public final class TestData {

    private static final Faker FAKER = new Faker();

    private static final AtomicInteger SEQ = new AtomicInteger(1);

    private TestData() {
        // Utility class - prevent instantiation
    }

    /**
     * Generates a unique ID for testing purposes.
     *
     * @return unique integer ID
     */
    public static int uniqueId() {
        return (int)(System.currentTimeMillis() & 0x7FFFFFFF) + SEQ.getAndIncrement();
    }

    /**
     * Generates a random book title from predefined list.
     *
     * @return random book title
     */
    public static String randomTitle() {
        return FAKER.book().title();
    }

    /**
     * Generates a random book description based on templates and subject areas.
     *
     * @return random book description
     */
    public static String randomDescription() {
        return FAKER.lorem().sentence();
    }

    /**
     * Generates a random page count within realistic ranges for technical books.
     *
     * @return random page count between 80 and 800
     */
    public static int randomPageCount() {
        // Most technical books range from 150-500 pages
        return FAKER.number().numberBetween(150, 500);
    }

    /**
     * Generates a random book excerpt from predefined templates.
     *
     * @return random book excerpt
     */
    public static String randomExcerpt() {

        return FAKER.lorem().paragraph();
    }
}
