package com.cims.employee.services;

import com.cims.employee.constants.CommonMessages;
import com.cims.employee.constants.VarList;
import com.cims.employee.constants.enums.Status;
import com.cims.employee.constants.validationMessages.AllowanceTypeValidationMessages;
import com.cims.employee.constants.validationMessages.CommonValidationMessages;
import com.cims.employee.dtos.AllowanceTypeDTO;
import com.cims.employee.dtos.UserAccountResponseDTO;
import com.cims.employee.entities.AllowanceType;
import com.cims.employee.proxyClients.UserServiceClient;
import com.cims.employee.repositories.AllowanceTypeRepository;
import com.cims.employee.utils.MapperUtils;
import com.cims.employee.utils.ResponseDTO;
import com.cims.employee.utils.ResponseUtils;
import com.cims.employee.utils.ValidatorUtils;
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
 * Service class that handles operations related to allowance type.
 */
@Service
@Transactional
@Slf4j
public class AllowanceTypeService {

    @Autowired
    private AllowanceTypeRepository allowanceTypeRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private MapperUtils mapperUtils;

    @Autowired
    private ResponseUtils responseUtils;

    /**
     * Saves or updates a allowance type in the database based on the provided allowance type DTO.
     *
     * @param allowanceTypeDTO the DTO representing the allowance type to be saved or updated
     * @return a ResponseEntity containing a ResponseDTO with details of the operation's success or failure
     */
    public ResponseEntity<ResponseDTO> saveUpdateAllowanceType(String token, AllowanceTypeDTO allowanceTypeDTO) {
        try {
            UserAccountResponseDTO userAccountResponseDTO = userServiceClient.getByTokenForClient(token);
            List<String> validations = validateAllowanceType(allowanceTypeDTO, userAccountResponseDTO.getBranchCode());
            if (!CollectionUtils.isEmpty(validations)) {
                return responseUtils.createResponseDTO(VarList.RSP_FAIL, validations, null, HttpStatus.ACCEPTED);
            }
            allowanceTypeDTO.setBranchCode(userAccountResponseDTO.getBranchCode());
            saveAllowanceTypeToDatabase(mapperUtils.mapDTOToEntity(allowanceTypeDTO, AllowanceType.class));
            return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.SAVED_SUCCESSFULLY, allowanceTypeDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("/**************** Exception in AllowanceTypeService -> saveUpdateAllowanceType()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all allowance types from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of AllowanceTypeDTOs representing all allowance types from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllAllowanceTypes(String token) {
        try {
            List<AllowanceType> allowanceTypes = getAllowanceTypesFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(allowanceTypes, AllowanceTypeDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in AllowanceTypeService -> getAllAllowanceTypes()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all active allowance types from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of AllowanceTypeDTOs representing all active allowance types from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllActiveAllowanceTypes(String token) {
        try {
            List<AllowanceType> allowanceTypes = getActiveAllowanceTypesFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(allowanceTypes, AllowanceTypeDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in AllowanceTypeService -> getAllActiveAllowanceTypes()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a allowance type from the database based on the provided ID and maps it to a DTO before returning it in a ResponseEntity.
     *
     * @param id the ID of the allowance type to be retrieved
     * @return a ResponseEntity containing a AllowanceTypeDTO representing the allowance type with the provided ID, or an error response if the allowance type does not exist or an error occurs
     */
    public ResponseEntity<ResponseDTO> getAllowanceTypeById(String id) {
        try {
            if (isAllowanceTypeExistById(id)) {
                AllowanceType allowanceTypes = getAllowanceTypeFromDatabaseById(id);
                return mapperUtils.mapEntityToDTO(allowanceTypes, AllowanceTypeDTO.class);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in AllowanceTypeService -> getAllowanceTypeById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a allowance type from the database based on the provided ID and returns a ResponseEntity with details of the operation's success or failure.
     *
     * @param id the ID of the allowance type to be deleted
     * @return a ResponseEntity indicating whether the deletion was successful or if an error occurred
     */
    public ResponseEntity<ResponseDTO> deleteAllowanceTypeById(String id) {
        try {
            if (isAllowanceTypeExistById(id)) {
                deleteAllowanceTypeFromDatabase(id);
                return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in AllowanceTypeService -> deleteAllowanceTypeById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Saves a allowance type entity to the database.
     *
     * @param allowanceType the allowance type entity to be saved
     */
    public void saveAllowanceTypeToDatabase(AllowanceType allowanceType) {
        try {
            allowanceTypeRepository.save(allowanceType);
        } catch (Exception e) {
            log.warn("/**************** Exception in AllowanceTypeService -> saveAllowanceTypeToDatabase()" + e);
        }
    }

    /**
     * Retrieves all allowance types from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of AllowanceType entities contain all allowance types from the database that belong to the same branch
     */
    public List<AllowanceType> getAllowanceTypesFromDatabase(String token) {
        try {
            String branchCode = userServiceClient.getByTokenForClient(token).getBranchCode();
            return allowanceTypeRepository.findAllByBranchCode(branchCode);
        } catch (Exception e) {
            log.warn("/**************** Exception in AllowanceTypeService -> getAllowanceTypesFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves all active allowance types from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of AllowanceType entities contain all active allowance types from the database that belong to the same branch
     */
    public List<AllowanceType> getActiveAllowanceTypesFromDatabase(String token) {
        try {
            Predicate<AllowanceType> filterOnStatus = at -> at.getStatus().equals(Status.ACTIVE);
            return getAllowanceTypesFromDatabase(token).stream().filter(filterOnStatus).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("/**************** Exception in AllowanceTypeService -> getActiveAllowanceTypesFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a allowance type entity from the database based on its ID.
     *
     * @param id the ID of the allowance type entity to retrieve
     * @return a AllowanceType entity representing the requested allowance type, or null if the ID is not valid or an error occurs
     */
    public AllowanceType getAllowanceTypeFromDatabaseById(String id) {
        try {
            return allowanceTypeRepository.findById(Long.parseLong(id)).orElse(null);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in AllowanceTypeService -> getAllowanceTypeFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a AllowanceType object from the database that matches the given allowance type and branch code.
     *
     * @param allowanceType a String representing the allowance type to search for
     * @param branchCode    a String representing the branch code to search for
     * @return a AllowanceType object if one is found, or null if none is found or if an exception occurs during the search
     */
    public AllowanceType getAllowanceTypeFromDatabaseByAllowanceTypeAndBranchCode(String allowanceType, String branchCode) {
        try {
            return allowanceTypeRepository.findByAllowanceTypeAndBranchCode(allowanceType, branchCode);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in AllowanceTypeService -> getAllowanceTypeFromDatabaseByAllowanceTypeAndBranchCode()" + e);
            return null;
        }
    }

    /**
     * Deletes a allowance type from the database by its ID.
     *
     * @param id the ID of the allowance type to be deleted
     */
    public void deleteAllowanceTypeFromDatabase(String id) {
        try {
            allowanceTypeRepository.deleteById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in AllowanceTypeService -> deleteAllowanceTypeFromDatabase()" + e);
        }
    }

    /**
     * Checks whether a allowance type exists in the database by its ID.
     *
     * @param id the ID of the allowance type to check for existence
     * @return true if a allowance type with the specified ID exists in the database, false otherwise
     */
    public boolean isAllowanceTypeExistById(String id) {
        try {
            return allowanceTypeRepository.existsById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in AllowanceTypeService -> isAllowanceTypeExist()" + e);
            return false;
        }
    }

    /**
     * Validates the allowance type data transfer object by checking if the required fields
     * are not empty and if the allowance type already exists in the database. Returns a list
     * of validation error messages.
     *
     * @param allowanceTypeDTO the allowance type data transfer object to be validated
     * @param branchCode       the branch code for which to validate the allowance type
     * @return a list of validation error messages, or an empty list if the allowance type is valid
     */
    private List<String> validateAllowanceType(AllowanceTypeDTO allowanceTypeDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        // Check if allowanceType fields
        validations.addAll(validateAllowanceTypeFields(allowanceTypeDTO));
        // Check if allowanceType exists
        validations.addAll(validateAllowanceTypeExistence(allowanceTypeDTO, branchCode));
        return validations;
    }

    /**
     * Validates the allowance type data transfer object by checking if the required fields
     * are not empty. Returns a list of validation error messages.
     *
     * @param allowanceTypeDTO the allowance type data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the fields are valid
     */
    private List<String> validateAllowanceTypeFields(AllowanceTypeDTO allowanceTypeDTO) {
        List<String> validations = new ArrayList<>();
        if (ValidatorUtils.stringNullValidator.test(allowanceTypeDTO.getAllowanceType())) {
            validations.add(AllowanceTypeValidationMessages.EMPTY_ALLOWANCE_TYPE);
        } else if (ValidatorUtils.stringNullValidator.test(allowanceTypeDTO.getDescription())) {
            validations.add(AllowanceTypeValidationMessages.EMPTY_DESCRIPTION);
        } else if (ValidatorUtils.priceNullValidator.test(allowanceTypeDTO.getAllowancePay())) {
            validations.add(AllowanceTypeValidationMessages.EMPTY_ALLOWANCE_PAY);
        } else if (ValidatorUtils.statusValidator.test(allowanceTypeDTO.getStatus().toString())) {
            validations.add(CommonValidationMessages.EMPTY_STATUS);
        } else if (!ValidatorUtils.priceValidator.test(allowanceTypeDTO.getAllowancePay())) {
            validations.add(AllowanceTypeValidationMessages.INVALID_ALLOWANCE_PAY);
        }
        return validations;
    }

    /**
     * Validates the allowance type data transfer object by checking if the allowance type already
     * exists in the database. Returns a list of validation error messages.
     *
     * @param allowanceTypeDTO the allowance type data transfer object to be validated
     * @param branchCode       the branch code of the allowance type to check for existence
     * @return a list of validation error messages, or an empty list if the allowance type is new or valid
     */
    private List<String> validateAllowanceTypeExistence(AllowanceTypeDTO allowanceTypeDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        AllowanceType allowanceType = getAllowanceTypeFromDatabaseByAllowanceTypeAndBranchCode(allowanceTypeDTO.getAllowanceType(), branchCode);
        if (!ValidatorUtils.entityNullValidator.test(allowanceType)) {
            if (!ValidatorUtils.stringNullValidator.test(allowanceTypeDTO.getId())) { // Edit allowance type
                if (!allowanceTypeDTO.getId().equalsIgnoreCase(allowanceType.getId().toString())) {
                    validations.add(AllowanceTypeValidationMessages.EXIT_ALLOWANCE_TYPE);
                }
            } else { // New allowance type
                validations.add(AllowanceTypeValidationMessages.EXIT_ALLOWANCE_TYPE);
            }
        }
        return validations;
    }
}
