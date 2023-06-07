package com.cims.client.constants;

/**
 * The {@code VarList} class contains a list of constant variables used for response codes in the application.
 * These codes are used to indicate the status of a request, such as whether it was successful or not,
 * The constants are defined as strings and can be accessed
 * statically through the class.
 * <p>
 * The following response codes are defined:
 * <p>
 * - RSP_SUCCESS: Indicates that the request was successful.
 * - RSP_NO_DATA_FOUND: Indicates that no data was found for the request.
 * - RSP_NOT_AUTHORISED: Indicates that the user is not authorized to perform the requested operation.
 * - RSP_ERROR: Indicates that there was an error with the request.
 * - RSP_DUPLICATED: Indicates that the requested data already exists in the system.
 * - RSP_FAIL: Indicates that the request failed.
 */
public class VarList {
    public static final String RSP_SUCCESS = "00";
    public static final String RSP_NO_DATA_FOUND = "01";
    public static final String RSP_NOT_AUTHORISED = "02";
    public static final String RSP_ERROR = "03";
    public static final String RSP_DUPLICATED = "04";
    public static final String RSP_FAIL = "10";
}
