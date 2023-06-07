package com.cims.equipment.constants.validationMessages;

/**
 * The {@code EquipmentStockValidationMessages} class contains constants for equipment stock validation messages.
 * These messages are used by the application to validate equipment type information provided by the user.
 * The constants are used as keys to retrieve the corresponding message from the resource bundle and
 * display them to the user as feedback for validation errors.
 */
public class EquipmentStockValidationMessages {
    public static final String EMPTY_EQUIPMENT_TYPE = "Please select a equipment type.";
    public static final String EMPTY_EQUIPMENT = "Please select a equipment.";
    public static final String EMPTY_EQUIPMENT_SUPPLIER = "Please select a equipment supplier.";
    public static final String EMPTY_STOCK_NUMBER = "Please enter stock number.";
    public static final String EMPTY_PURCHASE_PRISE = "Please enter purchase price.";
    public static final String INVALID_PURCHASE_PRISE = "Invalid purchase price.";
    public static final String EXIT_STOCK_NUMBER = "Stock number already exists.";
}
