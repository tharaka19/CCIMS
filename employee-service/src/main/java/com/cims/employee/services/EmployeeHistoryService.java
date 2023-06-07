package com.cims.employee.services;

import com.cims.employee.constants.CommonMessages;
import com.cims.employee.constants.VarList;
import com.cims.employee.constants.enums.Status;
import com.cims.employee.constants.validationMessages.CommonValidationMessages;
import com.cims.employee.constants.validationMessages.EmployeeHistoryValidationMessages;
import com.cims.employee.dtos.EmployeeHistoryDTO;
import com.cims.employee.dtos.UserAccountResponseDTO;
import com.cims.employee.entities.EmployeeHistory;
import com.cims.employee.proxyClients.UserServiceClient;
import com.cims.employee.repositories.EmployeeHistoryRepository;
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
 * Service class that handles operations related to employee history.
 */
@Service
@Transactional
@Slf4j
public class EmployeeHistoryService {

    @Autowired
    private EmployeeHistoryRepository employeeHistoryRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private MapperUtils mapperUtils;

    @Autowired
    private ResponseUtils responseUtils;

    /**
     * Saves or updates a employee history in the database based on the provided employee history DTO.
     *
     * @param employeeHistoryDTO the DTO representing the employee history to be saved or updated
     * @return a ResponseEntity containing a ResponseDTO with details of the operation's success or failure
     */
    public ResponseEntity<ResponseDTO> saveUpdateEmployeeHistory(String token, EmployeeHistoryDTO employeeHistoryDTO) {
        try {
            UserAccountResponseDTO userAccountResponseDTO = userServiceClient.getByTokenForClient(token);
            List<String> validations = validateEmployeeHistory(employeeHistoryDTO);
            if (!CollectionUtils.isEmpty(validations)) {
                return responseUtils.createResponseDTO(VarList.RSP_FAIL, validations, null, HttpStatus.ACCEPTED);
            }
            employeeHistoryDTO.setBranchCode(userAccountResponseDTO.getBranchCode());
            saveEmployeeHistoryToDatabase(mapperUtils.mapDTOToEntity(employeeHistoryDTO, EmployeeHistory.class));
            return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.SAVED_SUCCESSFULLY, employeeHistoryDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeHistoryService -> saveUpdateEmployeeHistory()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all employee histories from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of EmployeeHistoryDTOs representing all employee histories from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllEmployeeHistories(String token) {
        try {
            List<EmployeeHistory> employeeHistories = getEmployeeHistoriesFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(employeeHistories, EmployeeHistoryDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeHistoryService -> getAllEmployeeHistories()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all active employee histories from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of EmployeeHistoryDTOs representing all active employee histories from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllActiveEmployeeHistories(String token) {
        try {
            List<EmployeeHistory> employeeHistories = getActiveEmployeeHistoriesFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(employeeHistories, EmployeeHistoryDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeHistoryService -> getAllActiveEmployeeHistories()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a employee history from the database based on the provided ID and maps it to a DTO before returning it in a ResponseEntity.
     *
     * @param id the ID of the employee history to be retrieved
     * @return a ResponseEntity containing a EmployeeHistoryDTO representing the employee history with the provided ID, or an error response if the employee history does not exist or an error occurs
     */
    public ResponseEntity<ResponseDTO> getEmployeeHistoryById(String id) {
        try {
            if (isEmployeeHistoryExistById(id)) {
                EmployeeHistory employeeHistory = getEmployeeHistoryFromDatabaseById(id);
                return mapperUtils.mapEntityToDTO(employeeHistory, EmployeeHistoryDTO.class);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeHistoryService -> getEmployeeHistoryById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a employee history from the database based on the provided ID and returns a ResponseEntity with details of the operation's success or failure.
     *
     * @param id the ID of the employee history to be deleted
     * @return a ResponseEntity indicating whether the deletion was successful or if an error occurred
     */
    public ResponseEntity<ResponseDTO> deleteEmployeeHistoryById(String id) {
        try {
            if (isEmployeeHistoryExistById(id)) {
                deleteEmployeeHistoryFromDatabase(id);
                return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeHistoryService -> deleteEmployeeHistoryById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Saves a employee history entity to the database.
     *
     * @param employeeHistory the employee history entity to be saved
     */
    public void saveEmployeeHistoryToDatabase(EmployeeHistory employeeHistory) {
        try {
            employeeHistoryRepository.save(employeeHistory);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeHistoryService -> saveEmployeeHistoryToDatabase()" + e);
        }
    }

    /**
     * Retrieves all employee histories from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of EmployeeHistory entities contain all employee histories from the database that belong to the same branch
     */
    public List<EmployeeHistory> getEmployeeHistoriesFromDatabase(String token) {
        try {
            String branchCode = userServiceClient.getByTokenForClient(token).getBranchCode();
            return employeeHistoryRepository.findAllByBranchCode(branchCode);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeHistoryService -> getEmployeeHistoriesFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves all active employee histories from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of EmployeeHistory entities contain all active employee histories from the database that belong to the same branch
     */
    public List<EmployeeHistory> getActiveEmployeeHistoriesFromDatabase(String token) {
        try {
            Predicate<EmployeeHistory> filterOnStatus = fy -> fy.getStatus().equals(Status.ACTIVE);
            return getEmployeeHistoriesFromDatabase(token).stream().filter(filterOnStatus).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeHistoryService -> getActiveEmployeeHistoriesFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a employee history entity from the database based on its ID.
     *
     * @param id the ID of the employee history entity to retrieve
     * @return a EmployeeHistory entity representing the requested employee history, or null if the ID is not valid or an error occurs
     */
    public EmployeeHistory getEmployeeHistoryFromDatabaseById(String id) {
        try {
            return employeeHistoryRepository.findById(Long.parseLong(id)).orElse(null);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EmployeeHistoryService -> getEmployeeHistoryFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a EmployeeHistory object from the database that matches the given employee history and branch code.
     *
     * @param employeeHistory a String representing the employee history to search for
     * @param branchCode   a String representing the branch code to search for
     * @return a EmployeeHistory object if one is found, or null if none is found or if an exception occurs during the search
     */
    public EmployeeHistory getEmployeeHistoryFromDatabaseByFinancialYearAndBranchCode(String employeeHistory, String branchCode) {
        try {
            return null;//employeeHistoryRepository.findByFinancialYearAndBranchCode(employeeHistory, branchCode);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EmployeeHistoryService -> getEmployeeHistoryFromDatabaseByFinancialYearAndBranchCode()" + e);
            return null;
        }
    }

    /**
     * Deletes a employee history from the database by its ID.
     *
     * @param id the ID of the employee history to be deleted
     */
    public void deleteEmployeeHistoryFromDatabase(String id) {
        try {
            employeeHistoryRepository.deleteById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EmployeeHistoryService -> deleteEmployeeHistoryFromDatabase()" + e);
        }
    }

    /**
     * Checks whether a employee history exists in the database by its ID.
     *
     * @param id the ID of the employee history to check for existence
     * @return true if a employee history with the specified ID exists in the database, false otherwise
     */
    public boolean isEmployeeHistoryExistById(String id) {
        try {
            return employeeHistoryRepository.existsById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EmployeeHistoryService -> isEmployeeHistoryExist()" + e);
            return false;
        }
    }

    /**
     * Validates the employee history data transfer object by checking if the required fields
     * are not empty and if the employee history already exists in the database. Returns a list
     * of validation error messages.
     *
     * @param employeeHistoryDTO the employee history data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the employee history is valid
     */
    private List<String> validateEmployeeHistory(EmployeeHistoryDTO employeeHistoryDTO) {
        List<String> validations = new ArrayList<>();
        // Check if employeeHistory fields
        validations.addAll(validateEmployeeHistoryFields(employeeHistoryDTO));
        return validations;
    }

    /**
     * Validates the employee history data transfer object by checking if the required fields
     * are not empty. Returns a list of validation error messages.
     *
     * @param employeeHistoryDTO the employee history data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the fields are valid
     */
    private List<String> validateEmployeeHistoryFields(EmployeeHistoryDTO employeeHistoryDTO) {
        List<String> validations = new ArrayList<>();
        if (ValidatorUtils.stringNullValidator.test(employeeHistoryDTO.getFinancialYearId())) {
            validations.add(EmployeeHistoryValidationMessages.EMPTY_FINANCIAL_YEAR);
        } else if (ValidatorUtils.stringNullValidator.test(employeeHistoryDTO.getEmployeeId())) {
            validations.add(EmployeeHistoryValidationMessages.EMPTY_EMPLOYEE);
        } else if (ValidatorUtils.stringNullValidator.test(employeeHistoryDTO.getCaption())) {
            validations.add(EmployeeHistoryValidationMessages.EMPTY_CAPTION);
        } else if (ValidatorUtils.stringNullValidator.test(employeeHistoryDTO.getReminder())) {
            validations.add(EmployeeHistoryValidationMessages.EMPTY_REMINDER);
        } else if (ValidatorUtils.statusValidator.test(employeeHistoryDTO.getStatus().toString())) {
            validations.add(CommonValidationMessages.EMPTY_STATUS);
        }
        return validations;
    }
}
