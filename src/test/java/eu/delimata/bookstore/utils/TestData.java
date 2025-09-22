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
     * Generates a random book title using Faker library.
     *
     * @return random book title
     */
    public static String randomTitle() {
        return FAKER.book().title();
    }

    /**
     * Generates a random book description using Faker library.
     *
     * @return random book description
     */
    public static String randomDescription() {
        return FAKER.lorem().sentence();
    }

    /**
     * Generates a random page count using Faker library.
     *
     * @return random page count between 80 and 800
     */
    public static int randomPageCount() {
        // Most technical books range from 150-500 pages
        return FAKER.number().numberBetween(150, 500);
    }

    /**
     * Generates a random book excerpt using Faker library.
     *
     * @return random book excerpt
     */
    public static String randomExcerpt() {

        return FAKER.lorem().paragraph();
    }

    public static String randomFirstName() {
        return FAKER.name().firstName();
    }

    /**
     * Generates a random last name using Faker library.
     *
     * @return random last name
     */
    public static String randomLastName() {
        return FAKER.name().lastName();
    }
}
