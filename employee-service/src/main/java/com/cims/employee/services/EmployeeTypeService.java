package com.cims.employee.services;

import com.cims.employee.constants.CommonMessages;
import com.cims.employee.constants.VarList;
import com.cims.employee.constants.enums.Status;
import com.cims.employee.constants.validationMessages.CommonValidationMessages;
import com.cims.employee.constants.validationMessages.EmployeeTypeValidationMessages;
import com.cims.employee.dtos.EmployeeTypeDTO;
import com.cims.employee.dtos.UserAccountResponseDTO;
import com.cims.employee.entities.EmployeeType;
import com.cims.employee.proxyClients.UserServiceClient;
import com.cims.employee.repositories.EmployeeTypeRepository;
import com.cims.employee.utils.*;
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
 * Service class that handles operations related to employee type.
 */
@Service
@Transactional
@Slf4j
public class EmployeeTypeService {

    @Autowired
    private EmployeeTypeRepository employeeTypeRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private MapperUtils mapperUtils;

    @Autowired
    private ResponseUtils responseUtils;

    /**
     * Saves or updates a employee type in the database based on the provided employee type DTO.
     *
     * @param employeeTypeDTO the DTO representing the employee type to be saved or updated
     * @return a ResponseEntity containing a ResponseDTO with details of the operation's success or failure
     */
    public ResponseEntity<ResponseDTO> saveUpdateEmployeeType(String token, EmployeeTypeDTO employeeTypeDTO) {
        try {
            UserAccountResponseDTO userAccountResponseDTO = userServiceClient.getByTokenForClient(token);
            List<String> validations = validateEmployeeType(employeeTypeDTO, userAccountResponseDTO.getBranchCode());
            if (!CollectionUtils.isEmpty(validations)) {
                return responseUtils.createResponseDTO(VarList.RSP_FAIL, validations, null, HttpStatus.ACCEPTED);
            }
            employeeTypeDTO.setBranchCode(userAccountResponseDTO.getBranchCode());
            saveEmployeeTypeToDatabase(mapperUtils.mapDTOToEntity(employeeTypeDTO, EmployeeType.class));
            return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.SAVED_SUCCESSFULLY, employeeTypeDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeTypeService -> saveUpdateEmployeeType()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all employee types from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of EmployeeTypeDTOs representing all employee types from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllEmployeeTypes(String token) {
        try {
            List<EmployeeType> employeeTypes = getEmployeeTypesFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(employeeTypes, EmployeeTypeDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeTypeService -> getAllEmployeeTypes()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all active employee types from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of EmployeeTypeDTOs representing all active employee types from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllActiveEmployeeTypes(String token) {
        try {
            List<EmployeeType> employeeTypes = getActiveEmployeeTypesFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(employeeTypes, EmployeeTypeDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeTypeService -> getAllActiveEmployeeTypes()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a employee type from the database based on the provided ID and maps it to a DTO before returning it in a ResponseEntity.
     *
     * @param id the ID of the employee type to be retrieved
     * @return a ResponseEntity containing a EmployeeTypeDTO representing the employee type with the provided ID, or an error response if the employee type does not exist or an error occurs
     */
    public ResponseEntity<ResponseDTO> getEmployeeTypeById(String id) {
        try {
            if (isEmployeeTypeExistById(id)) {
                EmployeeType employeeTypes = getEmployeeTypeFromDatabaseById(id);
                return mapperUtils.mapEntityToDTO(employeeTypes, EmployeeTypeDTO.class);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeTypeService -> getEmployeeTypeById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a employee type from the database based on the provided ID and returns a ResponseEntity with details of the operation's success or failure.
     *
     * @param id the ID of the employee type to be deleted
     * @return a ResponseEntity indicating whether the deletion was successful or if an error occurred
     */
    public ResponseEntity<ResponseDTO> deleteEmployeeTypeById(String id) {
        try {
            if (isEmployeeTypeExistById(id)) {
                deleteEmployeeTypeFromDatabase(id);
                return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeTypeService -> deleteEmployeeTypeById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Saves a employee type entity to the database.
     *
     * @param employeeType the employee type entity to be saved
     */
    public void saveEmployeeTypeToDatabase(EmployeeType employeeType) {
        try {
            employeeTypeRepository.save(employeeType);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeTypeService -> saveEmployeeTypeToDatabase()" + e);
        }
    }

    /**
     * Retrieves all employee types from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of EmployeeType entities contain all employee types from the database that belong to the same branch
     */
    public List<EmployeeType> getEmployeeTypesFromDatabase(String token) {
        try {
            String branchCode = userServiceClient.getByTokenForClient(token).getBranchCode();
            return employeeTypeRepository.findAllByBranchCode(branchCode);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeTypeService -> getEmployeeTypesFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves all active employee types from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of EmployeeType entities contain all active employee types from the database that belong to the same branch
     */
    public List<EmployeeType> getActiveEmployeeTypesFromDatabase(String token) {
        try {
            Predicate<EmployeeType> filterOnStatus = fy -> fy.getStatus().equals(Status.ACTIVE);
            return getEmployeeTypesFromDatabase(token).stream().filter(filterOnStatus).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeTypeService -> getActiveEmployeeTypesFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a employee type entity from the database based on its ID.
     *
     * @param id the ID of the employee type entity to retrieve
     * @return a EmployeeType entity representing the requested employee type, or null if the ID is not valid or an error occurs
     */
    public EmployeeType getEmployeeTypeFromDatabaseById(String id) {
        try {
            return employeeTypeRepository.findById(Long.parseLong(id)).orElse(null);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EmployeeTypeService -> getEmployeeTypeFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a EmployeeType object from the database that matches the given employee type and branch code.
     *
     * @param employeeType a String representing the employee type to search for
     * @param branchCode   a String representing the branch code to search for
     * @return a EmployeeType object if one is found, or null if none is found or if an exception occurs during the search
     */
    public EmployeeType getEmployeeTypeFromDatabaseByEmployeeTypeAndBranchCode(String employeeType, String branchCode) {
        try {
            return employeeTypeRepository.findByEmployeeTypeAndBranchCode(employeeType, branchCode);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EmployeeTypeService -> getEmployeeTypeFromDatabaseByEmployeeTypeAndBranchCode()" + e);
            return null;
        }
    }

    /**
     * Deletes a employee type from the database by its ID.
     *
     * @param id the ID of the employee type to be deleted
     */
    public void deleteEmployeeTypeFromDatabase(String id) {
        try {
            employeeTypeRepository.deleteById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EmployeeTypeService -> deleteEmployeeTypeFromDatabase()" + e);
        }
    }

    /**
     * Checks whether a employee type exists in the database by its ID.
     *
     * @param id the ID of the employee type to check for existence
     * @return true if a employee type with the specified ID exists in the database, false otherwise
     */
    public boolean isEmployeeTypeExistById(String id) {
        try {
            return employeeTypeRepository.existsById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EmployeeTypeService -> isEmployeeTypeExist()" + e);
            return false;
        }
    }

    /**
     * Validates the employee type data transfer object by checking if the required fields
     * are not empty and if the employee type already exists in the database. Returns a list
     * of validation error messages.
     *
     * @param employeeTypeDTO the employee type data transfer object to be validated
     * @param branchCode      the branch code for which to validate the employee type
     * @return a list of validation error messages, or an empty list if the employee type is valid
     */
    private List<String> validateEmployeeType(EmployeeTypeDTO employeeTypeDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        // Check if employeeType fields
        validations.addAll(validateEmptyEmployeeTypeFields(employeeTypeDTO));
        // Check if employeeType exists
        validations.addAll(validateEmployeeTypeExistence(employeeTypeDTO, branchCode));
        return validations;
    }

    /**
     * Validates the employee type data transfer object by checking if the required fields
     * are not empty. Returns a list of validation error messages.
     *
     * @param employeeTypeDTO the employee type data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the fields are valid
     */
    private List<String> validateEmptyEmployeeTypeFields(EmployeeTypeDTO employeeTypeDTO) {
        List<String> validations = new ArrayList<>();
        if (ValidatorUtils.stringNullValidator.test(employeeTypeDTO.getEmployeeType())) {
            validations.add(EmployeeTypeValidationMessages.EMPTY_EMPLOYEE_TYPE);
        } else if (ValidatorUtils.stringNullValidator.test(employeeTypeDTO.getDescription())) {
            validations.add(EmployeeTypeValidationMessages.EMPTY_DESCRIPTION);
        } else if (ValidatorUtils.priceNullValidator.test(employeeTypeDTO.getBasicPay())) {
            validations.add(EmployeeTypeValidationMessages.EMPTY_BASIC_PAY);
        } else if (ValidatorUtils.priceNullValidator.test(employeeTypeDTO.getEpf())) {
            validations.add(EmployeeTypeValidationMessages.EMPTY_EPF);
        } else if (ValidatorUtils.priceNullValidator.test(employeeTypeDTO.getEpfCoContribution())) {
            validations.add(EmployeeTypeValidationMessages.EMPTY_EPF_CO_CONTRIBUTION);
        } else if (ValidatorUtils.priceNullValidator.test(employeeTypeDTO.getEtf())) {
            validations.add(EmployeeTypeValidationMessages.EMPTY_ETF);
        } else if (ValidatorUtils.statusValidator.test(employeeTypeDTO.getStatus().toString())) {
            validations.add(CommonValidationMessages.EMPTY_STATUS);
        } else if (CollectionUtils.isEmpty(validations)) {
            validations.addAll(validateEmployeeTypeFields(employeeTypeDTO));
        }
        return validations;
    }

    /**
     * Validates the employee type data transfer object by checking if the required fields
     * are not invalid. Returns a list of validation error messages.
     *
     * @param employeeTypeDTO the employee type data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the fields are valid
     */
    private List<String> validateEmployeeTypeFields(EmployeeTypeDTO employeeTypeDTO) {
        List<String> validations = new ArrayList<>();
        if (!ValidatorUtils.priceValidator.test(employeeTypeDTO.getBasicPay())) {
            validations.add(EmployeeTypeValidationMessages.INVALID_BASIC_PAY);
        } else if (!ValidatorUtils.priceValidator.test(employeeTypeDTO.getEpf())) {
            validations.add(EmployeeTypeValidationMessages.INVALID_EPF);
        } else if (!ValidatorUtils.priceValidator.test(employeeTypeDTO.getEpfCoContribution())) {
            validations.add(EmployeeTypeValidationMessages.INVALID_EPF_CO_CONTRIBUTION);
        } else if (!ValidatorUtils.priceValidator.test(employeeTypeDTO.getEtf())) {
            validations.add(EmployeeTypeValidationMessages.INVALID_ETF);
        }
        return validations;
    }

    /**
     * Validates the employee type data transfer object by checking if the employee type already
     * exists in the database. Returns a list of validation error messages.
     *
     * @param employeeTypeDTO the employee type data transfer object to be validated
     * @param branchCode      the branch code of the employee type to check for existence
     * @return a list of validation error messages, or an empty list if the employee type is new or valid
     */
    private List<String> validateEmployeeTypeExistence(EmployeeTypeDTO employeeTypeDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        EmployeeType employeeType = getEmployeeTypeFromDatabaseByEmployeeTypeAndBranchCode(employeeTypeDTO.getEmployeeType(), branchCode);
        if (!ValidatorUtils.entityNullValidator.test(employeeType)) {
            if (!ValidatorUtils.stringNullValidator.test(employeeTypeDTO.getId())) { // Edit employee type
                if (!employeeTypeDTO.getId().equalsIgnoreCase(employeeType.getId().toString())) {
                    validations.add(EmployeeTypeValidationMessages.EXIT_EMPLOYEE_TYPE);
                }
            } else { // New employee type
                validations.add(EmployeeTypeValidationMessages.EXIT_EMPLOYEE_TYPE);
            }
        }
        return validations;
    }
}
