package hr.tvz.groops.constants;

public enum CrudExceptionEnum {
    USER_NOT_FOUND_BY_ID("User not found by id", "User not found by id: %d"),
    USER_NOT_FOUND_BY_USERNAME("User not found by username", "User not found by username: %s"),
    ROLE_NOT_FOUND_BY_ID("Role not found by id", "Role not found by id: %d"),
    PERMISSION_NOT_FOUND_BY_ID("Permission not found by id", "Permission not found by id: %d"),
    ROLE_PERMISSION_NOT_FOUND_BY_ROLE_ID_AND_PERMISSION_ID("Role permission not found by role id and permission id", "Role permission not found by role id: %d and permission id: %d");

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
