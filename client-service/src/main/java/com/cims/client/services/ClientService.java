package com.cims.client.services;

import com.cims.client.constants.CommonMessages;
import com.cims.client.constants.VarList;
import com.cims.client.constants.enums.Status;
import com.cims.client.constants.validationMessages.ClientValidationMessages;
import com.cims.client.constants.validationMessages.CommonValidationMessages;
import com.cims.client.dtos.ClientDTO;
import com.cims.client.dtos.UserAccountResponseDTO;
import com.cims.client.entities.Client;
import com.cims.client.proxyClients.UserServiceClient;
import com.cims.client.repositories.ClientRepository;
import com.cims.client.utils.MapperUtils;
import com.cims.client.utils.ResponseDTO;
import com.cims.client.utils.ResponseUtils;
import com.cims.client.utils.ValidatorUtils;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service class that handles operations related to Client.
 */
@Service
@Transactional
@Slf4j
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private MapperUtils mapperUtils;

    @Autowired
    private ResponseUtils responseUtils;

    /**
     * Saves or updates a client in the database based on the provided client DTO.
     *
     * @param clientDTO the DTO representing the client to be saved or updated
     * @return a ResponseEntity containing a ResponseDTO with details of the operation's success or failure
     */
    public ResponseEntity<ResponseDTO> saveUpdateClient(String token, ClientDTO clientDTO) {
        try {
            UserAccountResponseDTO userAccountResponseDTO = userServiceClient.getByTokenForClient(token);
            List<String> validations = validateClient(clientDTO);
            if (!CollectionUtils.isEmpty(validations)) {
                return responseUtils.createResponseDTO(VarList.RSP_FAIL, validations, null, HttpStatus.ACCEPTED);
            }
            clientDTO.setBranchCode(userAccountResponseDTO.getBranchCode());
            saveClientToDatabase(mapperUtils.mapDTOToEntity(clientDTO, Client.class));
            return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.SAVED_SUCCESSFULLY, clientDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("/**************** Exception in ClientService -> saveUpdateClient()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all clients from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of ClientDTOs representing all clients from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllClients(String token) {
        try {
            List<Client> clients = getClientsFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(clients, ClientDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in ClientService -> getAllClients()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all active clients from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of ClientDTOs representing all active clients from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllActiveClients(String token) {
        try {
            List<Client> clients = getActiveClientsFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(clients, ClientDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in ClientService -> getAllActiveClients()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a client from the database based on the provided ID and maps it to a DTO before returning it in a ResponseEntity.
     *
     * @param id the ID of the client to be retrieved
     * @return a ResponseEntity containing a ClientDTO representing the client with the provided ID, or an error response if the client does not exist or an error occurs
     */
    public ResponseEntity<ResponseDTO> getClientById(String id) {
        try {
            if (isClientExistById(id)) {
                Client clients = getClientFromDatabaseById(id);
                return mapperUtils.mapEntityToDTO(clients, ClientDTO.class);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in ClientService -> getClientById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a client from the database based on the provided ID and returns a ResponseEntity with details of the operation's success or failure.
     *
     * @param id the ID of the client to be deleted
     * @return a ResponseEntity indicating whether the deletion was successful or if an error occurred
     */
    public ResponseEntity<ResponseDTO> deleteClientById(String id) {
        try {
            if (isClientExistById(id)) {
                deleteClientFromDatabase(id);
                return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in ClientService -> deleteClientById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Saves a client entity to the database.
     *
     * @param client the client entity to be saved
     */
    public void saveClientToDatabase(Client client) {
        try {
            clientRepository.save(client);
        } catch (Exception e) {
            log.warn("/**************** Exception in ClientService -> saveClientToDatabase()" + e);
        }
    }

    /**
     * Retrieves all clients from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of Client entities contain all clients from the database that belong to the same branch
     */
    public List<Client> getClientsFromDatabase(String token) {
        try {
            String branchCode = userServiceClient.getByTokenForClient(token).getBranchCode();
            return clientRepository.findAllByBranchCode(branchCode);
        } catch (Exception e) {
            log.warn("/**************** Exception in ClientService -> getClientsFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves all active clients from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of Client entities contain all active clients from the database that belong to the same branch
     */
    public List<Client> getActiveClientsFromDatabase(String token) {
        try {
            Predicate<Client> filterOnStatus = fy -> fy.getStatus().equals(Status.ACTIVE);
            return getClientsFromDatabase(token).stream().filter(filterOnStatus).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("/**************** Exception in ClientService -> getActiveClientsFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a client entity from the database based on its ID.
     *
     * @param id the ID of the client entity to retrieve
     * @return a Client entity representing the requested client, or null if the ID is not valid or an error occurs
     */
    public Client getClientFromDatabaseById(String id) {
        try {
            ObjectId objectId = new ObjectId(id);
            return clientRepository.findById(objectId).get();
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in ClientService -> getClientFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a Client object from the database that matches the given Client nic.
     *
     * @param nic a String representing the client nic to search for
     * @return a Client object if one is found, or null if none is found or if an exception occurs during the search
     */
    public Client getClientFromDatabaseByNIC(String nic) {
        try {
            return clientRepository.findByNic(nic);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in ClientService -> getClientFromDatabaseByNIC()" + e);
            return null;
        }
    }

    /**
     * Deletes a client from the database by its ID.
     *
     * @param id the ID of the client to be deleted
     */
    public void deleteClientFromDatabase(String id) {
        try {
            ObjectId objectId = new ObjectId(id);
            clientRepository.deleteById(objectId);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in ClientService -> deleteClientFromDatabase()" + e);
        }
    }

    /**
     * Checks whether a client exists in the database by its ID.
     *
     * @param id the ID of the client to check for existence
     * @return true if a client with the specified ID exists in the database, false otherwise
     */
    public boolean isClientExistById(String id) {
        try {
            ObjectId objectId = new ObjectId(id);
            return clientRepository.existsById(objectId);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in ClientService -> isClientExist()" + e);
            return false;
        }
    }

    /**
     * Validates the client data transfer object by checking if the required fields
     * are not empty and if the client already exists in the database. Returns a list
     * of validation error messages.
     *
     * @param clientDTO the client data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the client is valid
     */
    private List<String> validateClient(ClientDTO clientDTO) {
        List<String> validations = new ArrayList<>();
        // Check if client fields
        validations.addAll(validateEmptyClientFields(clientDTO));
        return validations;
    }

    /**
     * Validates the client data transfer object by checking if the required fields
     * are not empty. Returns a list of validation error messages.
     *
     * @param clientDTO the client data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the fields are valid
     */
    private List<String> validateEmptyClientFields(ClientDTO clientDTO) {
        List<String> validations = new ArrayList<>();
        if (ValidatorUtils.stringNullValidator.test(clientDTO.getFirstName())) {
            validations.add(ClientValidationMessages.EMPTY_FIRST_NAME);
        } else if (ValidatorUtils.stringNullValidator.test(clientDTO.getLastName())) {
            validations.add(ClientValidationMessages.EMPTY_LAST_NAME);
        } else if (ValidatorUtils.stringNullValidator.test(clientDTO.getFullName())) {
            validations.add(ClientValidationMessages.EMPTY_FULL_NAME);
        } else if (ValidatorUtils.stringNullValidator.test(clientDTO.getNic())) {
            validations.add(ClientValidationMessages.EMPTY_NIC);
        } else if (ValidatorUtils.stringNullValidator.test(clientDTO.getMobileNumber())) {
            validations.add(ClientValidationMessages.EMPTY_MOBILE_NUMBER);
        } else if (ValidatorUtils.stringNullValidator.test(clientDTO.getLandNumber())) {
            validations.add(ClientValidationMessages.EMPTY_LAND_NUMBER);
        } else if (ValidatorUtils.stringNullValidator.test(clientDTO.getAddress())) {
            validations.add(ClientValidationMessages.EMPTY_ADDRESS);
        } else if (ValidatorUtils.statusValidator.test(clientDTO.getStatus().toString())) {
            validations.add(CommonValidationMessages.EMPTY_STATUS);
        } else if (CollectionUtils.isEmpty(validations)) {
            validations.addAll(validateClientFields(clientDTO));
        }
        return validations;
    }

    /**
     * Validates the client data transfer object by checking if the required fields
     * are not invalid. Returns a list of validation error messages.
     *
     * @param clientDTO the client data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the fields are valid
     */
    private List<String> validateClientFields(ClientDTO clientDTO) {
        List<String> validations = new ArrayList<>();
        if (!ValidatorUtils.nicValidator.test(clientDTO.getNic())) {
            validations.add(ClientValidationMessages.INVALID_NIC);
        } else if (!ValidatorUtils.phoneNumberValidator.test(clientDTO.getMobileNumber())) {
            validations.add(ClientValidationMessages.INVALID_MOBILE_NUMBER);
        } else if (!ValidatorUtils.phoneNumberValidator.test(clientDTO.getLandNumber())) {
            validations.add(ClientValidationMessages.INVALID_LAND_NUMBER);
        }
        return validations;
    }

}
