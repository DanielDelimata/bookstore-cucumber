package eu.delimata.bookstore.enums;

public enum HttpCode {

    OK(200),
    CREATED(201),
    ACCEPTED(202),
    NO_CONTENT(204),

    BAD_REQUEST(400),
    NOT_FOUND(404),
    FORBIDDEN(403),
    CONFLICT(409),
    UNPROCESSABLE_ENTITY(422);

    private final int code;

    HttpCode(int code) {
        this.code = code;
    }

    public int toInt() {
        return code;
    }
}
