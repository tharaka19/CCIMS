package com.cims.auth.service.utils;

import com.cims.auth.service.constants.enums.ServiceName;
import com.cims.auth.service.dto.AuthRequestDTO;

/**
 * A utility class containing methods for authentication-related operations.
 */
public class AuthenticationUtils {

    /**
     * Prepends the service name prefix to the user name in the given authentication request.
     *
     * @param authRequest the authentication request containing the service name and user name.
     * @return the modified user name with the service name prefix.
     */
    public static String prependServiceNameToUserName(AuthRequestDTO authRequest) {
        String serviceNamePrefix = authRequest.getServiceName().equals(ServiceName.ADMIN) ? ServiceName.ADMIN.toString() : ServiceName.USER.toString();
        authRequest.setUserName(serviceNamePrefix.concat("_").concat(authRequest.getUserName()));
        return authRequest.getUserName();
    }

    /**
     * Extracts the service name from a full name string.
     *
     * @param fullName the full name string containing the service name prefix and user name, separated by an underscore.
     * @return the service name.
     */
    public static String getServiceName(String fullName){
        return fullName.split("_")[0];
    }

    /**
     * Extracts the user name from a full name string.
     *
     * @param fullName the full name string containing the service name prefix and user name, separated by an underscore.
     * @return the user name.
     */
    public static String getUserName(String fullName){
        return fullName.split("_")[1];
    }
}
