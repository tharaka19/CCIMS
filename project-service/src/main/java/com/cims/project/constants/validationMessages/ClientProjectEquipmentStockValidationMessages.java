package com.cims.project.constants.validationMessages;

/**
 * The {@code ClientProjectEquipmentStockValidationMessages} class contains constants for client project equipment stock validation messages.
 * These messages are used by the application to validate equipment type information provided by the user.
 * The constants are used as keys to retrieve the corresponding message from the resource bundle and
 * display them to the user as feedback for validation errors.
 */
public class ClientProjectEquipmentStockValidationMessages {
    public static final String EMPTY_EQUIPMENT = "Please select a equipment.";
    public static final String EMPTY_OPERATION = "Please select a operation.";
    public static final String EMPTY_EQUIPMENT_QUANTITY = "Please enter equipment quantity.";
    public static final String INVALID_EQUIPMENT_QUANTITY = "Invalid equipment quantity.";
    public static final String INVALID_ADD_EQUIPMENT_QUANTITY = "Invalid add equipment quantity.";
    public static final String INVALID_DEFECT_EQUIPMENT_QUANTITY = "Invalid defect equipment quantity.";
    public static final String INVALID_REMOVE_EQUIPMENT_QUANTITY = "Invalid remove equipment quantity.";
}
