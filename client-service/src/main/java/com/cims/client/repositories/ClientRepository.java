package com.cims.client.repositories;

import com.cims.client.entities.Client;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This is a repository interface for performing database operations on Client entities.
 */
@Repository
public interface ClientRepository extends MongoRepository<Client, ObjectId> {
    List<Client> findAllByBranchCode(String branchCode);

    Client findByNic(String nic);
}