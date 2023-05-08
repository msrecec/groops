package hr.tvz.groops.exception;

public enum ExceptionEnum {
    REFLECTION_EXCEPTION(0, "Reflection error has occurred."),
    INTERNAL_SERVER_EXCEPTION(1, "Something went wrong"),
    RUNTIME_EXCEPTION(2, "Something went wrong"),
    EXCEPTION(3, "Something went wrong"),
    INSTANCE_OF_EXCEPTION(4, "Error when checking instance of"),
    JSON_PROCESSING_EXCEPTION(5, "Error when processing json"),
    ILLEGAL_ARGUMENT_EXCEPTION(6, "Illegal argument provided"),
    PARSE_EXCEPTION(7, "Error when parsing"),
    IO_EXCEPTION(8, "I/O error"),
    LOCK_ACQUISITION_EXCEPTION(9, "Another transaction is currently running. Please wait and try again"),
    CSV_EXCEPTION(10, "csv exception for csv line number "),
    S3_EXCEPTION(11, "amazon s3 error"),
    ENTITY_NOT_FOUND_EXCEPTION(12, "entity not found in the database"),
    CONSTRAINT_VIOLATION_EXCEPTION(13, "Constraint violation exception"),
    DATA_INTEGRITY_VIOLATION_EXCEPTION(14, "Data integrity violation exception"),
    JPA_SYSTEM_EXCEPTION(15, "Jpa system exception"),
    CLIENT_AUTHORIZATION_EXCEPTION(16, "client authorization failed"),
    ACCESS_TOKEN_NOT_FOUND_EXCEPTION(17, "access token not found via oauth2 client"),
    EMPTY_RESULT_DATA_ACCESS_EXCEPTION(18, "No result found"),
    BIND_EXCEPTION(19, "Validation failed"),
    ACCESS_DENIED_EXCEPTION(20, "Access denied"),
    TRANSACTION_SYSTEM_EXCEPTION(21, "Transaction rollback"),
    NEXAR_EXCEPTION(22, "Nexar exception"),
    NO_BODY_IN_RESPONSE_EXCEPTION(23, "No body in http response"),
    EXTERNAL_SERVICE_EXCEPTION(24, "External service exception"),
    RESOURCE_NOT_FOUND_IN_CLASSPATH(25, "Required resource not found in classpath"),
    S3_OBJECT_EXISTS(26, "Document already exists"),
    BOM_DIFF_TEMPLATE_QUANTITY_POSITIVE(27, "Quantity be positive for non deleted bom diff template"),
    DUPLICATE_IMPORT_EXCEPTION(28, "Duplicates while importing"),
    PRODUCT_IMPORT_EXCEPTION(29, "Errors while importing products"),
    SERIALS_EMPTY(30, "Serials must not be empty"),
    COMMENT_ENTITIES_EMPTY(31, "Comment entities must not be empty"),
    UNSUPPORTED_CLASS_IMPLEMENTATION(32, "Given class implementation is not supported"),
    MISSING_COMMENT_TYPE_ID(33, "Missing comment type id"),
    MISSING_COMMENT_MESSAGE(34, "Missing comment message"),
    LOCK_INTERRUPTED_EXCEPTION(35, "Lock interrupted"),
    SCHEDULER_EXCEPTION(36, "Something went wrong in scheduler"),
    EMAIL_EXCEPTION(37, "Something went wrong in email"),
    INTERRUPTED_EXCEPTION(38, "Interrupted exception"),
    SCHEDULER_CONFIGURATION_EXCEPTION(39, "Something went wrong with scheduler configuration initialization"),
    NOTIFICATION_EXCEPTION(40, "Something went wrong when sending notification"),
    SUBSCRIPTION_ID_NOT_NULL(41, "Subscription id must not be null"),
    CLASS_CAST_EXCEPTION(42, "Class cast exception"),
    INVALID_PASSWORD_EXCEPTION(43, "Invalid password"),
    INVALID_DATE_OF_BIRTH_EXCEPTION(44, "Invalid date of birth"),
    UNAUTHORIZED_EXCEPTION(45, "Unauthorized");
    private final int code;
    private final String clientMessage;

    ExceptionEnum(int code, String clientMessage) {
        this.code = code;
        this.clientMessage = clientMessage;
    }

    public int getCode() {
        return code;
    }

    public String getShortMessage() {
        return clientMessage;
    }

    public String getFullMessage() {
        return code + ": " + clientMessage;
    }
}
