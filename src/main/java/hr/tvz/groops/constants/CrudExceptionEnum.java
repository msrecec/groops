package hr.tvz.groops.constants;

public enum CrudExceptionEnum {
    USER_NOT_FOUND_BY_ID("User not found by id", "User not found by id: %d");

    private final String message;
    private final String parametrizedMessage;

    CrudExceptionEnum(String message, String parametrizedMessage) {
        this.message = message;
        this.parametrizedMessage = parametrizedMessage;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageComposed(Object... parameters) {
        return String.format(parametrizedMessage, parameters);
    }
}
