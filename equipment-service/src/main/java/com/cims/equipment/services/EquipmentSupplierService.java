package com.cims.equipment.services;

import com.cims.equipment.constants.CommonMessages;
import com.cims.equipment.constants.VarList;
import com.cims.equipment.constants.enums.Status;
import com.cims.equipment.constants.validationMessages.CommonValidationMessages;
import com.cims.equipment.constants.validationMessages.EquipmentSupplierValidationMessages;
import com.cims.equipment.dtos.EquipmentSupplierDTO;
import com.cims.equipment.dtos.UserAccountResponseDTO;
import com.cims.equipment.entities.EquipmentSupplier;
import com.cims.equipment.proxyClients.UserServiceClient;
import com.cims.equipment.repositories.EquipmentSupplierRepository;
import com.cims.equipment.utils.MapperUtils;
import com.cims.equipment.utils.ResponseDTO;
import com.cims.equipment.utils.ResponseUtils;
import com.cims.equipment.utils.ValidatorUtils;
import lombok.extern.slf4j.Slf4j;
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
 * Service class that handles operations related to equipment supplier.
 */
@Service
@Transactional
@Slf4j
public class EquipmentSupplierService {

    @Autowired
    private EquipmentSupplierRepository equipmentSupplierRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private MapperUtils mapperUtils;

    @Autowired
    private ResponseUtils responseUtils;

