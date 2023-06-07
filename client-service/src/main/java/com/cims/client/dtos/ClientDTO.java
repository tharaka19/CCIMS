package com.cims.client.dtos;

import com.cims.client.constants.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class for client information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {

    private String id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String nic;
    private String mobileNumber;
    private String landNumber;
    private String address;
    private String branchCode;
    private Status status;
}
