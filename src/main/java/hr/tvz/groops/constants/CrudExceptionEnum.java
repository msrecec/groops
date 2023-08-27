package hr.tvz.groops.constants;

public enum CrudExceptionEnum {
    USER_NOT_FOUND_BY_ID("User not found by id", "User not found by id: %d"),
    COMMENT_NOT_FOUND_BY_ID("Comment not found by id", "Comment not found by id: %d"),
    GROUP_NOT_FOUND_BY_ID("Group not found by id", "Group not found by id: %d"),
    GROUP_REQUEST_NOT_FOUND_BY_GROUP_AND_USER("Group request not found by group and user", "Group request not found by group with id: %d and user with id: %d"),
    USER_NOT_FOUND_BY_USERNAME("User not found by username", "User not found by username: %s"),
    ROLE_NOT_FOUND_BY_ID("Role not found by id", "Role not found by id: %d"),
    POST_NOT_FOUND_BY_ID("Post not found by id", "Post not found by id: %d"),
    PERMISSION_NOT_FOUND_BY_ID("Permission not found by id", "Permission not found by id: %d"),
    ROLE_PERMISSION_NOT_FOUND_BY_ROLE_ID_AND_PERMISSION_ID("Role permission not found by role id and permission id", "Role permission not found by role id: %d and permission id: %d"),
    USER_GROUP_NOT_FOUND_BY_USER_ID_AND_GROUP_ID("User group not found by user id and group id", "User group not found by user id: %d and group id: %d"),
    USER_GROUP_ROLE_NOT_FOUND_BY_USER_GROUP_AND_ROLE("User group not found by user group id and role id", "User group not found by user group id: %d and role id: %d"),
    POST_LIKE_NOT_FOUND_BY_POST_AND_USER("User group not found by user group id and role id", "User group not found by user group id: %d and role id: %d"),
    FRIEND_REQUEST_NOT_FOUND_BY_SENDER_AND_RECIPIENT("Friend request not found by sender and recipient", "Friend request not found by sender with id: %d and recipient with id: %d");

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