    /**
     * Saves or updates a equipment supplier in the database based on the provided equipment supplier DTO.
     *
     * @param equipmentSupplierDTO the DTO representing the equipment supplier to be saved or updated
     * @return a ResponseEntity containing a ResponseDTO with details of the operation's success or failure
     */
    public ResponseEntity<ResponseDTO> saveUpdateEquipmentSupplier(String token, EquipmentSupplierDTO equipmentSupplierDTO) {
        try {
            UserAccountResponseDTO userAccountResponseDTO = userServiceClient.getByTokenForClient(token);
            List<String> validations = validateEquipmentSupplier(equipmentSupplierDTO, userAccountResponseDTO.getBranchCode());
            if (!CollectionUtils.isEmpty(validations)) {
                return responseUtils.createResponseDTO(VarList.RSP_FAIL, validations, null, HttpStatus.ACCEPTED);
            }
            equipmentSupplierDTO.setBranchCode(userAccountResponseDTO.getBranchCode());
            saveEquipmentSupplierToDatabase(mapperUtils.mapDTOToEntity(equipmentSupplierDTO, EquipmentSupplier.class));
            return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.SAVED_SUCCESSFULLY, equipmentSupplierDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentSupplierService -> saveUpdateEquipmentSupplier()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all equipment suppliers from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of EquipmentSupplierDTOs representing all equipment suppliers from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllEquipmentSuppliers(String token) {
        try {
            List<EquipmentSupplier> equipmentSuppliers = getEquipmentSuppliersFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(equipmentSuppliers, EquipmentSupplierDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentSupplierService -> getAllEquipmentSuppliers()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all active equipment suppliers from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of EquipmentSupplierDTOs representing all active equipment suppliers from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllActiveEquipmentSuppliers(String token) {
        try {
            List<EquipmentSupplier> equipmentSuppliers = getActiveEquipmentSuppliersFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(equipmentSuppliers, EquipmentSupplierDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentSupplierService -> getAllActiveEquipmentSuppliers()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a equipment supplier from the database based on the provided ID and maps it to a DTO before returning it in a ResponseEntity.
     *
     * @param id the ID of the equipment supplier to be retrieved
     * @return a ResponseEntity containing a EquipmentSupplierDTO representing the equipment supplier with the provided ID, or an error response if the equipment supplier does not exist or an error occurs
     */
    public ResponseEntity<ResponseDTO> getEquipmentSupplierById(String id) {
        try {
            if (isEquipmentSupplierExistById(id)) {
                EquipmentSupplier equipmentSuppliers = getEquipmentSupplierFromDatabaseById(id);
                return mapperUtils.mapEntityToDTO(equipmentSuppliers, EquipmentSupplierDTO.class);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentSupplierService -> getEquipmentSupplierById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a equipment supplier from the database based on the provided ID and returns a ResponseEntity with details of the operation's success or failure.
     *
     * @param id the ID of the equipment supplier to be deleted
     * @return a ResponseEntity indicating whether the deletion was successful or if an error occurred
     */
    public ResponseEntity<ResponseDTO> deleteEquipmentSupplierById(String id) {
        try {
            if (isEquipmentSupplierExistById(id)) {
                deleteEquipmentSupplierFromDatabase(id);
                return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentSupplierService -> deleteEquipmentSupplierById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Saves a equipment supplier entity to the database.
     *
     * @param equipmentSupplier the equipment supplier entity to be saved
     */
    public void saveEquipmentSupplierToDatabase(EquipmentSupplier equipmentSupplier) {
        try {
            equipmentSupplierRepository.save(equipmentSupplier);
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentSupplierService -> saveEquipmentSupplierToDatabase()" + e);
        }
    }

    /**
     * Retrieves all equipment suppliers from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of EquipmentSupplier entities contain all equipment suppliers from the database that belong to the same branch
     */
    public List<EquipmentSupplier> getEquipmentSuppliersFromDatabase(String token) {
        try {
            String branchCode = userServiceClient.getByTokenForClient(token).getBranchCode();
            return equipmentSupplierRepository.findAllByBranchCode(branchCode);
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentSupplierService -> getEquipmentSuppliersFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves all active equipment suppliers from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of EquipmentSupplier entities contain all active equipment suppliers from the database that belong to the same branch
     */
    public List<EquipmentSupplier> getActiveEquipmentSuppliersFromDatabase(String token) {
        try {
            Predicate<EquipmentSupplier> filterOnStatus = fy -> fy.getStatus().equals(Status.ACTIVE);
            return getEquipmentSuppliersFromDatabase(token).stream().filter(filterOnStatus).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentSupplierService -> getActiveEquipmentSuppliersFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a equipment supplier entity from the database based on its ID.
     *
     * @param id the ID of the equipment supplier entity to retrieve
     * @return a EquipmentSupplier entity representing the requested equipment supplier, or null if the ID is not valid or an error occurs
     */
    public EquipmentSupplier getEquipmentSupplierFromDatabaseById(String id) {
        try {
            return equipmentSupplierRepository.findById(Long.parseLong(id)).orElse(null);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EquipmentSupplierService -> getEquipmentSupplierFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a EquipmentSupplier object from the database that matches the given equipment supplier and branch code.
     *
     * @param supplierNumber a String representing the equipment supplier number to search for
     * @param branchCode   a String representing the branch code to search for
     * @return a EquipmentSupplier object if one is found, or null if none is found or if an exception occurs during the search
     */
    public EquipmentSupplier getEquipmentSupplierFromDatabaseByEquipmentSupplierAndBranchCode(String supplierNumber, String branchCode) {
        try {
            return equipmentSupplierRepository.findBySupplierNumberAndBranchCode(supplierNumber, branchCode);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EquipmentSupplierService -> getEquipmentSupplierFromDatabaseByEquipmentSupplierAndBranchCode()" + e);
            return null;
        }
    }

    /**
     * Deletes a equipment supplier from the database by its ID.
     *
     * @param id the ID of the equipment supplier to be deleted
     */
    public void deleteEquipmentSupplierFromDatabase(String id) {
        try {
            equipmentSupplierRepository.deleteById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EquipmentSupplierService -> deleteEquipmentSupplierFromDatabase()" + e);
        }
    }

    /**
     * Checks whether a equipment supplier exists in the database by its ID.
     *
     * @param id the ID of the equipment supplier to check for existence
     * @return true if a equipment supplier with the specified ID exists in the database, false otherwise
     */
    public boolean isEquipmentSupplierExistById(String id) {
        try {
            return equipmentSupplierRepository.existsById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EquipmentSupplierService -> isEquipmentSupplierExist()" + e);
            return false;
        }
    }

    /**
     * Validates the equipment supplier data transfer object by checking if the required fields
     * are not empty and if the equipment supplier already exists in the database. Returns a list
     * of validation error messages.
     *
     * @param equipmentSupplierDTO the equipment supplier data transfer object to be validated
     * @param branchCode      the branch code for which to validate the equipment supplier
     * @return a list of validation error messages, or an empty list if the equipment supplier is valid
     */
    private List<String> validateEquipmentSupplier(EquipmentSupplierDTO equipmentSupplierDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        // Check if equipment supplier fields
        validations.addAll(validateEquipmentSupplierFields(equipmentSupplierDTO));
        // Check if equipment supplier exists
        validations.addAll(validateEquipmentSupplierExistence(equipmentSupplierDTO, branchCode));
        return validations;
    }

    /**
     * Validates the equipment supplier data transfer object by checking if the required fields
     * are not empty. Returns a list of validation error messages.
     *
     * @param equipmentSupplierDTO the equipment supplier data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the fields are valid
     */
    private List<String> validateEquipmentSupplierFields(EquipmentSupplierDTO equipmentSupplierDTO) {
        List<String> validations = new ArrayList<>();
        if (ValidatorUtils.stringNullValidator.test(equipmentSupplierDTO.getSupplierNumber())) {
            validations.add(EquipmentSupplierValidationMessages.EMPTY_SUPPLIER_NUMBER);
        } else if (ValidatorUtils.stringNullValidator.test(equipmentSupplierDTO.getSupplierName())) {
            validations.add(EquipmentSupplierValidationMessages.EMPTY_SUPPLIER_NAME);
        } else if (ValidatorUtils.stringNullValidator.test(equipmentSupplierDTO.getMobileNumber())) {
            validations.add(EquipmentSupplierValidationMessages.EMPTY_MOBILE_NUMBER);
        } else if (ValidatorUtils.stringNullValidator.test(equipmentSupplierDTO.getLandNumber())) {
            validations.add(EquipmentSupplierValidationMessages.EMPTY_LAND_NUMBER);
        } else if (ValidatorUtils.stringNullValidator.test(equipmentSupplierDTO.getAddress())) {
            validations.add(EquipmentSupplierValidationMessages.EMPTY_ADDRESS);
        } else if (ValidatorUtils.statusValidator.test(equipmentSupplierDTO.getStatus().toString())) {
            validations.add(CommonValidationMessages.EMPTY_STATUS);
        } else if (!ValidatorUtils.phoneNumberValidator.test(equipmentSupplierDTO.getMobileNumber())) {
            validations.add(EquipmentSupplierValidationMessages.INVALID_MOBILE_NUMBER);
        } else if (!ValidatorUtils.phoneNumberValidator.test(equipmentSupplierDTO.getLandNumber())) {
            validations.add(EquipmentSupplierValidationMessages.INVALID_LAND_NUMBER);
        }
        return validations;
    }

    /**
     * Validates the equipment supplier data transfer object by checking if the equipment supplier already
     * exists in the database. Returns a list of validation error messages.
     *
     * @param equipmentSupplierDTO the equipment supplier data transfer object to be validated
     * @param branchCode      the branch code of the equipment supplier to check for existence
     * @return a list of validation error messages, or an empty list if the equipment supplier is new or valid
     */
    private List<String> validateEquipmentSupplierExistence(EquipmentSupplierDTO equipmentSupplierDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        EquipmentSupplier equipmentSupplier = getEquipmentSupplierFromDatabaseByEquipmentSupplierAndBranchCode(equipmentSupplierDTO.getSupplierNumber(), branchCode);
        if (!ValidatorUtils.entityNullValidator.test(equipmentSupplier)) {
            if (!ValidatorUtils.stringNullValidator.test(equipmentSupplierDTO.getId())) { // Edit equipment supplier
                if (!equipmentSupplierDTO.getId().equalsIgnoreCase(equipmentSupplier.getId().toString())) {
                    validations.add(EquipmentSupplierValidationMessages.EXIT_SUPPLIER_NUMBER_);
                }
            } else { // New equipment supplier
                validations.add(EquipmentSupplierValidationMessages.EXIT_SUPPLIER_NUMBER_);
            }
        }
        return validations;
    }
}
