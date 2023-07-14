package hr.tvz.groops.exception;

public enum CrudExceptionEnum {
    PRODUCT_SHIPMENT_NOT_FOUND("Product shipment not found for the given id", "Product shipment not found for id %d"),
    LOT_NOT_FOUND_BY_ID("Lot not found for the given id", "Lot not found for id %d"),
    PRODUCT_MODEL_NOT_FOUND_BY_ID("No product model found for the given id", "No product model found for id %d"),
    BATCH_STATUS_NOT_FOUND_BY_ID("No batch status found for the given id", "No batch status found for id %d"),
    BOM_COMPONENT_NOT_FOUND_BY_ID("No bom_component found for the given id", "No bom_component found for id %d"),
    BOM_COMPONENT_HISTORY_NOT_FOUND_BY_ID("No bom_component_history found for the given id", "No bom_component_history found for id %d"),
    BOM_NOT_FOUND_BY_ID("Bom not found for the given id", "Bom not found for id %d"),
    PRODUCT_ACTIVITY_NOT_FOUND_BY_ID("Product activity not found for the given id", "Product activity not found for id %d"),
    PRODUCT_ACTIVITY_TYPE_NOT_FOUND_BY_ID("Product activity type not found for the given id", "Product activity type not found for id %d"),
    BOM_DIFF_NOT_FOUND_BY_ID("Bom diff not found for the given id", "Bom diff not found for id %d"),
    BOM_NOT_FOUND_BY_NAME("Bom not found for the given name", "Bom not found for the given name: %s"),
    COMPONENT_NOT_FOUND_BY_TPG_CODE("Component not found for the given tpgCode", "No component found for the given tpg code: %s"),
    COMPONENT_NOT_FOUND_BY_ID("No component found for the given id", "No component found for id %d"),
    NON_ENTITY_NOT_FOUND_BY_ID("No non entity found for the given id", "No non entity found for id %d"),
    NON_ENTITY_NOT_FOUND_BY_NON_ENTITY_ENUM("No non entity found for the given non entity enum id", "No non entity found for non entity enum %s"),
    COMPONENT_GROUP_NOT_FOUND_BY_ID("No component group found for the given id", "No component group found for id %d"),
    ACCESS_DENIED("Access is denied", "Access is denied for user %s"),
    PRODUCT_NOT_FOUND_BY_ID("No product with the given id in the db", "Product not found for id %d"),
    COMMENT_TYPE_NOT_FOUND_BY_ID("No comment type found for the given id", "Comment type not found for id %d"),
    COMMENT_NOT_FOUND_BY_ID("No comment found for the given id", "Comment not found for id %d"),
    PRODUCTION_CHANGES_NOT_FOUND_BY_ID("No production changes found for the given id", "Production changes not found for id %d"),
    PRODUCT_REPLACEMENT_NOT_FOUND_BY_ID("No product replacement found for the given id", "Product replacement not found for id %d"),
    MODEL_CATEGORY_NOT_FOUND_BY_ID("Model Category with given id not found.", "Model category not found for id %d"),
    BOM_CATEGORY_NOT_FOUND_BY_ID("Bom Category with given id not found.", "Bom category not found for id %d"),
    BATCH_NOT_FOUND_BY_ID("Batch with given id not found.", "Batch not found for id %d"),
    COMPONENT_SUBCATEGORY_NOT_FOUND_BY_ID("Component subcategory with given id not found.", "Component subcategory not found for id %d"),
    USER_NOT_FOUND_BY_ID("User with given id not found.", "User not found for id %d"),
    REMINDER_NOT_FOUND_BY_ID("Reminder with given id not found.", "Reminder not found for id %d"),
    BOM_VERSION_NOT_FOUND_BY_ID("Bom version with the given id not found", "Bom version not found for id %d"),
    PRODUCT_MODEL_VERSION_NOT_FOUND_BY_ID("Product model version with the given id not found", "Product model version not found for id %d"),
    PRODUCT_MODEL_BOM_NOT_FOUND_BY_ID("Product model bom with the given id not found", "Product model bom not found for id %d"),
    PRODUCT_MODEL_BOM_VERSION_NOT_FOUND_BY_ID("Product model bom version with the given id not found", "Product model bom version not found for id %d"),
    COMPONENT_PRICE_HISTORY_NOT_FOUND_BY_ID("Component price history with the given id not found", "Component price history not found for id %d"),
    COMPONENT_METADATA_NOT_FOUND_BY_ID("Component metadata with the given id not found", "Component metadata not found for id %d"),
    TEST_DEVICE_LOGS_NOT_FOUND_BY_ID("Test device logs with the given id not found", "Test device logs not found for id %d"),
    LOT_STATUS_NOT_FOUND_BY_ID("Lot status not found for the given id", "Lot status not found for id %d"),
    PRODUCT_STATUS_NOT_FOUND_BY_ID("Product status not found for the given id", "Product status not found for id %d"),
    PRODUCT_MODEL_BOM_NOT_FOUND_BY_PRODUCT_MODEL_AND_BOM("No relationship found for the given product model and bom", "Relationship not found for product model with id %d and bom with id %d"),
    ACTIVITY_NOT_FOUND_BY_ID("Activity with the given id not found", null),
    BOM_COMPONENT_HISTORY_NOT_FOUND_BY_BOM_COMPONENT_ID_AND_BOM_VERSION("Bom component history not found for the given bom component id and bom version", "Bom component history not found for bom component with id %d and bom version %d"),
    COMPANY_NOT_FOUND_BY_NAME("Company not found for the given name", "Company not found for name %s"),
    COMPANY_NOT_FOUND_BY_ID("Company with given id not found.", "Company not found for id %d"),
    PRODUCT_SHIPMENT_STATUS_NOT_FOUND_BY_ID("Product shipment status not found by id", "Product shipment status not found for id %d"),
    PRODUCT_SHIPMENT_PRODUCT_NOT_FOUND_BY_ID("Product shipment product not found by id", "Product shipment product not found for id %d"),
    PRODUCT_SHIPMENT_NOT_FOUND_BY_ID("Product shipment not found by id", "Product shipment not found for id %d"),
    PRODUCT_MODEL_REQUIRED_COMPONENT_NOT_FOUND_BY_ID("Product model required component not found by id", "Product model required component not found for id %d"),
    PRODUCT_SHIPMENT_STATUS_NOT_FOUND_BY_NAME("Product shipment status not found by name", "Product shipment status not found for name %s"),
    RMA_NOT_FOUND_BY_ID("Rma not found by id", "Rma not found for id %d"),
    UPLOAD_TRANSACTION_NOT_FOUND_BY_ID("Upload transaction not found by id", "Upload transaction not found for id %d"),
    RMA_STATUS_NOT_FOUND_BY_ID("Rma status not found by id", "Rma status not found by id %d"),
    RMA_STATUS_NOT_FOUND_BY_NAME("Rma status not found by name", "Rma status not found by name %s"),
    PRODUCT_STATUS_NOT_FOUND_BY_NAME("Product status not found by name", "Product status not found by name %s"),
    LOT_STATUS_NOT_FOUND_BY_NAME("Missing LOT status by name", "LOT status not found by name %s"),
    LOT_NOT_FOUND_BY_NAME("Lot with the given name was not found in the db by name", "Lot not found by name %s"),
    COMPONENT_MANUFACTURER_NOT_FOUND_BY_NAME("component manufacturer not found by name", "Component manufacturer not found by name %s"),
    COMPONENT_MANUFACTURER_NOT_FOUND_BY_ID("component manufacturer not found by id", "Component manufacturer not found by id %d"),
    COMPONENT_CATEGORY_NOT_FOUND_BY_NAME("Component category not found by name", "Component category not found by name %s"),
    COMPONENT_CATEGORY_NOT_FOUND_BY_ID("Component category not found by id", "Component category not found by id %d"),
    BOM_DIFF_TEMPLATE_NOT_FOUND_BY_ID("Bom diff template with the given id was not found in the db", "Bom diff template not found for id %d"),
    PRODUCT_SHIPMENT_PRODUCT_NOT_FOUND_BY_PRODUCT_SHIPMENT_AND_PRODUCT("Product shipment product not found for product shipment and product", "Product shipment product not found for product shipment with id %d and product with id %d"),
    BOM_VERSION_NOT_FOUND_FOR_THE_BOM_AND_VERSION("Bom version with the given bom and version not found", "Bom version not found for bom with id %d and bom version %d"),
    BOM_VERSION_NOT_FOUND_FOR_THE_PARENT_BOM_ID("No bom version found for the given parent bom id", "Bom version not found for the parent bom id %d"),
    PRODUCT_MODEL_BOM_VERSION_NOT_FOUND_FOR_THE_BOM_AND_VERSION("Product model bom version not found for the given bom and version", "Product model bom version not found for the bom with id %d and version %d"),
    PRODUCT_MODEL_VERSION_NOT_FOUND_FOR_THE_PRODUCT_MODEL_AND_VERSION("No product model version found for the given product model and version", "Product model version not found for product model with id %d and version %d"),
    PRODUCT_MODEL_REQUIRED_COMPONENT_NOT_FOUND_FOR_PRODUCT_MODEL_AND_COMPONENT("Product model required component not found by given product model and component", "Product model required component not found for product model with id %d and component with id %d"),
    BOM_COMPONENT_HISTORY_NOT_FOUND_FOR_BOM_COMPONENT_ID_AND_VERSION("BomComponentHistory not found for the given bomComponentId and bomVersion", "Bom component history not found for bom component id %d and bom version %d"),
    ACTION_NOT_FOUND_BY_NAME("Action not found by name", "Action not found by name: %s"),
    ENTITY_TABLE_NOT_FOUND_BY_NAME("Entity table not found by name", "Entity table not found by name: %s"),
    ENTITY_TYPE_NOT_FOUND_BY_ID("Entity type not found by id", "Entity type not found by id: %d"),
    ENTITY_METADATA_NOT_FOUND_BY_ENTITY_ID_AND_ENTITY_TYPE_ID("Entity metadata not found by entity id and entity type id", "Entity metadata not found by entity id: %d and entity type id: %d"),
    ENTITY_TYPE_NOT_FOUND_BY_NAME("Entity type not found by name", "Entity type not found by name: %s"),
    ACTION_NOT_FOUND_BY_ID("Action not found by id", "Action not found by id: %d"),
    SUBSCRIPTION_NOT_FOUND_BY_EMAIL_AND_ENTITY_METADATA_ID("Subscription not found by email and entity metadata id", "Subscription not found by email: %s and entity metadata id: %d"),
    CRUD_ACTION_NOT_FOUND_BY_ID("Crud action not found by id", "Crud action not found by id: %d"),
    SUBSCRIPTION_NOT_FOUND_BY_ID("Subscription not found by id", "Subscription not found by id: %d"),
    SUBSCRIPTION_NOT_FOUND_BY_SUBSCRIBER_ID_AND_ENTITY_METADATA_ID("Subscription not found by subscriber id and entity metadata id", "Subscription not found by subscriber id: %d and entity metadata id: %d"),
    CRUD_ACTION_NOT_FOUND_BY_NAME("Crud action not found by name", "Crud action not found by name: %s"),
    USER_NOT_FOUND_BY_USERNAME("User not found by username", "User not found by username: %s"),
    DOCUMENT_NOT_FOUND_ON_S3_BY_KEY_FOR_BUCKET("Document not found on S3 by key for bucket", "Document not found on S3 by key: %s for bucket: %s");

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
