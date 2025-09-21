package eu.delimata.bookstore.enums;

public enum HttpCode {


    // --- 2xx Success ---
    OK(200),
    CREATED(201),
    ACCEPTED(202),
    NO_CONTENT(204),

    // --- 4xx Client Error ---
    BAD_REQUEST(400),
    NOT_FOUND(404),
    FORBIDDEN(403),
    CONFLICT(409),
    UNPROCESSABLE_ENTITY(422);

    // --- 5xx Server Error ---
    // (not used in tests so far)

    private final int code;

    HttpCode(int code) {
        this.code = code;
    }

    public static HttpCode resolve(int code) {
        for (HttpCode httpCode : HttpCode.values()) {
            if (httpCode.code == code) {
                return httpCode;
            }
        }
        return null;
    }

    public int toInt() {
        return code;
    }
}
