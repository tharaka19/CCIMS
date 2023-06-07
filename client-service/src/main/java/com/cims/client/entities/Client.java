package com.cims.client.entities;

import com.cims.client.constants.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Entity class for holding client information.
 */
@Document(collection = "client")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client {

    @Id
    private ObjectId id;

    @Field(name = "first_name")
    private String firstName;

    @Field(name = "last_name")
    private String lastName;

    @Field(name = "full_name")
    private String fullName;

    @Field(name = "nic")
    private String nic;

    @Field(name = "mobile_number")
    private String mobileNumber;

    @Field(name = "land_number")
    private String landNumber;

    @Field(name = "address")
    private String address;

    @Field(name = "branch_code")
    private String branchCode;

    @Field(name = "status")
    private Status status;
}
